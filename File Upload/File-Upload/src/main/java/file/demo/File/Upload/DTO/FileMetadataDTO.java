package file.demo.File.Upload.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileMetadataDTO {

    private Long id;
    private String name;
    private String type;
    private long size; // in bytes



}
