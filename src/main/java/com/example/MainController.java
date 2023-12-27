package com.example;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML private Pane parentPane;
    @FXML private Button openWavButton;

    private FileChooser fileChooser = new FileChooser();

    @FXML
    private void openWav() {
        var file = fileChooser.showOpenDialog(parentPane.getScene().getWindow());
        if (file == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("players.fxml"));

        try {
            var window = new Stage();
            window.setTitle("Player");
            window.setScene(new Scene(loader.load()));
            PlayersController controller = loader.getController();
            controller.loadFile(file);
            window.setOnHidden(event -> {
                controller.clear();
            });
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        var wavExtensionFilter = new FileChooser.ExtensionFilter("Wav audio", new String[] { "*.wav", "*.wave" });
        fileChooser.getExtensionFilters().add(wavExtensionFilter);
    }
}
