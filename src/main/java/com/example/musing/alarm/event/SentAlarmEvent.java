package com.example.musing.alarm.event;

import com.example.musing.alarm.entity.AlarmType;
import com.example.musing.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SentAlarmEvent {
    private User user;
    private AlarmType alarmType;
    private String relatedUrl;

    public static SentAlarmEvent of(User user, AlarmType alarmType, String relatedUrl){
        return SentAlarmEvent.builder()
                .user(user)
                .alarmType(alarmType)
                .relatedUrl(relatedUrl)
                .build();
    }
}
