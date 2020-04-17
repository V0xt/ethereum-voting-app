package ethereumvotingapp;

import ethereumvotingapp.controllers.MainController;
import javafx.scene.control.Alert;

public class Alerts {

    public enum alertType {
        TOO_MANY_CANDIDATES,
        EMPTY_FIELDS,
        SUCCESSFUL_CONTRACT_CREATION,
        INCORRECT_ADDRESS_FORMAT,
        SUCCESSFUL_VOTER_ADDING,
        VOTING_EXCEPTION,
        SUCCESSFUL_VOTING,
        VOTER_ADDING_EXCEPTION,
        LOGIN_ERROR
    }

    public static void showAlertDialog(alertType type, String optionalMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание!");
        alert.setHeaderText(null);

        switch(type) {
            case TOO_MANY_CANDIDATES:
                alert.setContentText("Извините, кандидатов не может быть больше 5.");
                break;
            case EMPTY_FIELDS:
                alert.setContentText("Заполнены не все поля.");
                break;
            case SUCCESSFUL_CONTRACT_CREATION:
                alert.setContentText("Голосование успешно создано. Не забудьте обновить таблицу!\nАдрес контракта: " + MainController.CONTRACT_ADDRESS);
                break;
            case INCORRECT_ADDRESS_FORMAT:
                alert.setContentText("Неверный формат адреса. Пример:\n0x528832444fA9e37782E087Bccd464240Cca20952");
                break;
            case SUCCESSFUL_VOTER_ADDING:
                alert.setContentText("Адрес успешно получил право голоса. Хэш транзакции: \n" + optionalMessage);
                break;
            case VOTER_ADDING_EXCEPTION:
                alert.setContentText("Ошибка при добавлении избирателя.\n" + optionalMessage);
                break;
            case SUCCESSFUL_VOTING:
                alert.setContentText("Ваш голос засчитан. Хэш транзакции: \n" + optionalMessage);
                break;
            case VOTING_EXCEPTION:
                alert.setContentText("Ошибка при голосовании.\n" + optionalMessage);
                break;
            case LOGIN_ERROR:
                alert.setContentText("Ошибка при входе, проверьте ваш приватный ключ.\n" + optionalMessage);
                break;
            default:
                alert.setContentText("Неизвестная ошибка.\n" + optionalMessage);
                break;
        }

        alert.showAndWait();
    }

}
