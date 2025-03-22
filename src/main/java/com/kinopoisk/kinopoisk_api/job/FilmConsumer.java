package com.kinopoisk.kinopoisk_api.job;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.entity.Subscriber;
import com.kinopoisk.kinopoisk_api.repository.SubscriberRepository;
import com.kinopoisk.kinopoisk_api.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilmConsumer {
    @Autowired
    private EmailService emailService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @JmsListener(destination = "filmQueue")
    public void spam(List<FilmDTO> filmDTOS){
        List<Subscriber> subscribers = subscriberRepository.findAll();
        String xmlContent = emailService.convertToXML(filmDTOS);
        subscribers.forEach(subscriber -> {
            emailService.sendSimpleMail(
                    subscriber.getEmail(),
                    "EveryDay`s spam",
                    xmlContent
            );
        });
    }
}
