package com.example.repository;

import com.example.model.Booking;
import com.example.model.Reservation;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudent(User student);
    Optional<Booking> findByReservationAndStudent(Reservation reservation, User student);
    long countByReservation(Reservation reservation);
}
