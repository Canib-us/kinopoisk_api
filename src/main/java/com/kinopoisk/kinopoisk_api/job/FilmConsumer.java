package com.kinopoisk.kinopoisk_api.job;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.entity.Subscriber;
import com.kinopoisk.kinopoisk_api.repository.SubscriberRepository;
import com.kinopoisk.kinopoisk_api.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilmConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FilmConsumer.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @JmsListener(destination = "filmsQueue")
    public void spam(List<FilmDTO> films){
        try {
            List<Subscriber> subscribers = subscriberRepository.findAll();
            String xmlContent = emailService.convertToXML(films);
            subscribers.forEach(subscriber -> {
                emailService.sendSimpleMail(
                    subscriber.getEmail(),
                    "EveryDay`s spam",
                    xmlContent
                );
            });
            logger.info("Письма успешно отправлены.");
        } catch (Exception e) {
            logger.error("Ошибка при отправке писем: ", e);
        }

    }
}
