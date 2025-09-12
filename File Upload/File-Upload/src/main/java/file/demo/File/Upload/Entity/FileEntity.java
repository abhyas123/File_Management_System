package file.demo.File.Upload.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String name;
    private String type; // file type  (image/png, audio/mpeg, etc.)


    // for large size file upto 4gb

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data; // Actual file bytes
}
