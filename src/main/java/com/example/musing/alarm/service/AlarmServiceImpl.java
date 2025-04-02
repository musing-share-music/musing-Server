package com.example.musing.alarm.service;

import com.example.musing.alarm.dto.AlarmDto;
import com.example.musing.alarm.entity.Alarm;
import com.example.musing.alarm.entity.AlarmType;
import com.example.musing.alarm.repository.AlarmRepository;
import com.example.musing.alarm.repository.EmitterRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.musing.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final EmitterRepository emitterRepository;
    private final Long timeoutMillis = 600_000L;
    private final String REPLY_CONTEMT = "게시글에 별점이 달렸어요.";
    private final String ADMINPERMIT_CONTEMT = "작성하신 게시글의 관리자 확인이 완료되었어요.";
    private final String ADMINDENY_CONTENT = "작성하신 게시글의 관리자 확인이 거절되었어요.";
    private final String APPLYPERMIT_CONTENT = "승인 요청이 접수된 게시글이 있어요.";

    @Transactional
    @Override
    public Alarm redirectAlarmUrl(String userId, long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_ALARM));

        if (alarm.isRead() || !Objects.equals(alarm.getUser().getId(), userId)){
            throw new CustomException(INVALID_ACCESS);
        }

        alarmStatusIsReadTrue(alarm);

        return alarm;
    }

    @Override
    public List<AlarmDto> findAlarms(String userId) {
        return alarmRepository.findByUserId(userId)
                .stream()
                .map(AlarmDto::from)
                .toList();
    }

    // SSE Connection Pool의 Active 할당을 최소화 하기위해 @Transactional 제거
    @Override
    public SseEmitter subscribe(String userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeoutMillis));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludeId(userId);
        sendAlarm(emitter, eventId,
                emitterId, "EventStream Created. [userId=" + userId + "]");

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    @Transactional
    @Override
    public void send(User user, AlarmType alarmType, String relatedUrl) {
        Alarm alarm = alarmRepository
                .save(createNotification(user, alarmType, relatedUrl));

        String userId = String.valueOf(user.getId());
        String eventId = makeTimeIncludeId(user.getId());
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterByUserId(userId);
        emitters.forEach(
                (id, emitter) -> {
                    emitterRepository.saveEventCache(id, alarm);
                    sendAlarm(emitter, eventId, id,
                            AlarmDto.from(alarm));
                }
        );
    }

    private void alarmStatusIsReadTrue(Alarm alarm) {
        alarm.read();
    }

    private String makeTimeIncludeId(String id) {
        return id + "_" + System.currentTimeMillis();
    }

    private void sendAlarm(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository
                .findAllEventCacheByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendAlarm(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private Alarm createNotification(User user, AlarmType type, String url) {
        if (type == AlarmType.REPLY) {
            return Alarm.of(REPLY_CONTEMT, url, type, user);
        }
        if (type == AlarmType.APPLYPERMIT) {
            return Alarm.of(APPLYPERMIT_CONTENT, url, type, user);
        }
        if (type == AlarmType.ADMINPERMIT) {
            return Alarm.of(ADMINPERMIT_CONTEMT, url, type, user);
        }
        if (type == AlarmType.ADMINDENY) {
            return Alarm.of(ADMINDENY_CONTENT, url, type, user);
        }
        throw new CustomException(NOT_FOUND_ALARM_TYPE);
    }
}