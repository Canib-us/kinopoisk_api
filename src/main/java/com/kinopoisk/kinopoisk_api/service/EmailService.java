package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("example@yandex.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public String convertToXML (List<FilmDTO> films) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<films>\n");
        for (FilmDTO film : films) {
            xml.append("<film>\n")
                    .append("<name>").append(escapeXML(film.getNameRu())).append("</name>\n")
                    .append("<year>").append(film.getYear()).append("</year>\n")
                    .append("<rating>").append(film.getRatingKinopoisk()).append("</rating>\n")
                    .append("<description>").append(escapeXML(film.getShortDescription())).append("</description>\n")
                    .append("</film>\n");
        }
        xml.append("</films>");
        return xml.toString();
    }

    private String escapeXML(String value) {
        if(value == null) return null;
        return value.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&apos;");

    }
}
