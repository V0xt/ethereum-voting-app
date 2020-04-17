package ethereumvotingapp.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ethereumvotingapp.Alerts;
import ethereumvotingapp.Ballot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import org.web3j.protocol.Web3j;

public class VotingCreationController {

    private int numberOfCandidates = 0;

    private TextField textFields[] = new TextField[5];

    @FXML
    private VBox candidatesVBox;

    @FXML
    private Button removeCandidate;

    @FXML
    private Button deployContract;

    @FXML
    private Label userAccount;

    @FXML
    void initialize() {
        userAccount.setText("Ваш аккаунт: " + LoginController.accountAddress);
    }

    @FXML
    void addTextField(ActionEvent event) {
        if (candidatesLimitReached()) {
            Alerts.showAlertDialog(Alerts.alertType.TOO_MANY_CANDIDATES, null);
            return;
        }

        textFields[numberOfCandidates] = new TextField();
        textFields[numberOfCandidates].setPromptText("Кандидат - " + (numberOfCandidates + 1));
        textFields[numberOfCandidates].setPrefSize(330, 25);

        candidatesVBox.getChildren().add(textFields[numberOfCandidates]);

        numberOfCandidates = numberOfCandidates + 1;

        deployContract.setDisable(false);
        removeCandidate.setDisable(false);
    }

    @FXML
    void removeTextField(ActionEvent event) {
        if (numberOfCandidates - 1 <= 0) {
            removeCandidate.setDisable(true);
            deployContract.setDisable(true);
        }

        numberOfCandidates = numberOfCandidates - 1;

        candidatesVBox.getChildren().remove(textFields[numberOfCandidates]);
    }

    @FXML
    void deploySmartContract(ActionEvent event) {
        if (containsEmptyFields()) return;

        List<byte[]> candidateNames = new ArrayList<>();
        fillByteArray(candidateNames);

        try {
            MainController.CONTRACT_ADDRESS = deployContract(LoginController.web3, candidateNames);
            //System.out.println(CONTRACT_ADDRESS);
            Alerts.showAlertDialog(Alerts.alertType.SUCCESSFUL_CONTRACT_CREATION, null);
        } catch (Exception ignored) {
        }
    }

    private boolean candidatesLimitReached() {
        return numberOfCandidates == 5;
    }

    private boolean containsEmptyFields() {
        for(int i = 0; i < numberOfCandidates; i += 1) {
            if (textFields[i].getText().isEmpty()) {
                Alerts.showAlertDialog(Alerts.alertType.EMPTY_FIELDS, null);
                return true;
            }
        }
        return false;
    }

    private void fillByteArray(List<byte[]> candidateNames) {
        for(int i = 0; i < numberOfCandidates; i += 1) {
            String candidateName = textFields[i].getText();
            String candidateHash = ASCIItoHEX(" " + candidateName);
            byte[] candidateByte = Arrays.copyOfRange(javax.xml.bind.DatatypeConverter.parseHexBinary(candidateHash), 1, 33);

            candidateNames.add(candidateByte);
        }
    }

    private static String ASCIItoHEX(String ascii) {
        String hex = "";
        for (int i = 0; i < ascii.length(); i++) {
            char ch = ascii.charAt(i);
            int in = (int)ch;
            String part = Integer.toHexString(in);
            hex += part;
        }
        return hex;
    }

    private String deployContract(Web3j web3, List<byte[]> candidateNames) throws Exception {
        return Ballot.deploy(web3, LoginController.transactionManager, MainController.GAS_PRICE, MainController.GAS_LIMIT, candidateNames)
                .send()
                .getContractAddress();
    }
}
