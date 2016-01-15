package renamer.controller.process;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import renamer.model.FileItem;
import renamer.storage.FileItemsStorage;

import java.util.ArrayList;

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
}
