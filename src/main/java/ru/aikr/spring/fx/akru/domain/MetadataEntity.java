package ru.aikr.spring.fx.akru.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "metadata_entity")
public class MetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metadataid")
    private Long id;

    @Column(name = "exifdate")
    private String EXIFDate = null;
    @Column(name = "exiftime")
    private String EXIFTime = null;
    @Column(name = "author")
    private String author = null;

    @Column(name = "cameramodel")
    private String cameraModel = null;
    @Column(name = "focallength")
    private String focalLength = null;

    @Column(name = "imagewidth")
    private String imageWidth = null;
    @Column(name = "imageheight")
    private String imageHeight = null;
    @Column(name = "software")
    private String software = null;

    public MetadataEntity(String EXIFDate, String EXIFTime, String author, String cameraModel, String focalLength, String imageWidth, String imageHeight, String software) {
        this.EXIFDate = EXIFDate;
        this.EXIFTime = EXIFTime;
        this.author = author;
        this.cameraModel = cameraModel;
        this.focalLength = focalLength;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.software = software;
    }
}