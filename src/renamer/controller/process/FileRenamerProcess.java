package renamer.controller.process;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import renamer.model.FileItem;
import renamer.storage.FileItemsStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileRenamerProcess {

    //получаем список файлов
    private ObservableList<FileItem> fileItemsList = FileItemsStorage.getInstance().getFileItemsList();

    private Logger LOGGER;
    private int brokenFilesCounter = 0;

    //constructor
    public FileRenamerProcess(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    //главный метод
    public void rename() {

        //проверяем на новые одинаковые имена
        checkingFileNames();

        //список тредов
        ArrayList<RenThread> threadsList = new ArrayList<>();

        //определяем кол-во потоков (вирт.ядер)
        int qtyThreads = Runtime.getRuntime().availableProcessors();

        //если файлов меньше, чем потоков проца - уменьшаем
        while (qtyThreads > fileItemsList.size()) {
            qtyThreads = qtyThreads - 2;
        }

        LOGGER.info("Количество потоков - " + qtyThreads);


        //выясняем, по сколько файлов кидать на поток
        int elementsOnThread = fileItemsList.size() / qtyThreads;
        //выясняем остаток после равного деления файлов
        int rest = fileItemsList.size() % qtyThreads;

        //Создаем нужное кол-во потоков, равное кол-ву определенных потоков процессора
        for (int i = 1; i <= qtyThreads; i++) {

            //создаем новый тред
            RenThread thread = new RenThread();

            //Передаем в тред ссылку на логгер
            thread.setLOGGER(LOGGER);

            //и ссылку на коллекцию
            thread.setFileItemsList(fileItemsList);

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
            int lastElement = fileItemsList.size() - 1;
            thread.setEndThreadElementIndex(lastElement);
        }

        LOGGER.info("* СТАРТ ВЫПОЛНЕНИЯ *");

        //Запуск всех тредов
        for (RenThread threads : threadsList) {
            try {
                threads.start();
                threads.join();

                brokenFilesCounter = brokenFilesCounter + threads.getBrokenFilesCounter();

            } catch (InterruptedException e) {
                LOGGER.error(e.fillInStackTrace());
            }
        }

        LOGGER.info("Процесс завершен. Проблемных файлов - " + brokenFilesCounter);
    }

    //проверка на наличие одинаковых имен перед переименованием
    public void checkingFileNames() {

        LOGGER.info("Проверяем на недопустимые символы");
        for(int i = 0; i < fileItemsList.size(); i++) {
            String file = fileItemsList.get(i).getNewFileName();
            file = illegalCharactersReplace(file);
            fileItemsList.get(i).setNewFileName(file);
        }


        LOGGER.info("Проверяем на одинаковые имена");

        int counter = 1;    // подготовка счетчика дубликатов имен файлов

        List<String> renamedNamesList = new ArrayList<>();                  //Временая коллекция для сравнения

        for (int index = 1; index <= fileItemsList.size() - 1; index++) {   //проходим список файлов без прохода первого файла

            String currentFileItem = fileItemsList.get(index).getNewFileName();         // получаем имя текущего файла
            String previousFileItem = fileItemsList.get(index - 1).getNewFileName();    // получаем имя предыдущего файла


            if (isDuplicate(currentFileItem, previousFileItem, renamedNamesList)) {     //если имена совпадают
                LOGGER.warn("Дублирование имен файлов при переименовании");
                counter ++;                                                 //увеличим значение счетчика
                String fileName;

                if (!currentFileItem.contains(".")) {                       //если у файла нет расширения
                    fileName = addDoubleCounter(currentFileItem, counter);
                }
                else {                                                      //если расширение присутствует
                    fileName = currentFileItem.substring(0, currentFileItem.lastIndexOf("."));   //отрезаем имя файла
                    fileName = addDoubleCounter(fileName, counter);         //дописываем значение счетчика
                    int extPosition = currentFileItem.lastIndexOf(".") + 1; //получаем расширение файла
                    String ext = currentFileItem.substring(extPosition);
                    fileName = fileName + "." + ext;                        //приклеиваем его к измененному имени файла
                }

                fileItemsList.get(index).setNewFileName(fileName);          //запись изменений в коллекцию
                LOGGER.warn("Фвйл " + currentFileItem + " (Старое имя: " + fileItemsList.get(index).getOldFileName() + ") будет переименован в " + fileName);
            }
            else {                                                          //если имена не совпадают
                counter = 1;                                                //обнуляем счетчик
            }

            renamedNamesList.add(currentFileItem);                          //добавляем новое имя в список переименованных файлов
        }
    }

    //проверка на совпадения
    private boolean isDuplicate(String curFile, String prevFile, List<String> renFileList) {

        boolean flag = false;
        if (curFile.equalsIgnoreCase(prevFile)) {
            flag = true;
        }
        for (String aRenFileList : renFileList) {
            if (aRenFileList.equalsIgnoreCase(curFile)) {
                flag = true;
            }
        }
        return flag;
    }


    //добавление значения счетчика дубликатов к новому имени файла
    private String addDoubleCounter (String fileName, int counter) {
        return fileName + "__" + counter;
    }

    //выпиливаем с имени файла недопустимые символы (если вдруг где-то их упустили)
    private String illegalCharactersReplace (String fileName) {

        fileName = fileName.replaceAll(":", "-");       //отделяет букву диска или имя альтернативного потока данных
        fileName = fileName.replaceAll("\\\\", "-");    //разделитель подкаталогов
        fileName = fileName.replaceAll("\\/", "-");     //разделитель ключей командного интерпретатора
        fileName = fileName.replaceAll("\\*", "-");     //заменяющий символ
        fileName = fileName.replaceAll("\\?", "-");     //заменяющий символ
        fileName = fileName.replaceAll("\"", "-");      //используется для указания путей, содержащих пробелы
        fileName = fileName.replaceAll("\\<", "-");     //перенаправление ввода
        fileName = fileName.replaceAll("\\>", "-");     //перенаправление вывода
        fileName = fileName.replaceAll("\\|", "-");     //обозначает конвейер
        fileName = fileName.replaceAll("\\+", "-");     //конкатенация

        return fileName;
    }
}
