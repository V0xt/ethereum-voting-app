package ethereumvotingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage loginStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

        loginStage.setTitle("Вход в аккаунт");
        loginStage.setScene(new Scene(root, 600, 400));
        loginStage.setMaxHeight(700);
        loginStage.setMaxWidth(1200);
        loginStage.setMinHeight(450);
        loginStage.setMinWidth(450);

        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
