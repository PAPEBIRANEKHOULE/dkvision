package sn.khoula.photographique.controller;

import jakarta.validation.Valid;
import sn.khoula.photographique.entity.ContactMessage;
import sn.khoula.photographique.repository.ContactMessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    private final ContactMessageRepository contactMessageRepository;

    public ContactController(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid ContactMessage contactMessage,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "contact");
            return "contact";
        }
        contactMessageRepository.save(contactMessage);
        return "redirect:/contact?success";
    }
}
