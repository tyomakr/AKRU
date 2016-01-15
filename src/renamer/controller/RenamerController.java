package renamer.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import renamer.model.FileItem;

public interface RenamerController {

    TextField getTextFieldFileNameMask();
    TextField getTextFieldFileExtMask();

    Spinner<Integer> getSpinnerCounterStartTo();
    Spinner<Integer> getSpinnerCounterStep();
    Spinner<Integer> getSpinnerCounterDigits();

    ObservableList<String> getComboBoxRegisterList();
}
