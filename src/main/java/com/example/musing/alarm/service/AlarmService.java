package com.example.musing.alarm.service;

import com.example.musing.alarm.dto.AlarmDto;
import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.entity.AlarmType;
import com.example.musing.user.entity.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface AlarmService {

    Alarm redirectAlarmUrl(String userId, long alarmId);

    SseEmitter subscribe(String userId, String lastEventId);

    void send(User user, AlarmType alarmType, String relatedUrl);
}
