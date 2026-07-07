package sn.khoula.photographique.controller;

import jakarta.validation.Valid;
import sn.khoula.photographique.entity.Gallery;
import sn.khoula.photographique.service.GalleryService;
import sn.khoula.photographique.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/galleries")
public class GalleryController {

    private final GalleryService galleryService;
    private final PhotoService photoService;

    public GalleryController(GalleryService galleryService, PhotoService photoService) {
        this.galleryService = galleryService;
        this.photoService = photoService;
    }

    @GetMapping
    public String listGalleries(Model model) {
        List<Gallery> galleries = galleryService.findAll();
        model.addAttribute("galleries", galleries);
        model.addAttribute("activePage", "galleries");
        return "galleries";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("gallery", new Gallery());
        model.addAttribute("activePage", "galleries");
        return "create-gallery";
    }

    @PostMapping
    public String createGallery(@Valid @ModelAttribute Gallery gallery, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "galleries");
            return "create-gallery";
        }
        galleryService.save(gallery);
        return "redirect:/galleries";
    }

    @GetMapping("/{id}")
    public String viewGallery(@PathVariable Long id, Model model) {
        Optional<Gallery> galleryOpt = galleryService.findById(id);
        if (galleryOpt.isEmpty()) {
            return "redirect:/galleries";
        }
        Gallery gallery = galleryOpt.get();
        model.addAttribute("gallery", gallery);
        model.addAttribute("photos", photoService.findByGalleryId(id));
        model.addAttribute("activePage", "galleries");
        return "gallery-detail";
    }

    @GetMapping("/{id}/delete")
    public String deleteGallery(@PathVariable Long id) {
        galleryService.deleteById(id);
        return "redirect:/galleries";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Gallery> galleryOpt = galleryService.findById(id);
        if (galleryOpt.isEmpty()) {
            return "redirect:/galleries";
        }
        model.addAttribute("gallery", galleryOpt.get());
        model.addAttribute("activePage", "galleries");
        return "edit-gallery";
    }

    @PostMapping("/{id}/edit")
    public String updateGallery(@PathVariable Long id,
                                @Valid @ModelAttribute Gallery gallery,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "galleries");
            return "edit-gallery";
        }
        Optional<Gallery> existingOpt = galleryService.findById(id);
        if (existingOpt.isEmpty()) {
            return "redirect:/galleries";
        }
        Gallery existing = existingOpt.get();
        existing.setName(gallery.getName());
        existing.setDescription(gallery.getDescription());
        galleryService.save(existing);
        return "redirect:/galleries/" + id;
    }
}
