package ru.aikr.spring.fx.akru.service.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Setter
@Getter
@RequiredArgsConstructor
public class RenThread extends Thread {

    private List<FileItemEntity> fileItemEntities;

    private int startThreadElementIndex;
    private int endThreadElementIndex;
    private int brokenFilesCounter = 0;

    @Override
    public void run() {

        //перебираем элементы, соответствующие переданным индексам
        for (int i = startThreadElementIndex; i <= endThreadElementIndex; i++) {

            Path folder = Paths.get(fileItemEntities.get(i).getFilePath()).getParent();
            log.info(folder.toString());

            File original = folder.resolve(fileItemEntities.get(i).getOldFileName()).toFile();
            File newFile = folder.resolve(fileItemEntities.get(i).getNewFileName()).toFile();

            if (!(original.exists() & original.isFile() & original.canWrite())) {
                log.warn("Problem renaming file " + original.getAbsolutePath() + ". Maybe file do not exists, or contains invalid characters. SKIPPING");
                brokenFilesCounter ++;
            } else {
                original.renameTo(newFile);
                log.info("File " + original.getAbsolutePath() + " renamed successfully on " + newFile.getName());
            }
        }
    }
}