package com.example;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.stream.IntStream;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FolderStore {
    private final ObservableList<File> files = FXCollections.observableArrayList();
    private final SimpleIntegerProperty currentIndex = new SimpleIntegerProperty(0);
    private final ObservableObjectValue<File> currentFile = Bindings.valueAt(files, currentIndex);

    private final SimpleBooleanProperty update = new SimpleBooleanProperty(true);
    private final ObservableObjectValue<File> currentFileHoldable = (ObservableObjectValue<File>) currentFile.when(update);

    public ObservableObjectValue<File> getCurrentFile() {
        return currentFileHoldable;
    }

    public FolderStore() {
    }

    private static FileFilter wavExtensionFilter = file -> {
        if (!file.isFile()) {
            return false;
        }
        var name = file.getName();
        return name.endsWith(".wav") || name.endsWith(".wave");
    };

    public void loadFile(File file) {

        update.set(false);

        var parentDir = new File(file.getParent());
        var wavArray = parentDir.listFiles(wavExtensionFilter);

        var wavList = Arrays.stream(wavArray).sorted().toList();
        files.setAll(wavList);

        int currentWavIndex = IntStream.range(0, wavList.size())
                .filter(i -> {
                    var fileFromList = wavList.get(i);
                    return file.compareTo(fileFromList) == 0;
                })
                .findFirst()
                .getAsInt();

        currentIndex.set(currentWavIndex);

        update.set(true);
    }

    public void prevFile() {
        update.setValue(false);
        currentIndex.set((currentIndex.get() + files.size() - 1) % files.size());
        update.setValue(true);
    }

    public void nextFile() {
        update.setValue(false);
        currentIndex.set((currentIndex.get() + 1) % files.size());
        update.setValue(true);
    }

    public void clear() {
        update.setValue(false);
        files.clear();
        currentIndex.set(0);
        update.setValue(true);
    }
}
