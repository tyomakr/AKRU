package ru.aikr.spring.fx.akru.service.preparing.impl;

import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.service.preparing.MaskHandlerService;
import ru.aikr.spring.fx.akru.service.preparing.MetadataHandlerService;
import ru.aikr.spring.fx.akru.service.storage.GUIFieldsControlService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaskHandlerServiceImpl implements MaskHandlerService {

    private final FileItemsStorageService fileItemsStorageService;
    private final GUIFieldsControlService GUIFieldsControlService;
    private final MetadataHandlerService metadataHandlerService;

    private ObservableList<FileItemEntity> fileItemsList;

    public void applyMasks() {                                                                                          //чтение маски и превью
        fileItemsList = fileItemsStorageService.getFileItemEntitiesList();

        for (int index = 0; index < fileItemsList.size(); index++) {
            FileItemEntity fileItem = fileItemsList.get(index);

            //Получаем полное имя файла
            String oldFileName = fileItem.getOldFileName();

            //Если файл не содержит точку в названии (файлы с именем без расширения)
            if (!oldFileName.contains(".")) {
                //подменяем символы, согласно шаблонам масок
                String newFileName = applyFileNameTemplates(fileItem, index);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName);
            }

            //В обычных случаях, когда имя и расширение присутствуют
            else {
                //подменяем символы, согласно шаблонам масок
                String newFileName = applyFileNameTemplates(fileItem, index);
                //получаем расширение файла
                int extPosition = fileItem.getOldFileName().lastIndexOf(".") + 1;
                String extension = fileItem.getOldFileName().substring(extPosition);
                //подменяем символы расширения файла, согласно шаблону маски
                extension = fileExtTemplates(extension, index);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName + "." + extension);
            }

            fileItemsStorageService.saveFileItemEntity(fileItem);
        }
    }

                                                                                                                        //Шаблоны для замены имени файла по маске
    private String applyFileNameTemplates(FileItemEntity fileItem, int index) {

        //получаем маски из поля textField
        String maskFileName = GUIFieldsControlService.getTextFieldFileNameMask().getText();

        //применяем шаблоны масок
        String newFileName;

        //первоначальное старое имя
        String oldFileName = fileItem.getOldFileName();
        newFileName = maskFileName.replaceAll("\\[N]", oldFileName.substring(0, oldFileName.lastIndexOf(".")));


        //дата и время (создания файла)
        if (newFileName.contains("[YMD]") || newFileName.contains("[hms]")) {

            String fileDate = "";
            String fileTime = "";
            try {
                BasicFileAttributes attributes = Files.readAttributes(Path.of(fileItem.getFilePath()), BasicFileAttributes.class);
                fileDate = attributes.creationTime().toString().substring(0, 10);
                fileTime = attributes.creationTime().toString().substring(11, 19).replaceAll(":", "-");

            } catch (IOException e) {e.printStackTrace();}

            newFileName = newFileName.replaceAll("\\[YMD]", fileDate);
            newFileName = newFileName.replaceAll("\\[hms]", fileTime);
        }

        //счетчик
        if (newFileName.contains("[C]")) {
            newFileName = applyCounter(newFileName, index);
        }


        /* ДЛЯ EXIF и прочих метаданных */
        if (newFileName.contains("[EXIF_")) {
            //читаем метаданные текущего файла и записываем их в metadataStorage
            metadataHandlerService.readMetadata(fileItemsList.get(index));

            if (newFileName.contains("[EXIF_D]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_D]", fileItemsList.get(index).getMetadataEntity().getEXIFDate());
            }
            if (newFileName.contains("[EXIF_T]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_T]", fileItemsList.get(index).getMetadataEntity().getEXIFTime());
            }

            if (newFileName.contains("[EXIF_Author]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_Author]", fileItemsList.get(index).getMetadataEntity().getAuthor());
            }
            if (newFileName.contains("[EXIF_Camera]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_Camera]", fileItemsList.get(index).getMetadataEntity().getCameraModel());
            }
            if (newFileName.contains("[EXIF_FL]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_FL]", fileItemsList.get(index).getMetadataEntity().getFocalLength());
            }
            if (newFileName.contains("[EXIF_W]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_W]", fileItemsList.get(index).getMetadataEntity().getImageWidth());
            }
            if (newFileName.contains("[EXIF_H]")) {
                newFileName = newFileName.replaceAll("\\[EXIF_H]", fileItemsList.get(index).getMetadataEntity().getImageHeight());
            }
            if (newFileName.contains("[EXIF_SOFT")) {
                newFileName = newFileName.replaceAll("\\[EXIF_SOFT]", fileItemsList.get(index).getMetadataEntity().getSoftware());
            }

        }
        return newFileName;
    }


                                                                                                                        //Шаблоны для замены расширения файла по маске
    private String fileExtTemplates(String extension, int index) {

        //получаем маски из TextField ext
        String maskExtension = GUIFieldsControlService.getTextFieldFileExtMask().getText();

        String newExtension;
        //Тип файла по умолчанию
        newExtension = maskExtension.replaceAll("\\[T]", extension);

        //применяем по надобности счетчик
        if (newExtension.contains("[C]")) {
            newExtension = applyCounter(newExtension, index);
        }
        return newExtension;
    }

                                                                                                                        //Применить счетчик
    private String applyCounter(String fileStr, int index) {

        int start = GUIFieldsControlService.getSpinnerCounterStartTo().getValue();
        int step = GUIFieldsControlService.getSpinnerCounterStep().getValue();
        int digits = GUIFieldsControlService.getSpinnerCounterDigits().getValue();

        //вычисляем значение счетчика для конкретного файла
        int calculatedCount = start + (step * index);

        //работаем с дополнительными нулями в счетчике
        String finalCount = String.format("%0" + digits + "d", calculatedCount);

        fileStr = fileStr.replaceAll("\\[C]", finalCount);

        return fileStr;
    }

                                                                                                                        //Регистр
    public void applyRegister(String value) {

        fileItemsList = fileItemsStorageService.getFileItemEntitiesList();

        for (FileItemEntity fileItem : fileItemsList) {
            if (value.equals(GUIFieldsControlService.getComboBoxRegisterList().get(1))) {
                fileItem.setNewFileName(fileItem.getNewFileName().toUpperCase());
            } else if (value.equals(GUIFieldsControlService.getComboBoxRegisterList().get(2))) {
                fileItem.setNewFileName(fileItem.getNewFileName().toLowerCase());
            }
            fileItemsStorageService.saveFileItemEntity(fileItem);
        }

    }
}
