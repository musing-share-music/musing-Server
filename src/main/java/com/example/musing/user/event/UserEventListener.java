package com.example.musing.user.event;

import com.example.musing.auth.oauth2.service.Oauth2ProviderTokenService;
import com.example.musing.playlist.event.DeleteVideoEvent;
import com.example.musing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;


@RequiredArgsConstructor
@Component
public class UserEventListener {
    private final Oauth2ProviderTokenService oauth2ProviderTokenService;

    @TransactionalEventListener
    @Async
    public void disconnectThirdPartyService(String userId) throws IOException, InterruptedException {
        oauth2ProviderTokenService.disconnectThirdPartyService(userId);
    }
}
