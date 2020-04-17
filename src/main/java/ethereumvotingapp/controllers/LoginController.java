package ethereumvotingapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

public class LoginController {

    private static final String NODE_ENDPOINT = "HTTP://127.0.0.1:7545";
    private static Credentials credentials;

    static Web3j web3;
    static String accountAddress;
    static TransactionManager transactionManager;

    @FXML
    private TextField pKeyField;

    @FXML
    void initialize() {
        web3 = connectToNode();
    }

    private Web3j connectToNode() {
        return Web3j.build(new HttpService(NODE_ENDPOINT));
    }

    @FXML
    void accessAccount(ActionEvent event) {
        credentials = createCredentialsFromPrivateKey();
        accountAddress = getAccountAddress();
        transactionManager = setupRawTransactionManager();

        openMainForm();
    }

    private Credentials createCredentialsFromPrivateKey() {
        String privateKey = pKeyField.getText();
        return Credentials.create(privateKey);
    }

    private String getAccountAddress() {
        return credentials.getAddress();
    }

    private TransactionManager setupRawTransactionManager() {
        return new RawTransactionManager(web3, credentials);
    }

    private void openMainForm() {
        Stage currentStage = getLoginStage();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));

        try {
            Parent root = fxmlLoader.load();

            Stage mainStage = new Stage();
            mainStage.setTitle("Главное окно");
            mainStage.setScene(new Scene(root, 800, 600));
            mainStage.setMaxHeight(700);
            mainStage.setMaxWidth(1200);
            mainStage.setMinHeight(450);
            mainStage.setMinWidth(450);

            mainStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Stage getLoginStage() {
        Scene currentScene = pKeyField.getScene();
        return (Stage) currentScene.getWindow();
    }
}
