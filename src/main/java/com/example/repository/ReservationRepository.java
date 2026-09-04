package com.example.repository;

import com.example.model.Reservation;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTeacher(User teacher);

    List<Reservation> findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate date);

    List<Reservation> findByClassroomIdAndDate(Long classroomId, LocalDate date);

    List<Reservation> findByClassroomId(Long classroomId);
}
