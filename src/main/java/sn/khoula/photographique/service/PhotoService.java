package sn.khoula.photographique.service;

import sn.khoula.photographique.entity.Gallery;
import sn.khoula.photographique.entity.Photo;
import sn.khoula.photographique.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<Photo> findByGalleryId(Long galleryId) {
        return photoRepository.findByGalleryIdOrderByCreatedAtDesc(galleryId);
    }

    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    public Photo uploadPhoto(MultipartFile file, String title, Gallery gallery) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        Path galleryUploadDir = Paths.get(uploadDir, gallery.getId().toString());
        Files.createDirectories(galleryUploadDir);

        Path targetPath = galleryUploadDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        Photo photo = new Photo();
        photo.setTitle(title);
        photo.setFileName(originalFileName);
        photo.setFilePath(gallery.getId() + "/" + uniqueFileName);
        photo.setContentType(file.getContentType());
        photo.setFileSize(file.getSize());
        photo.setGallery(gallery);

        return photoRepository.save(photo);
    }

    public void deleteById(Long id) {
        photoRepository.findById(id).ifPresent(photo -> {
            try {
                Path filePath = Paths.get(uploadDir, photo.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but continue with deletion from DB
            }
            photoRepository.deleteById(id);
        });
    }

    public long countByGalleryId(Long galleryId) {
        return photoRepository.countByGalleryId(galleryId);
    }
}
