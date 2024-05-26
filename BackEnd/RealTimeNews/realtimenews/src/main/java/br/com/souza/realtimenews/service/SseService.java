package br.com.souza.realtimenews.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Objects;

@Service
public class SseService {

    public SseEmitter createSseConnection(Integer identificador, Map<Integer, SseEmitter> emitters){
        SseEmitter sseEmitter = instanciarNovoEmitter(identificador, emitters);

        sseEmitter.onCompletion(() -> {
            emitters.remove(identificador);
            System.out.println("completion");
        });

        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
            emitters.remove(identificador);
            System.out.println("timeout");
        });

        sseEmitter.onError((e) -> {
            sseEmitter.completeWithError(e);
            emitters.remove(identificador);
            System.out.println("onerror");
        });

        return sseEmitter;
    }

    public void notifyFrontend(Integer userToBeNotified, String notificationType, String username, Map<Integer, SseEmitter> emitters){
        String event = new JSONObject()
                .put("username", username)
                .toString();

        SseEmitter emitter = emitters.get(userToBeNotified);
        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(SseEmitter.event().name(notificationType).data(event));
            } catch (Exception e) {
                emitter.completeWithError(e);
                emitters.remove(userToBeNotified);
            }
        }
    }

    private SseEmitter instanciarNovoEmitter(Integer identificador, Map<Integer, SseEmitter> emitters) {
        emitters.remove(identificador);
        SseEmitter sseEmitter = new SseEmitter();
        emitters.put(identificador, sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("OK").data("ok"));
        } catch (Exception e) {
            sseEmitter.completeWithError(e);
            emitters.remove(identificador);
        }

        return sseEmitter;
    }
}
