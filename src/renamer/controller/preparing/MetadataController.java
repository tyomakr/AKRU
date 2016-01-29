package renamer.controller.preparing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import renamer.model.FileItem;
import renamer.model.MetadataStorage;

import java.io.File;

public class MetadataController {

    public static void readMetadata(FileItem fileItem) {

        File jpegFile = fileItem.getFile();
        //создаем новый metadataStorage
        MetadataStorage metadataStorage =  new MetadataStorage();
        //Если у данного fileItem такой storage уже существует, и не пустой, тогда забираем сразу с него данные
        if (fileItem.getMetadata() != null) {
            metadataStorage = fileItem.getMetadata();
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
                if (!exifDateTimeShooting.isEmpty()) {
                    metadataStorage.setEXIFDate(exifDate);
                    metadataStorage.setEXIFTime(exifTime);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setEXIFDate("[unknown date_time]");
                metadataStorage.setEXIFTime("");
            }

            //фокусное расстояние
            try {
                String focalLength = directory1.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
                if (!focalLength.isEmpty()) {
                    metadataStorage.setFocalLength(focalLength);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setFocalLength("[unknown focal]");
            }

            //ширина изображения
            try {
                String imageWidth = directory1.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                if (!imageWidth.isEmpty()) {
                    metadataStorage.setImageWidth(imageWidth);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setImageWidth("[unknown width]");
            }

            //высота изображения
            try {
                String imageHeight = directory1.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
                if (!imageHeight.isEmpty()) {
                    metadataStorage.setImageHeight(imageHeight);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setImageWidth("[unknown height]");
            }

            //ISO
            try {
                String isoSpeed = directory1.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                if (!isoSpeed.isEmpty()) {
                    metadataStorage.setIsoSpeed(isoSpeed);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setIsoSpeed("[unknown ISO]");
            }


            //автор
            ExifIFD0Directory directory2 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            try {
                String author = directory2.getString(ExifSubIFDDirectory.TAG_ARTIST);
                if (!author.isEmpty()) {
                    metadataStorage.setAuthor(author);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setAuthor("[unknown author]");
            }

            //камера
            try {
                String camera = directory2.getString(ExifIFD0Directory.TAG_MODEL);
                if (!camera.isEmpty()) {
                    metadataStorage.setCameraModel(camera);
                }
                else throw new NullPointerException();
            } catch (NullPointerException e) {
                metadataStorage.setCameraModel("[unknown camera]");
            }

            //записываем метаданные в fileItem storage
            fileItem.setMetadata(metadataStorage);

        } catch (Exception e) {
            e.getMessage();
        }

    }

}
