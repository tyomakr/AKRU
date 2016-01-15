package renamer.controller.preparing;

import javafx.collections.ObservableList;
import renamer.model.FileItem;
import renamer.storage.FieldsValuesStorage;
import renamer.storage.FileItemsStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class MaskController {

    //доступ к списку файлов
    private ObservableList<FileItem> fileItemsList = FileItemsStorage.getInstance().getFileItemsList();

    //чтение маски и превью
    public void applyMasks() {

        for (int index = 0; index < fileItemsList.size(); index++) {
            FileItem fileItem = fileItemsList.get(index);

            //Получаем полное имя файла
            String oldFileName = fileItem.getOldFileName();
            //получаем для шаблонов файл
            File file = fileItem.getFile();

            //Если файл не содержит точку в названии (файлы с именеи без расширения)
            if (!oldFileName.contains(".")) {
                //подменяем символы, согласно шаблонов масок
                String newFileName = fileNameTemplates(file, oldFileName, index);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName);
            }

            //В обычных случаях, когда имя и расширение присутствуют
            else {
                //отрезаем расширение файла
                oldFileName = oldFileName.substring(0, oldFileName.lastIndexOf("."));
                //подменяем символы, согласно шаблонов масок
                String newFileName = fileNameTemplates(file, oldFileName, index);
                //получаем расширение файла
                int extPosition = fileItem.getOldFileName().lastIndexOf(".") + 1;
                String extension = fileItem.getOldFileName().substring(extPosition);
                //подменяем символы расширения файла, согласно шаблону маски
                extension = fileExtTemplates(extension, index);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName + "." + extension);
            }
        }
    }

    //Шаблоны для замены имени файла по маске
    private String fileNameTemplates(File file, String oldFileName, int index) {

        //получаем маски с textField
        String maskFileName = FieldsValuesStorage.getInstance().getTextFieldFileNameMask().getText();

        //аттрибуты файла
        //дата
        String fileDate = "";
        //время
        String fileTime = "";
        try {
            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            fileDate = attributes.creationTime().toString().substring(0, 10);
            fileTime = attributes.creationTime().toString().substring(11, 19).replaceAll(":", "-");

        } catch (IOException e) {e.printStackTrace();}

        //Применяем шаблоны масок
        String newFileName;
        newFileName = maskFileName.replaceAll("\\[N\\]", oldFileName);
        newFileName = newFileName.replaceAll("\\[YMD\\]", fileDate);
        newFileName = newFileName.replaceAll("\\[hms\\]", fileTime);

        //применяем по надобности счетчик
        if (newFileName.contains("[C]")) {
            newFileName = applyCounter(newFileName, index);
        }
        return newFileName;
    }

    //Шаблоны для замены расширения файла по маске
    private String fileExtTemplates(String extension, int index) {

        //получаем маски с textField
        String maskExtension = FieldsValuesStorage.getInstance().getTextFieldFileExtMask().getText();

        String newExtension;
        //Тип файла по умолчанию
        newExtension = maskExtension.replaceAll("\\[T\\]", extension);

        //применяем по надобности счетчик
        if (newExtension.contains("[C]")) {
            newExtension = applyCounter(newExtension, index);
        }
        return newExtension;
    }

    //Применить счетчик
    private String applyCounter(String fileStr, int index) {

        int start = FieldsValuesStorage.getInstance().getSpinnerCounterStartTo().getValue();
        int step = FieldsValuesStorage.getInstance().getSpinnerCounterStep().getValue();
        int digits = FieldsValuesStorage.getInstance().getSpinnerCounterDigits().getValue();

        //вычисляем значение счетчика для конкретного файла
        int calculatedCount = start + (step * index);

        //работаем с дополнительными нулями в счетчике
        String finalCount = String.format("%0" + digits + "d", calculatedCount);

        fileStr = fileStr.replaceAll("\\[C\\]", finalCount);

        return fileStr;
    }

    //Регистр
    public void applyRegister(String value) {

        for (int index = 0; index < fileItemsList.size(); index++) {
            FileItem fileItem = fileItemsList.get(index);

            if (value.equals(FieldsValuesStorage.getInstance().getComboBoxRegisterList().get(1))) {
                fileItem.setNewFileName(fileItem.getNewFileName().toUpperCase());
            }
            else if (value.equals(FieldsValuesStorage.getInstance().getComboBoxRegisterList().get(2))) {
                fileItem.setNewFileName(fileItem.getNewFileName().toLowerCase());
            }
        }
    }
}