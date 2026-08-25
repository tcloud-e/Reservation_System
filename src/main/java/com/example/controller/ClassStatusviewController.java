package com.example.controller;

import com.example.model.Classroom;
import com.example.model.Reservation;
import com.example.repository.ClassroomRepository;
import com.example.repository.ReservationRepository; // ★追加
import com.example.util.TimetableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/teacher")
public class ClassStatusviewController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ReservationRepository reservationRepository; // ★追加（本物の予約をDBから引くために使用）

    @GetMapping("/room_status.html")
    public String showFloorMap(Model model) {
        List<Classroom> classrooms = classroomRepository.findAll();
        
        Map<String, List<Classroom>> floorMap = new LinkedHashMap<>();
        floorMap.put("1F", new ArrayList<>());
        floorMap.put("2F", new ArrayList<>());
        floorMap.put("3F", new ArrayList<>());

        for (Classroom classroom : classrooms) {
            String location = classroom.getLocation();
            if (location != null) {
                if (location.contains("1F")) {
                    floorMap.get("1F").add(classroom);
                } else if (location.contains("2F")) {
                    floorMap.get("2F").add(classroom);
                } else if (location.contains("3F")) {
                    floorMap.get("3F").add(classroom);
                }
            }
        }

        model.addAttribute("floorMap", floorMap);
        model.addAttribute("today", LocalDate.now());
        return "teacher/room_status";
    }

    // ==========================================
    // ★ 以下の本物データ連携APIメソッドを追加します
    // ==========================================
    @GetMapping("/api/schedule")
    @ResponseBody
    public ResponseEntity<?> getClassroomSchedule(
            @RequestParam("classroomId") Long classroomId,
            @RequestParam("date") String dateStr) {

        try {
            LocalDate date = LocalDate.parse(dateStr);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // 1. 指定された日付・教室の予約リストをデータベースから取得
            List<Reservation> reservations = reservationRepository.findByClassroomIdAndDate(classroomId, date);

            // 2. 指定曜日の標準タイムテーブルを取得
            List<TimetableUtil.PeriodInfo> periods = TimetableUtil.getPeriods(dayOfWeek);
            
            List<Map<String, Object>> scheduleList = new ArrayList<>();

            // ★ periods が null の場合の安全対策
            if (periods != null) {
                for (TimetableUtil.PeriodInfo period : periods) {
                    Map<String, Object> periodStatus = new HashMap<>();
                    periodStatus.put("period", period.period());
                    periodStatus.put("timeLabel", period.startTime() + " 〜 " + period.endTime());
                    
                    // 3. データベース内にこのコマ（period）の予約があるかチェック
                    Optional<Reservation> matchingRes = reservations.stream()
                            .filter(r -> r.getPeriod() == period.period())
                            .findFirst();

                    if (matchingRes.isPresent()) {
                        Reservation res = matchingRes.get();
                        // 予約あり：科目名と予約者を表示
                        periodStatus.put("subject", res.getSubject());
                        periodStatus.put("status", "予約あり(" + res.getTeacher().getName() + "先生)");
                        periodStatus.put("booked", true);
                    } else {
                        // 予約なし（空室）：規定の通常科目名を表示
                        periodStatus.put("subject", period.subject());
                        periodStatus.put("status", "空室");
                        periodStatus.put("booked", false);
                    }

                    scheduleList.add(periodStatus);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("classroomId", classroomId);
            response.put("date", dateStr);
            response.put("dayLabel", TimetableUtil.dayLabel(dayOfWeek));
            response.put("schedule", scheduleList); // 空の場合でも必ず空のリストを返す

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 万が一Java側で予期せぬエラーが起きた場合も、500エラーにせず安全な空データを返す
            e.printStackTrace(); // コンソールにエラーログを出力
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("classroomId", classroomId);
            errorResponse.put("date", dateStr);
            errorResponse.put("dayLabel", "不明");
            errorResponse.put("schedule", new ArrayList<>()); // フロントで length エラーを起こさないための空リスト
            return ResponseEntity.ok(errorResponse);
        }
    }
}
// package com.example.controller;

// import com.example.model.Classroom;
// import com.example.repository.ClassroomRepository; // 事前に作成されていると仮定
// import com.example.util.TimetableUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.time.DayOfWeek;
// import java.time.LocalDate;
// import java.util.*;

// @Controller
// @RequestMapping("/classroom")
// public class ClassStatusviewController {

//     @Autowired
//     private ClassroomRepository classroomRepository;

//     // 1. フロアマップ画面の表示
//     @GetMapping("/map")
//     public String showFloorMap(Model model) {
//         List<Classroom> classrooms = classroomRepository.findAll();
        
//         // 階（Floor）ごとに教室をグルーピングしてマップに詰める (例: "1F", "2F", "3F")
//         Map<String, List<Classroom>> floorMap = new TreeMap<>();
//         for (Classroom classroom : classrooms) {
//             // location フィールドから "1F" や "2F" を抽出、または初期値から判別
//             String floor = classroom.getLocation() != null ? classroom.getLocation() : "1F";
//             floorMap.computeIfAbsent(floor, k -> new ArrayList<>()).add(classroom);
//         }

//         model.addAttribute("floorMap", floorMap);
//         model.addAttribute("today", LocalDate.now());
//         return "classroom/map"; // templates/classroom/map.html を表示
//     }

//     // 2. [API] 特定の教室・特定の日付の予約状況を非同期で返す
//     @GetMapping("/api/schedule")
//     @ResponseBody
//     public ResponseEntity<?> getClassroomSchedule(
//             @RequestParam("classroomId") Long classroomId,
//             @RequestParam("date") String dateStr) {

//         LocalDate date = LocalDate.parse(dateStr);
//         DayOfWeek dayOfWeek = date.getDayOfWeek();

//         // 指定曜日の標準タイムテーブルを取得
//         List<TimetableUtil.PeriodInfo> periods = TimetableUtil.getPeriods(dayOfWeek);

//         // 実際の実装では、ここで「すでにその教室・コマに予約が入っているか」を
//         // 予約テーブル（Reservation/Bookingエンティティ等）からDB検索します。
//         // ここではサンプルとしてダミーの予約ステータスを付与して返します。
//         List<Map<String, Object>> scheduleList = new ArrayList<>();
//         for (TimetableUtil.PeriodInfo period : periods) {
//             Map<String, Object> periodStatus = new HashMap<>();
//             periodStatus.put("period", period.period());
//             periodStatus.put("timeLabel", period.startTime() + " 〜 " + period.endTime());
//             periodStatus.put("subject", period.subject());
            
//             // TODO: DBの予約状況を反映させるロジック
//             // 例: classroomId と date と period.period() で検索して、予約があれば "予約あり(講師名/生徒数)", なければ "空き"
//             boolean isBooked = Math.random() > 0.5; // ダミーのランダム判定
//             periodStatus.put("status", isBooked ? "予約あり" : "空き");
//             periodStatus.put("booked", isBooked);

//             scheduleList.add(periodStatus);
//         }

//         Map<String, Object> response = new HashMap<>();
//         response.put("classroomId", classroomId);
//         response.put("date", dateStr);
//         response.put("dayLabel", TimetableUtil.dayLabel(dayOfWeek));
//         response.put("schedule", scheduleList);

//         return ResponseEntity.ok(response);
//     }
// }