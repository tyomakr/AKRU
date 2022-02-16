package ru.aikr.spring.fx.akru.service.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.service.process.RenThread;
import ru.aikr.spring.fx.akru.service.process.RenamerService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RenamerServiceImpl implements RenamerService {

    private final FileItemsStorageService fileItemsStorageService;

    private int brokenFilesCounter = 0;

    public void startRenameProcess() {
        List<FileItemEntity> fileItemEntitiesList = fileItemsStorageService.getFileItemEntitiesList();

        // проверяем на новые одинаковые имена
        checkingFileNames(fileItemEntitiesList);

        //список тредов
        ArrayList<RenThread> threadsList = new ArrayList<>();

        //определяем кол-во потоков (вирт.ядер)
        int qtyThreads = Runtime.getRuntime().availableProcessors();

        //если файлов меньше, чем потоков проца - уменьшаем
        while (qtyThreads > fileItemEntitiesList.size()) {
            qtyThreads = qtyThreads - 2;
        }
        log.info("Quantity threads: " + qtyThreads);

        //выясняем, по сколько файлов кидать на поток
        int elementsOnThread = fileItemEntitiesList.size() / qtyThreads;
        //выясняем остаток после равного деления файлов
        int rest = fileItemEntitiesList.size() % qtyThreads;

        //Создаем нужное кол-во потоков, равное кол-ву определенных потоков процессора
        for (int i = 1; i <= qtyThreads; i++) {

            //создаем новый тред
            RenThread thread = new RenThread();
            //и ссылку на коллекцию
            thread.setFileItemEntities(fileItemEntitiesList);
            //устанавливаем последний элемент треда
            int lastElement = (elementsOnThread * i);
            //записываем с учетом индекса листа
            thread.setEndThreadElementIndex(lastElement - 1);
            //устанавливаем ему первый элемент треда
            thread.setStartThreadElementIndex(lastElement - elementsOnThread);
            //добавляем тред в список
            threadsList.add(thread);
        }

        //если остается какой-то остаток
        if (rest != 0) {
            //берем последний созданный тред
            RenThread thread = threadsList.get(threadsList.size() - 1);
            // добавляем остаток к последнему треду
            int lastElement = fileItemEntitiesList.size() - 1;
            thread.setEndThreadElementIndex(lastElement);
        }

        log.info("* START RENAMING *");

        //Запуск всех тредов
        for (RenThread threads : threadsList) {
            try {
                threads.start();
                threads.join();
                brokenFilesCounter = brokenFilesCounter + threads.getBrokenFilesCounter();

            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        log.info("RENAMING PROCESS COMPLETE. Problem files: " + brokenFilesCounter);
    }


    // проверка на наличие одинаковых имен перед переименованием
    public void checkingFileNames(List<FileItemEntity> fileList) {

        log.info("Checking for illegal characters...");
        for (FileItemEntity fileItemEntity : fileList) {
            String file = fileItemEntity.getNewFileName();
            file = illegalCharactersReplace(file);
            fileItemEntity.setNewFileName(file);
        }

        log.info("Checking for identical names...");
        validateIdenticalOldAndNewFilenames(fileList);                                                                  //проверка на совпадение новых имен со старыми

        int counter = 1;                                                                                                //подготовка счетчика дубликатов новых имен файлов
        List<String> renamedNamesList = new ArrayList<>();                                                              //временная коллекция для сравнения

        for (int index = 1; index <= fileList.size() - 1; index++) {                                                    //проходим список файлов без прохода первого файла
            String currentFileItem = fileList.get(index).getNewFileName();                                              //получаем имя текущего файла
            String previousFileItem = fileList.get(index - 1).getNewFileName();                                         //получаем имя предыдущего файла

            if (isDuplicate(currentFileItem, previousFileItem, renamedNamesList)) {                                     //если имена совпадают
                log.warn("Duplicate filenames on rename");
                counter ++;                                                                                             //увеличим значение счетчика

                String fileName = changeNewFilename(currentFileItem, counter);                                          //изменение имени файла (с учетом расширения)
                fileList.get(index).setNewFileName(fileName);                                                           //запись изменений в коллекцию
                log.warn("File " + currentFileItem + "(Old filename: " + fileList.get(index).getOldFileName() + ") will be renamed to " + fileName );
            }
            else {                                                                                                      //если имена не совпадают
                counter = 1;                                                                                            //обнуляем счетчик
            }
            renamedNamesList.add(currentFileItem);                                                                      //добавляем новое имя в список переименованных файлов
        }
    }

    //сравнение на совпадение новых имен файлов со старыми (для избежания затирания оригинальных файлов таким же именем нового файла)
    private List<FileItemEntity> validateIdenticalOldAndNewFilenames(List<FileItemEntity> fileList) {

        for (int old = 0; old < fileList.size(); old++) {
            int counter = 1;
            for (FileItemEntity fileItemEntity : fileList) {
                if (fileList.get(old).getOldFileName().equalsIgnoreCase(fileItemEntity.getNewFileName())) {
                    String modFilename = changeNewFilename(fileItemEntity.getNewFileName(), counter);
                    fileItemEntity.setNewFileName(modFilename);
                    counter++;
                    log.info("Conflict with old filenames. The file " + fileItemEntity.getOldFileName() + " will be renamed to " + modFilename);
                }
            }
        }
        return fileList;
    }


    //выпиливаем с имени файла недопустимые символы (если вдруг где-то их упустили)
    private String illegalCharactersReplace (String fileName) {

        //список regexp на разные случаи
        for (String s : Arrays.asList(":", "\\\\", "\\/", "\\*", "\\?", "\"", "\\<", "\\>", "\\|", "\\+")) {
            fileName = fileName.replaceAll(s, "-");
        }
        return fileName;
    }

    //проверка на совпадения
    private boolean isDuplicate(String curFile, String prevFile, List<String> renFileList) {

        boolean flag = curFile.equalsIgnoreCase(prevFile);
        for (String aRenFileList : renFileList) {
            if (aRenFileList.equalsIgnoreCase(curFile)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    //переименование файла (для дублей)
    private String changeNewFilename(String originalFilename, int counter) {

        String resultFilename;

        if (!originalFilename.contains(".")) {                                                                          //если у файла нет расширения
            resultFilename = addDoubleCounter(originalFilename, counter);
        }
        else {                                                                                                          //если расширение присутствует
            resultFilename = originalFilename.substring(0, originalFilename.lastIndexOf("."));                       //отрезаем имя файла
            resultFilename = addDoubleCounter(resultFilename, counter);                                                 //дописываем значение счетчика
            int extPosition = originalFilename.lastIndexOf(".") + 1;                                                 //получаем расширение файла
            String ext = originalFilename.substring(extPosition);
            resultFilename = resultFilename + "." + ext;                                                                //приклеиваем его к измененному имени файла
        }
        return resultFilename;
    }


    //добавление значения счетчика дубликатов к новому имени файла
    private String addDoubleCounter (String fileName, int counter) {
        return fileName + "__" + counter;
    }
}

