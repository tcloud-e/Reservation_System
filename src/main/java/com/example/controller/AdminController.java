package com.example.controller;

import com.example.model.Classroom;
import com.example.service.ClassroomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ClassroomService classroomService;

    public AdminController(ClassroomService classroomService) {
    this.classroomService = classroomService;
    }

    @GetMapping("/classrooms")
    public String list(Model model) {
        model.addAttribute("classrooms", classroomService.findAll());
        model.addAttribute("classroom", new Classroom());
        return "admin/classroom-list";
    }

    @PostMapping("/classrooms")
    public String create(@ModelAttribute Classroom classroom) {
        classroomService.save(classroom);
        return "redirect:/admin/classrooms";
    }

    @PostMapping("/classrooms/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            classroomService.delete(id);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/classrooms";
    }
}
