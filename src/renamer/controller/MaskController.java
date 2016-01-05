package renamer.controller;

import renamer.model.FileItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class MaskController {

    //доступ к данным из вызывавшего этот контроллер
    private RenamerController renamerController;
    public void setRenamerController(RenamerController renamerController) {
        this.renamerController = renamerController;
    }

    //чтение маски и превью
    public void applyMasks() {

        for (FileItem fileItem : renamerController.getFileItemsList()) {

            //Получаем полное имя файла
            String oldFileName = fileItem.getOldFileName();
            //получаем для шаблонов файл
            File file = fileItem.getFile();

            //Если файл не содержит точку в названии (файлы с именеи без расширения)
            if (!oldFileName.contains(".")) {
                //подменияем символы, согласно шаблонов масок
                String newFileName = fileNameTemplates(file, oldFileName);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName);
            }

            //Если полное имя файла начинается с точки (файлы только с расширением /nix системы)
            else if (oldFileName.startsWith(".")) {
                //ничего не делаем, записываем новое имя файла таким-же
                fileItem.setNewFileName(oldFileName);
            }

            //В обычных случаях, когда имя и расширение присутствуют
            else {

                //отрезаем расширение файла
                oldFileName = oldFileName.substring(0, oldFileName.lastIndexOf("."));
                //подменияем символы, согласно шаблонов масок
                String newFileName = fileNameTemplates(file, oldFileName);
                //получаем расширение файла
                int extPosition = fileItem.getOldFileName().lastIndexOf(".") + 1;
                String extension = fileItem.getOldFileName().substring(extPosition);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName + "." + extension);
            }
        }
    }

    //Шаблоны для замены по маске
    private String fileNameTemplates(File file, String oldFileName) {

        //получаем маски с textField
        String maskFileName = renamerController.getTextFieldFileNameMask().getText();


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

        if (newFileName.contains("[C]")) {
            //TODO
        }

        return newFileName;
    }

}
