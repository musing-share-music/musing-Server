package com.example.musing.alarm.dto;

import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.entity.AlarmType;
import com.example.musing.user.entity.User;
import lombok.Builder;

@Builder
public record AlarmDto(
        String content,

        String urlLink,

        boolean isRead,

        AlarmType alarmType) {

    public static AlarmDto from(Alarm alarm) {
        return AlarmDto.builder()
                .content(alarm.getContent())
                .urlLink(alarm.getUrlLink())
                .isRead(alarm.isRead())
                .alarmType(alarm.getAlarmType())
                .build();
    }
}
