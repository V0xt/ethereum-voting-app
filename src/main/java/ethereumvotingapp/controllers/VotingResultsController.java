package ethereumvotingapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import org.web3j.utils.Numeric;

public class VotingResultsController {

    @FXML
    private BarChart<String, Number> candidatesChart;

    @FXML
    private Label winningCandidate;

    @FXML
    private Label contractAddress;

    @FXML
    void initialize() {
        contractAddress.setText("Адрес контракта: " + MainController.CONTRACT_ADDRESS);

        String winnerName = getWinnerName();
        winningCandidate.setText("Лидирующий кандидат: " + winnerName);

        fillBarChart();
    }

    private void fillBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Кандидаты");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Голоса");

        XYChart.Series<String, Number> electionsData = new XYChart.Series<>();
        electionsData.setName("Данные голосования");

        fillDataSeriesFromElectionsList(electionsData);

        candidatesChart.getData().add(electionsData);
    }

    private void fillDataSeriesFromElectionsList(XYChart.Series<String, Number> dataSeries) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            loader.load();
            MainController mainController = loader.getController();

            for(int i = 0; i < mainController.electionsList.size(); i ++) {
                String candidateName = mainController.electionsList.get(i).getName();
                Number votes = mainController.electionsList.get(i).getVotes();

                dataSeries.getData().add(new XYChart.Data<>(candidateName, votes));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getWinnerName() {
        byte[] winnerNameByteArray;
        String winnerNameString;

        winnerNameByteArray = getWinnerNameBytes();

        assert winnerNameByteArray != null;
        winnerNameString = hexToASCII(Numeric.toHexStringNoPrefix(winnerNameByteArray));

        return winnerNameString;
    }

    private byte[] getWinnerNameBytes() {
        try {
            return MainController.ballot.winnerName().send();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}
