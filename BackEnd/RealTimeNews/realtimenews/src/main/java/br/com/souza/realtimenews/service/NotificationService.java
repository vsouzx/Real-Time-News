package br.com.souza.realtimenews.service;

import br.com.souza.realtimenews.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SseService sseService;

    public void createFollowNotification(Integer identificador, String username, Map<Integer, SseEmitter> emitters){
        sseService.notifyFrontend(identificador, NotificationType.NEW_FOLLOW.name(), username, emitters);
    }

    public void createLikeNotification(Integer identificador, String username, Map<Integer, SseEmitter> emitters){
        sseService.notifyFrontend(identificador, NotificationType.NEW_LIKE.name(), username, emitters);
    }
}
