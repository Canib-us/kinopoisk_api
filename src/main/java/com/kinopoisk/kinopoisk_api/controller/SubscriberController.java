package com.kinopoisk.kinopoisk_api.controller;

import com.kinopoisk.kinopoisk_api.entity.Subscriber;
import com.kinopoisk.kinopoisk_api.service.SubscriberService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/follow")
public class SubscriberController {
    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/new")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody String email) {
        Subscriber subscriber = subscriberService.addSubscriber(email);
        return ResponseEntity.ok(subscriber);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Subscriber>> getSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscribers();
        return ResponseEntity.ok(subscribers);
    }
}
