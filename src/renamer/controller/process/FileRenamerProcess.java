package renamer.controller.process;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import renamer.model.FileItem;
import renamer.storage.FileItemsStorage;

import java.util.ArrayList;
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
        for(int i = 0; i < fileItemsList.size(); i++) {

            String file1 = fileItemsList.get(i).getNewFileName();
            int count = 0;

            for(int j = 0; j < fileItemsList.size(); j++) {
                String file2 = fileItemsList.get(j).getNewFileName();

                if(file1.equalsIgnoreCase(file2)) {
                    count ++;
                }
                //если совпадений более одного
                if(count > 1) {
                    String fileName = fileItemsList.get(j).getNewFileName();
                    LOGGER.warn("Дублирование имен файлов при переименовании");

                    //если у файла нет расширения
                    if (!fileName.contains(".")) {
                        fileName = fileName + "____" + count;
                    }

                    //если расширение присутствует
                    else {
                        //отрезаем имя файла
                        fileName = fileItemsList.get(j).getNewFileName().substring(0, fileItemsList.get(j).getNewFileName().lastIndexOf("."));
                        //дописываем значение счетчика
                        fileName = fileName + "____" + count;
                        //получаем расширение файла
                        int extPos = fileItemsList.get(j).getNewFileName().lastIndexOf(".") + 1;
                        String ext = fileItemsList.get(j).getNewFileName().substring(extPos);
                        //приклеиваем его к измененному имени файла
                        fileName = fileName + "." + ext;
                    }

                    fileItemsList.get(j).setNewFileName(fileName);
                    LOGGER.warn("Фвйл " + fileItemsList.get(j).getNewFileName() + " (Старое имя: " + fileItemsList.get(j).getOldFileName() + ") будет переименован в " + fileName);
                }
            }
        }
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
