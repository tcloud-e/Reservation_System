package com.example.cramschool.controller;

import com.example.cramschool.dto.TimeSlotDto;
import com.example.cramschool.model.Classroom;
import com.example.cramschool.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ClassroomController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @GetMapping("/teacher/classrooms")
    public String viewClassroomStatus(
            @RequestParam(name = "date", required = false) String dateStr, 
            Model model) {
        
        // 日付が未選択なら今日の日付にする
        LocalDate selectedDate = (dateStr == null || dateStr.isEmpty()) 
                ? LocalDate.now() : LocalDate.parse(dateStr);
        
        // 1. 全教室データを取得（101〜303）
        List<Classroom> classrooms = classroomRepository.findAll();
        
        // 2. 選択された日付の曜日から、時間割（コマ）の枠を取得
        List<TimeSlotDto> timeSlots = TimeSlotDto.getSlotsByDate(selectedDate);
        
        // 本来はここで「既に予約テーブルに存在するデータ」を引いてきて、空き・予約済を判定するフラグを渡します。
        // 今回は一覧表示の枠組みを最優先で作成するため、枠データをそのまま渡します。

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("timeSlots", timeSlots);
        
        return "teacher/classroom_status";
    }
}