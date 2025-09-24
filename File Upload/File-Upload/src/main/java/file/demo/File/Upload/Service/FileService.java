package file.demo.File.Upload.Service;

import file.demo.File.Upload.DTO.FileMetadataDTO;
import file.demo.File.Upload.Entity.FileEntity;
import file.demo.File.Upload.Exception.FileNotFoundException;
import file.demo.File.Upload.Repository.FileRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class FileService {

    private final FileRepo fileRepository;

    public FileService(FileRepo fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity storeFile(MultipartFile file) throws Exception {
        log.info("Uploading file: {}", file.getOriginalFilename());
        FileEntity entity = new FileEntity();
        entity.setName(file.getOriginalFilename());
        entity.setType(file.getContentType());
        entity.setData(file.getBytes());
        return fileRepository.save(entity);
    }

    public FileEntity getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));
    }

    public ResponseEntity<byte[]> downloadFile(Long id) {
        FileEntity fileEntity = getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileEntity.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getName() + "\"")
                .body(fileEntity.getData());
    }

    public void streamDownload(Long id, HttpServletResponse response) throws Exception {
        FileEntity fileEntity = getFile(id);
        response.setContentType(fileEntity.getType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"");

        try (OutputStream os = response.getOutputStream()) {
            os.write(fileEntity.getData());
            os.flush();
        }
    }

    public ResponseEntity<byte[]> downloadAllFilesAsZip() throws IOException {
        List<FileEntity> files = fileRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (FileEntity file : files) {
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                zos.write(file.getData());
                zos.closeEntry();
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_files.zip")
                .body(baos.toByteArray());
    }

    //  Get metadata for a single file
    public FileMetadataDTO getFileMetadata(Long id) {
        FileEntity file = getFile(id);
        return new FileMetadataDTO(file.getId(), file.getName(), file.getType(), file.getData().length);
    }

    //  Download single file as ZIP
    public ResponseEntity<byte[]> downloadFileAsZip(Long id) throws IOException {
        FileEntity file = getFile(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(file.getName());
            zos.putNextEntry(entry);
            zos.write(file.getData());
            zos.closeEntry();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName() + ".zip")
                .body(baos.toByteArray());
    }

    public List<FileMetadataDTO> getAllFileMetadata() {
        List<FileEntity> files = fileRepository.findAll();
        List<FileMetadataDTO> metadataList = new ArrayList<>();

        for (FileEntity file : files) {
            metadataList.add(new FileMetadataDTO(
                    file.getId(),            //  real database ID
                    file.getName(),
                    file.getType(),
                    file.getData().length    // size in bytes
            ));
        }
        return metadataList;
    }

    public void deleteFile(Long id) {
        if (!fileRepository.existsById(id)) {
            throw new FileNotFoundException("File not found with ID: " + id);
        }
        fileRepository.deleteById(id);
    }

    public FileEntity updateFile(Long id, MultipartFile file) throws Exception {
        FileEntity existingFile = getFile(id);
        existingFile.setName(file.getOriginalFilename());
        existingFile.setType(file.getContentType());
        existingFile.setData(file.getBytes());
        return fileRepository.save(existingFile);
    }
}














