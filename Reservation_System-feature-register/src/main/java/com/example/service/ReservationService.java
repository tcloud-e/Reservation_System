package com.example.service;

import com.example.model.*;
import com.example.repository.BookingRepository;
import com.example.repository.ClassroomRepository;
import com.example.repository.ReservationRepository;
import com.example.util.TimetableUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClassroomRepository classroomRepository;
    private final BookingRepository bookingRepository;

    public ReservationService(ReservationRepository reservationRepository, ClassroomRepository classroomRepository, BookingRepository bookingRepository ) {
        this.reservationRepository = reservationRepository;
        this.classroomRepository = classroomRepository;
        this.bookingRepository = bookingRepository;
    }

    /** 講師がコマ数を指定して教室を予約 */
    public Reservation createReservation(User teacher, Long classroomId,
                                          LocalDate date, int period, Integer capacity) {

        var dayOfWeek = date.getDayOfWeek();
        var periodInfo = TimetableUtil.getPeriod(dayOfWeek, period)
                .orElseThrow(() -> new IllegalArgumentException(
                    "指定した日付（" + TimetableUtil.dayLabel(dayOfWeek) + "）に"
                    + period + "コマ目はありません"));

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("教室が見つかりません"));

        // 同一教室・同一日・同一コマの重複チェック
        List<Reservation> existing = reservationRepository.findByClassroomIdAndDate(classroomId, date);
        for (Reservation r : existing) {
            if (r.getPeriod() == period) {
                throw new IllegalArgumentException(
                    classroom.getName() + " の " + period + "コマ目は既に予約されています");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setClassroom(classroom);
        reservation.setTeacher(teacher);
        reservation.setSubject(periodInfo.subject());
        reservation.setDate(date);
        reservation.setPeriod(period);
        reservation.setStartTime(periodInfo.startTime());
        reservation.setEndTime(periodInfo.endTime());
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
        bookingRepository.save(new Booking(reservation, student));
    }

    public List<Booking> findBookingsByStudent(User student) {
        return bookingRepository.findByStudent(student);
    }
}
