package com.example;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerController implements Initializable {
    @FXML
    private Label nameLabel;

    @FXML
    private Slider positionSlider;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button closeButton;

    private FolderStore folderStore = new FolderStore();
    private ObservableValue<Boolean> isEnabled = Bindings.isNotNull(folderStore.getCurrentFile());

    private ObservableValue<String> fileName = folderStore.getCurrentFile().map(t -> {
        return t == null ? "" : t.getName();
    });

    public ObservableValue<Boolean> getIsEnabled() {
        return isEnabled;
    }

    private DoubleProperty baseVolume = new SimpleDoubleProperty(1);

    public DoubleProperty baseVolumeProperty() {
        return baseVolume;
    }

    private DoubleBinding volume;

    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        positionSlider.valueProperty().addListener((o1, o2, newPositionMillis) -> {
            if (mediaPlayer == null)
                return;

            var positionMillis = newPositionMillis.doubleValue();
            var diff = Math.abs(mediaPlayer.getCurrentTime().toMillis() - positionMillis);
            if (diff < 500)
                return;

            mediaPlayer.seek(new Duration(positionMillis));
        });

        nameLabel.textProperty().bind(fileName);

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);

        volume = volumeSlider.valueProperty().multiply(baseVolume);
        volume.addListener((o1, o2, newVolume) -> {
            if (mediaPlayer == null)
                return;

            var volume = newVolume.doubleValue();
            mediaPlayer.setVolume(volume);
        });

        folderStore.getCurrentFile().addListener((o, v0, newFile) -> {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            if (newFile == null) {
                return;
            }

            media = new Media(folderStore.getCurrentFile().getValue().toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(1);
            mediaPlayer.setVolume(volume.getValue().doubleValue());

            mediaPlayer.setOnReady(() -> {
                positionSlider.setMin(0);
                positionSlider.setMax(mediaPlayer.getTotalDuration().toMillis());
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, duration) -> {
                positionSlider.setValue(duration.toMillis());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                folderStore.nextFile();
            });
            mediaPlayer.play();
        });
    }

    @FXML
    private void onPlayButtonAction(ActionEvent a) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    private void onPauseButtonAction(ActionEvent a) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private void onPrevButtonAction(ActionEvent a) {
        if (mediaPlayer != null) {
            folderStore.prevFile();
        }
    }

    @FXML
    private void onNextButtonAction(ActionEvent a) {
        if (mediaPlayer != null) {
            folderStore.nextFile();
        }
    }

    @FXML
    private void onCloseButtonAction(ActionEvent a) {
        if (mediaPlayer != null) {
            folderStore.clear();
        }
    }

    public void loadFile(File file) {
        folderStore.loadFile(file);
    }

    public void clear() {
        folderStore.clear();
    }
}
