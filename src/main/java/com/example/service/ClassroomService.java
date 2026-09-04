package com.example.service;

import com.example.model.Classroom;
import com.example.repository.ClassroomRepository;
import com.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ReservationRepository reservationRepository;

    public ClassroomService(ReservationRepository reservationRepository, ClassroomRepository classroomRepository) {
        this.reservationRepository = reservationRepository;
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }

    public Classroom save(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    /**
     * 生徒が予約している授業がある教室は削除不可
     */
    public void delete(Long id) {
        var reservations = reservationRepository.findByClassroomId(id);
        for (var r : reservations) {
            if (!r.getBookings().isEmpty()) {
                throw new IllegalStateException(
                    "生徒が予約している授業があるため「" + r.getClassroom().getName() + "」は削除できません");
            }
        }
        classroomRepository.deleteById(id);
    }

    public Classroom findById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("教室が見つかりません"));
    }
}
