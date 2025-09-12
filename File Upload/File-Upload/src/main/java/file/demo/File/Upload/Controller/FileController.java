package file.demo.File.Upload.Controller;

import file.demo.File.Upload.DTO.FileMetadataDTO;
import file.demo.File.Upload.Exception.FileNotFoundException;
import file.demo.File.Upload.Service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.storeFile(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload file");
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        try {
            return fileService.downloadFile(id);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("File not found");
        }
    }

    @GetMapping("/stream/download/{id}")
    public void streamDownload(@PathVariable Long id, HttpServletResponse response) {
        try {
            fileService.streamDownload(id, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/download-all")
    public ResponseEntity<?> downloadAllFilesAsZip() {
        try {
            return fileService.downloadAllFilesAsZip();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error creating ZIP file");
        }
    }

    @GetMapping("/files/metadata")
    public ResponseEntity<List<FileMetadataDTO>> getAllFileMetadata() {
        try {
            List<FileMetadataDTO> metadataList = fileService.getAllFileMetadata();
            return ResponseEntity.ok(metadataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //  Download single file as ZIP by ID
    @GetMapping("/download-zip/{id}")
    public ResponseEntity<?> downloadFileAsZip(@PathVariable Long id) {
        try {
            return fileService.downloadFileAsZip(id);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("File not found");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error creating ZIP file");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok("File deleted successfully");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("File not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            fileService.updateFile(id, file);
            return ResponseEntity.ok("File updated successfully");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("File not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update file");
        }
    }
}






















