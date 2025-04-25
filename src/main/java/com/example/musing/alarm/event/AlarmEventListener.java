package com.example.musing.alarm.event;


import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlarmEventListener {
    private final AlarmService alarmService;

    @TransactionalEventListener
    @Async
    public void sendAlarm(SentAlarmEvent sentAlarmEvent) {
        Alarm alarm = alarmService.send(sentAlarmEvent.getUser(), sentAlarmEvent.getAlarmType(), sentAlarmEvent.getRelatedUrl());
        alarmService.sendSSE(alarm);
    }
}
