package renamer.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class FieldsValuesStorage {

    private static FieldsValuesStorage instance = new FieldsValuesStorage();

    //fields
    private TextField textFieldFileNameMask;
    private TextField textFieldFileExtMask;

    private Spinner<Integer> spinnerCounterStartTo;
    private Spinner<Integer> spinnerCounterStep;
    private Spinner<Integer> spinnerCounterDigits;

    private ObservableList<String> comboBoxRegisterList = FXCollections.observableArrayList("Без изменений", "БОЛЬШИЕ БУКВЫ", "маленькие буквы");


    public void setDefaultValuesFields() {
        textFieldFileNameMask.setText("[N]");
        textFieldFileExtMask.setText("[T]");
    }

    //очистка полей
    public void fieldsSetNull() {
        textFieldFileNameMask = null;
        textFieldFileExtMask = null;
        spinnerCounterStartTo = null;
        spinnerCounterStep = null;
        spinnerCounterDigits = null;
    }


    //setters and getters
    public static FieldsValuesStorage getInstance() {return instance;}

    public TextField getTextFieldFileNameMask() {return textFieldFileNameMask;}
    public void setTextFieldFileNameMask(TextField textFieldFileNameMask) {this.textFieldFileNameMask = textFieldFileNameMask;}

    public TextField getTextFieldFileExtMask() {return textFieldFileExtMask;}
    public void setTextFieldFileExtMask(TextField textFieldFileExtMask) {this.textFieldFileExtMask = textFieldFileExtMask;}

    public Spinner<Integer> getSpinnerCounterStartTo() {return spinnerCounterStartTo;}
    public void setSpinnerCounterStartTo(Spinner<Integer> spinnerCounterStartTo) {this.spinnerCounterStartTo = spinnerCounterStartTo;}

    public Spinner<Integer> getSpinnerCounterStep() {return spinnerCounterStep;}
    public void setSpinnerCounterStep(Spinner<Integer> spinnerCounterStep) {this.spinnerCounterStep = spinnerCounterStep;}

    public Spinner<Integer> getSpinnerCounterDigits() {return spinnerCounterDigits;}
    public void setSpinnerCounterDigits(Spinner<Integer> spinnerCounterDigits) {this.spinnerCounterDigits = spinnerCounterDigits;}

    public ObservableList<String> getComboBoxRegisterList() {return comboBoxRegisterList;}
    public void setComboBoxRegisterList(ObservableList<String> comboBoxRegisterList) {this.comboBoxRegisterList = comboBoxRegisterList;}
}
