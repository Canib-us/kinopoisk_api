package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.util.FilmListWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your email");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public String convertToXML (List<FilmDTO> films) {
        try {
            FilmListWrapper wrapper = new FilmListWrapper();
            wrapper.setFilms(films);

            JAXBContext context = JAXBContext.newInstance(FilmListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(wrapper, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Ошибка преобразования в XML", e);
        }
    }
}



