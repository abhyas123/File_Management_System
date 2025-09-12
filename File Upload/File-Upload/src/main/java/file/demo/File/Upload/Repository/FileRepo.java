package file.demo.File.Upload.Repository;


import file.demo.File.Upload.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<FileEntity,Long> {
}
