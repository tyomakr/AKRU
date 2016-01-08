package renamer.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import renamer.model.FileItem;

public interface RenamerController {

    ObservableList<FileItem> getFileItemsList();

    TextField getTextFieldFileNameMask();
    TextField getTextFieldFileExtMask();

    Spinner<Integer> getSpinnerCounterStartTo();
    Spinner<Integer> getSpinnerCounterStep();
    Spinner<Integer> getSpinnerCounterDigits();

    ObservableList<String> getComboBoxRegisterList();
}
