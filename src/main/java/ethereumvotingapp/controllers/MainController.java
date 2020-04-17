package ethereumvotingapp.controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ethereumvotingapp.Alerts;
import ethereumvotingapp.Ballot;
import ethereumvotingapp.VotingTable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.web3j.crypto.Hash;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Numeric;

import org.apache.commons.codec.binary.StringUtils;

public class MainController {

    private static final Pattern ignoreCaseAddrPattern = Pattern.compile("(?i)^(0x)?[0-9a-f]{40}$");
    private static final Pattern lowerCaseAddrPattern = Pattern.compile("^(0x)?[0-9a-f]{40}$");
    private static final Pattern upperCaseAddrPattern = Pattern.compile("^(0x)?[0-9A-F]{40}$");

    private static TransactionReceipt transactionReceipt;

    final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    static Ballot ballot;
    public static String CONTRACT_ADDRESS = "0xcFAE635Fd4a6828Ed6788B73D4e4Dcd810525fbF";

    ObservableList<VotingTable> electionsList = FXCollections.observableArrayList();

    @FXML
    private TableView<VotingTable> electionsTable;

    @FXML
    private TableColumn<VotingTable, Integer> id;

    @FXML
    private TableColumn<VotingTable, String> name;

    @FXML
    private TableColumn<VotingTable, Integer> votes;

    @FXML
    private TextField voterTextField;

    @FXML
    private Label userAccount;

    @FXML
    private ComboBox<String> candidatesBox;

    @FXML
    void initialize() throws Exception {
        userAccount.setText("Ваш аккаунт: " + LoginController.accountAddress);
        ballot = loadContract(CONTRACT_ADDRESS);

        loadCandidates();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        votes.setCellValueFactory(new PropertyValueFactory<>("votes"));
    }

    private void setupContract() throws Exception {
        ballot = loadContract(CONTRACT_ADDRESS);
        loadCandidates();
    }

    @FXML
    public void showCreateElectionsDialog(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/VotingCreation.fxml"));
            Parent root = fxmlLoader.load();

            Stage createElectionsStage = new Stage();
            createElectionsStage.setTitle("Создание голосования");
            createElectionsStage.setScene(new Scene(root));
            createElectionsStage.setMinHeight(450);
            createElectionsStage.setMinWidth(450);
            createElectionsStage.setMaxHeight(500);
            createElectionsStage.setMaxWidth(650);

            createElectionsStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void showWinnerForm(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/VotingResults.fxml"));
            Parent root = fxmlLoader.load();

            Stage electionsResultStage = new Stage();
            electionsResultStage.setTitle("Результаты голосования");
            electionsResultStage.setScene(new Scene(root));
            electionsResultStage.setMinHeight(450);
            electionsResultStage.setMinWidth(450);
            electionsResultStage.setMaxHeight(500);
            electionsResultStage.setMaxWidth(650);

            electionsResultStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void changeAccount(ActionEvent event) {
        Stage currentStage = (Stage) userAccount.getScene().getWindow();
        currentStage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = fxmlLoader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Вход в аккаунт");
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.setMaxHeight(700);
            loginStage.setMaxWidth(1200);
            loginStage.setMinHeight(450);
            loginStage.setMinWidth(450);

            loginStage.show();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void vote(ActionEvent event) {
        BigInteger selectedCandidateId = BigInteger.valueOf(candidatesBox.getSelectionModel().getSelectedIndex());

        try {
            transactionReceipt = ballot.vote(selectedCandidateId).send();
            String txHash = transactionReceipt.getTransactionHash();
            Alerts.showAlertDialog(Alerts.alertType.SUCCESSFUL_VOTING, txHash);
            setupContract();
        } catch (Exception ex) {
            Alerts.showAlertDialog(Alerts.alertType.VOTING_EXCEPTION, ex.getMessage());
        }
    }

    @FXML
    void updateTable(ActionEvent event) {
        try {
            initialize();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void giveRightToVote(ActionEvent event) {
        String voterAddress = voterTextField.getText();

        if (!isAddressCorrect(voterAddress)) {
            return;
        }

        try {
            transactionReceipt = ballot.giveRightToVote(voterAddress).send();
            String txHash = transactionReceipt.getTransactionHash();
            Alerts.showAlertDialog(Alerts.alertType.SUCCESSFUL_VOTER_ADDING, txHash);
            setupContract();
        }
        catch (Exception ex) {
            Alerts.showAlertDialog(Alerts.alertType.VOTER_ADDING_EXCEPTION, ex.getMessage());
        }

    }

    private boolean isAddressCorrect(String address) {
        if (address.length() < 2 || !isAddress(address.substring(2))) {
            Alerts.showAlertDialog(Alerts.alertType.INCORRECT_ADDRESS_FORMAT, null);
            return false;
        }
        return true;
    }

    private static Boolean isAddress(String address) {
        /*
         * check basic address requirements, i.e. is not empty and contains
         * the valid number and type of characters
         */
        if (address.isEmpty() || !ignoreCaseAddrPattern.matcher(address).find()) {
            return false;
        } else if (lowerCaseAddrPattern.matcher(address).find()
                || upperCaseAddrPattern.matcher(address).find()) {
            // if it's all small caps or caps return true
            return true;
        } else {
            // if it is mixed caps it is a checksum address and needs to be validated
            return validateChecksumAddress(address);
        }
    }

    private static Boolean validateChecksumAddress(String address) {
        String formattedAddress = address.replace("0x", "").toLowerCase();
        String hash = Numeric.toHexStringNoPrefix(Hash.sha3(formattedAddress.getBytes()));
        for (int i = 0; i < 40; i++) {
            if (Character.isLetter(address.charAt(i))) {
                // each uppercase letter should correlate with a first bit of 1 in the hash
                // char with the same index, and each lowercase letter with a 0 bit
                int charInt = Integer.parseInt(Character.toString(hash.charAt(i)), 16);
                if ((Character.isUpperCase(address.charAt(i)) && charInt <= 7)
                        || (Character.isLowerCase(address.charAt(i)) && charInt > 7)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void loadCandidates() throws Exception{
        int numberOfCandidates = getNumberOfCandidates();

        List<Tuple2<byte[], BigInteger>> proposals = new ArrayList<>();

        candidatesBox.getItems().clear();
        electionsTable.getItems().clear();

        for(int i = 0; i < numberOfCandidates; i++) {
            BigInteger bigNumberOfProposals = BigInteger.valueOf(i);
            proposals.add(ballot.getProposal(bigNumberOfProposals).send());

            String proposalName = StringUtils.newStringUsAscii(proposals.get(i).component1());

            candidatesBox.getItems().add(proposalName);
        }
        candidatesBox.getSelectionModel().select(0);

        int index = 0;
        while (index < proposals.size()) {
            String proposalName = StringUtils.newStringUsAscii(proposals.get(index).component1());
            Integer proposalVotes = proposals.get(index).component2().intValue();

            electionsList.add(new VotingTable(index, proposalName, proposalVotes));

            index += 1;
        }
        electionsTable.setItems(electionsList);
    }

    private Integer getNumberOfCandidates() throws Exception{
        return ballot.getProposalsCount()
                .send()
                .intValue();
    }

    private Ballot loadContract(String contractAddress) {
        return Ballot.load(contractAddress, LoginController.web3, LoginController.transactionManager, GAS_PRICE, GAS_LIMIT);
    }
}
