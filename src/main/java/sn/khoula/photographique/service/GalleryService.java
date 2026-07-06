package sn.khoula.photographique.service;

import sn.khoula.photographique.entity.Gallery;
import sn.khoula.photographique.repository.GalleryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalleryService {

    private final GalleryRepository galleryRepository;

    public GalleryService(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    public List<Gallery> findAll() {
        return galleryRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Gallery> findById(Long id) {
        return galleryRepository.findById(id);
    }

    public Gallery save(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    public void deleteById(Long id) {
        galleryRepository.deleteById(id);
    }
}
