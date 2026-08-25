package com.example.config;

import com.example.model.Classroom;
import com.example.repository.ClassroomRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final ClassroomRepository classroomRepository;

public DataInitializer(ClassroomRepository classroomRepository) {
    this.classroomRepository = classroomRepository;
}

    @Override
    public void run(ApplicationArguments args) {
        // 既にデータがあれば初期化しない
        if (classroomRepository.count() > 0) return;

        List<Classroom> classrooms = List.of(
            // 1F
            new Classroom("101教室",   10, "1F"),
            new Classroom("102教室",   30, "1F"),
            new Classroom("103教室",   20, "1F"),
            new Classroom("104教室",   20, "1F"),
            // 2F（倉庫は登録しない）
            new Classroom("201ホール", 50, "2F（大ホール）"),
            new Classroom("202教室",   20, "2F"),
            // 3F
            new Classroom("301教室",   10, "3F"),
            new Classroom("302教室",   30, "3F"),
            new Classroom("303教室",   30, "3F")
        );
        classroomRepository.saveAll(classrooms);
        System.out.println("✅ 教室データを初期登録しました（" + classrooms.size() + "件）");
    }
}
