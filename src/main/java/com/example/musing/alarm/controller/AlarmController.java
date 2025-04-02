package com.example.musing.alarm.controller;

import com.example.musing.alarm.dto.AlarmDto;
import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.service.AlarmService;
import com.example.musing.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "알람 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 클릭 redirect",
            description = "알람을 클릭 할 때, 읽음 처리하기 위해 넣어놨습니다." +
                    "읽음 처리를 제거할 예정이라 일단 사용하지 않습니다. 삭제할 예정입니다.")
    @PutMapping
    public void redirectAlarmUrl(@RequestParam long alarmId, HttpServletResponse response) throws IOException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Alarm alarm = alarmService.redirectAlarmUrl(userId, alarmId);

        response.sendRedirect(alarm.getUrlLink());
    }

    @Operation(summary = "알람 SSE 연결",
            description = "사용자에게 알람을 전달하기 위해 사용됩니다.")
    @GetMapping(value ="/create", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subcribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        SseEmitter sseEmitter = alarmService.subscribe(userId, lastEventId);
        return ResponseEntity.ok(sseEmitter);
    }
}
