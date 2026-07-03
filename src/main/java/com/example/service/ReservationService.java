package com.example.service;

import com.example.model.*;
import com.example.repository.BookingRepository;
import com.example.repository.ClassroomRepository;
import com.example.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /** 講師が教室を予約して授業枠を作成 */
    public Reservation createReservation(User teacher, Long classroomId, String subject,
                                          LocalDate date, LocalTime startTime, LocalTime endTime,
                                          Integer capacity) {

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("終了時刻は開始時刻より後にしてください");
        }

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("教室が見つかりません"));

        // 同一教室・同一日付の予約と時間重複チェック
        List<Reservation> existing = reservationRepository.findByClassroomIdAndDate(classroomId, date);
        for (Reservation r : existing) {
            boolean overlap = startTime.isBefore(r.getEndTime()) && endTime.isAfter(r.getStartTime());
            if (overlap) {
                throw new IllegalArgumentException(
                    "この教室は同じ時間帯に既に予約されています（" + r.getStartTime() + "〜" + r.getEndTime() + "）");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setClassroom(classroom);
        reservation.setTeacher(teacher);
        reservation.setSubject(subject);
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setCapacity(capacity);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> findByTeacher(User teacher) {
        return reservationRepository.findByTeacher(teacher);
    }

    public List<Reservation> findUpcoming() {
        return reservationRepository.findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate.now());
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));
    }

    public void deleteReservation(Long id, User teacher) {
        Reservation r = findById(id);
        if (!r.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("自分が作成した予約のみ削除できます");
        }
        reservationRepository.delete(r);
    }

    /** 生徒が授業を予約 */
    public void bookLesson(Reservation reservation, User student) {
        bookingRepository.findByReservationAndStudent(reservation, student).ifPresent(b -> {
            throw new IllegalArgumentException("既にこの授業を予約済みです");
        });

        long count = bookingRepository.countByReservation(reservation);
        if (reservation.getCapacity() != null && count >= reservation.getCapacity()) {
            throw new IllegalArgumentException("定員に達しています");
        }

        Booking booking = new Booking(reservation, student);
        bookingRepository.save(booking);
    }

    public List<Booking> findBookingsByStudent(User student) {
        return bookingRepository.findByStudent(student);
    }

    public long countBookings(Reservation reservation) {
        return bookingRepository.countByReservation(reservation);
    }
}
