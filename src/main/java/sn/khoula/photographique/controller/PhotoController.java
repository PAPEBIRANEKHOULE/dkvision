package sn.khoula.photographique.controller;

import sn.khoula.photographique.entity.Gallery;
import sn.khoula.photographique.entity.Photo;
import sn.khoula.photographique.service.GalleryService;
import sn.khoula.photographique.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping
public class PhotoController {

    private final PhotoService photoService;
    private final GalleryService galleryService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public PhotoController(PhotoService photoService, GalleryService galleryService) {
        this.photoService = photoService;
        this.galleryService = galleryService;
    }

    @GetMapping("/galleries/{id}/upload")
    public String showUploadForm(@PathVariable Long id, Model model) {
        Optional<Gallery> galleryOpt = galleryService.findById(id);
        if (galleryOpt.isEmpty()) {
            return "redirect:/galleries";
        }
        model.addAttribute("gallery", galleryOpt.get());
        model.addAttribute("activePage", "galleries");
        return "upload-photo";
    }

    @PostMapping("/galleries/{id}/upload")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(value = "title", required = false) String title,
                              Model model) {
        Optional<Gallery> galleryOpt = galleryService.findById(id);
        if (galleryOpt.isEmpty()) {
            return "redirect:/galleries";
        }

        if (file.isEmpty()) {
            model.addAttribute("gallery", galleryOpt.get());
            model.addAttribute("error", "Veuillez sélectionner un fichier");
            model.addAttribute("activePage", "galleries");
            return "upload-photo";
        }

        try {
            photoService.uploadPhoto(file, title, galleryOpt.get());
        } catch (IOException e) {
            model.addAttribute("gallery", galleryOpt.get());
            model.addAttribute("error", "Erreur lors de l'upload : " + e.getMessage());
            model.addAttribute("activePage", "galleries");
            return "upload-photo";
        }

        return "redirect:/galleries/" + id;
    }

    @GetMapping("/photos/{id}/delete")
    public String deletePhoto(@PathVariable Long id) {
        Optional<Photo> photoOpt = photoService.findById(id);
        if (photoOpt.isPresent()) {
            Long galleryId = photoOpt.get().getGallery().getId();
            photoService.deleteById(id);
            return "redirect:/galleries/" + galleryId;
        }
        return "redirect:/galleries";
    }

    @GetMapping("/uploads/{galleryId}/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String galleryId, @PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, galleryId, fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg";
                String fileNameLower = fileName.toLowerCase();
                if (fileNameLower.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileNameLower.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (fileNameLower.endsWith(".webp")) {
                    contentType = "image/webp";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            // ignore
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("galleries", galleryService.findAll());
        model.addAttribute("activePage", "home");
        return "index";
    }
}
