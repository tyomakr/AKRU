package renamer.model;

public class MetadataStorage {

    private String EXIFDate = null;
    private String EXIFTime = null;
    private String author = null;

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
}


