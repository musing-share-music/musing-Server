package com.example.musing.alarm.controller;

import com.example.musing.alarm.dto.AlarmDto;
import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.service.AlarmService;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @PutMapping
    public void redirectAlarmUrl(@RequestParam long alarmId, HttpServletResponse response) throws IOException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Alarm alarm = alarmService.redirectAlarmUrl(userId, alarmId);

        response.sendRedirect(alarm.getUrlLink());
    }

    @GetMapping(value ="/create", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subcribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        SseEmitter sseEmitter = alarmService.subscribe(userId, lastEventId);
        return ResponseEntity.ok(sseEmitter);
    }
}
