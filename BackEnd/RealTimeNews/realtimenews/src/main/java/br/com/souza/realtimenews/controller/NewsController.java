package br.com.souza.realtimenews.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NewsController {

    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @CrossOrigin
    @GetMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(){
        SseEmitter sseEmitter = new SseEmitter(30_000L);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitter.onCompletion(() -> {
            emitters.remove(sseEmitter);
            System.out.println("completion");
        });
        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
            emitters.remove(sseEmitter);
            System.out.println("timeout");
        });
        sseEmitter.onError((e) -> {
            sseEmitter.completeWithError(e);
            emitters.remove(sseEmitter);
            System.out.println("onerror");
        });
        emitters.add(sseEmitter);

        return sseEmitter;
    }

    @PostMapping(value = "/new")
    public void dispatchEventToClients(@RequestParam("title") String title, @RequestParam("text") String text){

        String eventFormatted = new JSONObject()
                .put("title", title)
                .put("text", text)
                .toString();

        for (SseEmitter emitter : emitters){
            try {
                emitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
                emitter.complete();
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
