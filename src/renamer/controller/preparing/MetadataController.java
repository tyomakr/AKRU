package renamer.controller.preparing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import renamer.model.FileItem;
import renamer.model.MetadataStorage;

import java.io.File;

public class MetadataController {

    public static void showMetadataPreview(FileItem fileItem) {

        File jpegFile = fileItem.getFile();
        MetadataStorage metadataStorage =  new MetadataStorage();
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
                metadataStorage.setEXIFDate("[Unknown date_time]");
                metadataStorage.setEXIFTime("");
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


            fileItem.setMetadata(metadataStorage);

        } catch (Exception e) {
            e.getMessage();
        }

    }





}
