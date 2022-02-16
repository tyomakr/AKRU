package ru.aikr.spring.fx.akru.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "file_item_entities")
public class FileItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "filepath")
    private String filePath;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadataid", referencedColumnName = "metadataid")
    private MetadataEntity metadataEntity;

    @Column(name = "oldfilename")
    private String oldFileName;
    @Column(name = "newfilename")
    private String newFileName;
    @Column(name = "filesize")
    private Long fileSize;


    public FileItemEntity(String filePath, String oldFileName, String newFileName, long fileSize) {

        this.filePath = filePath;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
        this.fileSize = fileSize;
    }
}