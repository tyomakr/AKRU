package renamer.model;

public class MetadataStorage {

    private String EXIFDate = null;
    private String EXIFTime = null;
    private String author = null;

    private String cameraModel = null;
    private String focalLength = null;
    private String apertureValue = null;
    private String shutterSpeed = null;
    private String isoSpeed = null;

    private String imageWidth = null;
    private String imageHeight = null;
    private String software = null;



    //constructors
    public MetadataStorage() {}


    //setters and getters
    public String getEXIFDate() {
        return EXIFDate;
    }

    public void setEXIFDate(String EXIFDate) {
        this.EXIFDate = EXIFDate;
    }

    public String getEXIFTime() {
        return EXIFTime;
    }

    public void setEXIFTime(String EXIFTime) {
        this.EXIFTime = EXIFTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getApertureValue() {
        return apertureValue;
    }

    public void setApertureValue(String apertureValue) {
        this.apertureValue = apertureValue;
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public void setShutterSpeed(String shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    public String getIsoSpeed() {
        return isoSpeed;
    }

    public void setIsoSpeed(String isoSpeed) {
        this.isoSpeed = isoSpeed;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }
}


