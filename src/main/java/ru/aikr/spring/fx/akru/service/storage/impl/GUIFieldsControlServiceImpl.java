package ru.aikr.spring.fx.akru.service.storage.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.aikr.spring.fx.akru.service.storage.GUIFieldsControlService;

@Service
@Setter
@Getter
public class GUIFieldsControlServiceImpl implements GUIFieldsControlService {

    private TextField textFieldFileNameMask = new TextField("[N]");
    private TextField textFieldFileExtMask = new TextField("[T]");

    private Spinner<Integer> spinnerCounterStartTo;
    private Spinner<Integer> spinnerCounterStep;
    private Spinner<Integer> spinnerCounterDigits;

    private ObservableList<String> comboBoxRegisterList = FXCollections
            .observableArrayList("Без изменений", "БОЛЬШИЕ БУКВЫ", "маленькие буквы");

    //установка значений полей по умолчанию
    public void setFieldToDefaultValues() {
        textFieldFileNameMask.setText("[N]");
        textFieldFileExtMask.setText("[T]");
    }

    //для EXIF полей
    public String setOfValuesValue() {
        return "множество значений";
    }

}
