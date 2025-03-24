package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.entity.Subscriber;
import com.kinopoisk.kinopoisk_api.repository.SubscriberRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubscriberService {
    @Autowired
    private SubscriberRepository subscriberRepository;

    public Subscriber addSubscriber(String email) {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email);
        return subscriberRepository.save(subscriber);
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }


}
