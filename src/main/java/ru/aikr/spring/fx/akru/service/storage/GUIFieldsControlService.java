package ru.aikr.spring.fx.akru.service.storage;

import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public interface GUIFieldsControlService {

    void setFieldToDefaultValues();
    String setOfValuesValue();

    ObservableList<String> getComboBoxRegisterList();
    void setComboBoxRegisterList(ObservableList<String> comboBoxRegisterList);

    void setTextFieldFileNameMask(TextField tf);
    void setTextFieldFileExtMask(TextField tf);
    TextField getTextFieldFileNameMask();
    TextField getTextFieldFileExtMask();

    Spinner<Integer> getSpinnerCounterStartTo();
    Spinner<Integer> getSpinnerCounterStep();
    Spinner<Integer> getSpinnerCounterDigits();
    void setSpinnerCounterStartTo(Spinner<Integer> spinner);
    void setSpinnerCounterStep(Spinner<Integer> spinner);
    void setSpinnerCounterDigits(Spinner<Integer> spinner);
}