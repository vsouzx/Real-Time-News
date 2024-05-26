package br.com.souza.realtimenews.controller;

import br.com.souza.realtimenews.service.NotificationService;
import br.com.souza.realtimenews.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NewsController {

    @Autowired
    private SseService service;

    @Autowired
    private NotificationService notificationService;

    public Map<Integer, SseEmitter> emitters = new HashMap<>();

    @CrossOrigin
    @GetMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@RequestParam("identificador") Integer identificador) {
        return service.createSseConnection(identificador, emitters);
    }

    @PostMapping(value = "/new-follow")
    public void newFollow(@RequestParam("identificador") Integer identificador,
                          @RequestParam("username") String username) {
        notificationService.createFollowNotification(identificador, username, emitters);
    }

    @PostMapping(value = "/new-like")
    public void newLike(@RequestParam("identificador") Integer identificador,
                        @RequestParam("username") String username) {
        notificationService.createLikeNotification(identificador, username, emitters);
    }
}
