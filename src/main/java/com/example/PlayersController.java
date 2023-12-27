package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;

public class PlayersController implements Initializable {
    @FXML
    private VBox parentPane;

    @FXML
    private Button openWavButton0;

    @FXML
    private Button openWavButton1;

    @FXML
    private Slider mixSlider;

    private Pane player0;
    private PlayerController player0Controller;
    private Pane player1;
    private PlayerController player1Controller;

    private FileChooser fileChooser = new FileChooser();

    @FXML
    private void openWav(ActionEvent event) {
        var button = (Button) event.getSource();
        var controller = button == openWavButton0 ? player0Controller : player1Controller;

        var file = fileChooser.showOpenDialog(parentPane.getScene().getWindow());
        if (file == null) return;

        controller.loadFile(file);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPlayer0();
        player0.visibleProperty().bind(player0Controller.getIsEnabled());
        player0.managedProperty().bind(player0Controller.getIsEnabled());

        var player0Disabled = player0Controller.getIsEnabled().map(t -> !t);
        openWavButton0.visibleProperty().bind(player0Disabled);
        openWavButton0.managedProperty().bind(player0Disabled);

        var baseVolume0 = mixSlider.valueProperty().map(t -> Math.clamp(2 - t.doubleValue(), 0, 1));
        player0Controller.baseVolumeProperty().bind(baseVolume0);

        loadPlayer1();
        player1.visibleProperty().bind(player1Controller.getIsEnabled());
        player1.managedProperty().bind(player1Controller.getIsEnabled());

        var player1Disabled = player1Controller.getIsEnabled().map(t -> !t);
        openWavButton1.visibleProperty().bind(player1Disabled);
        openWavButton1.managedProperty().bind(player1Disabled);

        var baseVolume1 = mixSlider.valueProperty().map(t -> Math.clamp(t.doubleValue(), 0, 1));
        player1Controller.baseVolumeProperty().bind(baseVolume1);
    }

    private void loadPlayer0() {
        var pair = loadPlayer();
        player0 = pair.getKey();
        player0Controller = pair.getValue();
        addAfter(openWavButton0, player0);
    }

    private void loadPlayer1() {
        var pair = loadPlayer();
        player1 = pair.getKey();
        player1Controller = pair.getValue();
        player1.setVisible(false);
        player1.setManaged(false);
        addAfter(openWavButton1, player1);
    }

    private void addAfter(Node node0, Node node1) {
        var index = parentPane.getChildren().indexOf(node0);
        parentPane.getChildren().add(index + 1, node1);
    }

    private static Pair<Pane, PlayerController> loadPlayer() {
        var loader = new FXMLLoader(PlayerController.class.getResource("player.fxml"));
        try {
            return new Pair<Pane, PlayerController>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadFile(File file) {
        player0Controller.loadFile(file);
    }

    public void clear() {
        player0Controller.clear();
        player1Controller.clear();
    }
}
