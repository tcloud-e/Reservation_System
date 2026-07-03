package com.example.controller;

import com.example.model.Reservation;
import com.example.model.User;
import com.example.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reservations")
    public String list(@AuthenticationPrincipal User student, Model model) {
        model.addAttribute("reservations", reservationService.findUpcoming());
        model.addAttribute("myBookings", reservationService.findBookingsByStudent(student));
        return "student/reservation-list";
    }

    @PostMapping("/reservations/{id}/book")
    public String book(@PathVariable Long id, @AuthenticationPrincipal User student, Model model) {
        try {
            Reservation reservation = reservationService.findById(id);
            reservationService.bookLesson(reservation, student);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/student/reservations";
    }
}
