package com.example.controller;

import com.example.model.User;
import com.example.service.ClassroomService;
import com.example.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClassroomService classroomService;

    @GetMapping("/reservations/new")
    public String newForm(Model model) {
        model.addAttribute("classrooms", classroomService.findAll());
        return "teacher/reservation-form";
    }

    @PostMapping("/reservations")
    public String create(
            @AuthenticationPrincipal User teacher,
            @RequestParam Long classroomId,
            @RequestParam String subject,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Integer capacity,
            Model model) {

        try {
            reservationService.createReservation(
                    teacher, classroomId, subject,
                    LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime),
                    capacity);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("classrooms", classroomService.findAll());
            return "teacher/reservation-form";
        }

        return "redirect:/teacher/reservations";
    }

    @GetMapping("/reservations")
    public String myReservations(@AuthenticationPrincipal User teacher, Model model) {
        model.addAttribute("reservations", reservationService.findByTeacher(teacher));
        return "teacher/my-reservations";
    }

    @PostMapping("/reservations/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User teacher, Model model) {
        try {
            reservationService.deleteReservation(id, teacher);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/teacher/reservations";
    }
}
