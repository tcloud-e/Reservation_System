package com.example.controller;

import com.example.model.User;
import com.example.service.ClassroomService;
import com.example.service.ReservationService;
import com.example.util.TimetableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClassroomService classroomService;

    @GetMapping("/reservations/new")
    public String newForm(@RequestParam(required = false) String date, Model model) {
        model.addAttribute("classrooms", classroomService.findAll());

        if (date != null && !date.isBlank()) {
            LocalDate localDate = LocalDate.parse(date);
            var periods = TimetableUtil.getPeriods(localDate.getDayOfWeek());
            model.addAttribute("selectedDate", date);
            model.addAttribute("dayLabel", TimetableUtil.dayLabel(localDate.getDayOfWeek()));
            model.addAttribute("periods", periods);
        }
        return "teacher/reservation-form";
    }

    @PostMapping("/reservations")
    public String create(
            @AuthenticationPrincipal User teacher,
            @RequestParam Long classroomId,
            @RequestParam String date,
            @RequestParam int period,
            @RequestParam Integer capacity,
            Model model) {

        try {
            reservationService.createReservation(
                    teacher, classroomId, LocalDate.parse(date), period, capacity);
        } catch (IllegalArgumentException e) {
            LocalDate localDate = LocalDate.parse(date);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("classrooms", classroomService.findAll());
            model.addAttribute("selectedDate", date);
            model.addAttribute("dayLabel", TimetableUtil.dayLabel(localDate.getDayOfWeek()));
            model.addAttribute("periods", TimetableUtil.getPeriods(localDate.getDayOfWeek()));
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
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User teacher) {
        try {
            reservationService.deleteReservation(id, teacher);
        } catch (IllegalArgumentException ignored) {}
        return "redirect:/teacher/reservations";
    }
}
