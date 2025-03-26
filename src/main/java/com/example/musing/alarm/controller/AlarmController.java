package com.example.musing.alarm.controller;

import com.example.musing.alarm.service.AlarmService;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping(value ="/create", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseDto<SseEmitter> subcribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        SseEmitter sseEmitter = alarmService.subscribe(userId, lastEventId);
        return ResponseDto.of(sseEmitter);
    }
}
