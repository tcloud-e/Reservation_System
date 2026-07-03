package com.example.service;

import com.example.model.Classroom;
import com.example.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }

    public Classroom save(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    public void delete(Long id) {
        classroomRepository.deleteById(id);
    }

    public Classroom findById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("教室が見つかりません"));
    }
}
