package ru.aikr.spring.fx.akru.service.preparing.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.domain.MetadataEntity;
import ru.aikr.spring.fx.akru.service.preparing.MetadataHandlerService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

import java.io.File;
import java.nio.file.Path;

@Slf4j
@Service
public class MetadataHandlerServiceImpl implements MetadataHandlerService {

    @Override
    public void readMetadata(FileItemEntity fileItem) {

        File jpegFile = Path.of(fileItem.getFilePath()).toFile();
        //создаем новый metadataStorage
        MetadataEntity metadataEntity = new MetadataEntity();

        //Если у данного fileItem такой storage уже существует, и не пустой, тогда забираем сразу с него данные
        if (fileItem.getMetadataEntity() != null) {
            metadataEntity = fileItem.getMetadataEntity();
        }

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

            //дата и время съемки
            ExifSubIFDDirectory directory1 = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            try {
                String exifDateTimeShooting = directory1.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                exifDateTimeShooting = exifDateTimeShooting.replaceAll(":", "-");
                String exifDate = exifDateTimeShooting.substring(0, 10);
                String exifTime = exifDateTimeShooting.substring(11, 19);
                metadataEntity.setEXIFDate(exifDate);
                metadataEntity.setEXIFTime(exifTime);
            } catch (NullPointerException e) {
                metadataEntity.setEXIFDate("[unknown date_time]");
                metadataEntity.setEXIFTime("");
            }

            //фокусное расстояние
            try {
                String focalLength = directory1.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
                if (!focalLength.isEmpty()) {
                    metadataEntity.setFocalLength(focalLength);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setFocalLength("[unknown focal]");
            }

            //ширина изображения
            try {
                String imageWidth = directory1.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                if (!imageWidth.isEmpty()) {
                    metadataEntity.setImageWidth(imageWidth);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setImageWidth("[unknown width]");
            }

            //высота изображения
            try {
                String imageHeight = directory1.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
                if (!imageHeight.isEmpty()) {
                    metadataEntity.setImageHeight(imageHeight);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setImageWidth("[unknown height]");
            }


            //автор
            ExifIFD0Directory directory2 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            try {
                String author = directory2.getString(ExifSubIFDDirectory.TAG_ARTIST);
                if (!author.isEmpty()) {
                    metadataEntity.setAuthor(author);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setAuthor("[unknown author]");
            }

            //камера
            try {
                String camera = directory2.getString(ExifIFD0Directory.TAG_MODEL);
                if (!camera.isEmpty()) {
                    metadataEntity.setCameraModel(camera);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setCameraModel("[unknown camera]");
            }

            //программа
            try {
                String software = directory2.getString(ExifIFD0Directory.TAG_SOFTWARE);
                if (!software.isEmpty()){
                    metadataEntity.setSoftware(software);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataEntity.setSoftware("[unknown software]");
            }
            //записываем метаданные в fileItem storage
            fileItem.setMetadataEntity(metadataEntity);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
