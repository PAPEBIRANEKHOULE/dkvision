package sn.khoula.photographique.repository;

import sn.khoula.photographique.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByGalleryIdOrderByCreatedAtDesc(Long galleryId);
    long countByGalleryId(Long galleryId);
}
