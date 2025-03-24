package com.kinopoisk.kinopoisk_api.job;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.dto.FilmResponse;
import org.json.JSONObject;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FilmFetchJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(FilmFetchJob.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";
    private final String token = "your token";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Запуск задачи filmFetchJob...");
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        String genreId = getGenreForDay(day, context);
        try {
            List<FilmDTO> filmDTOS = fetchFilmsFromApi(genreId);
            sendToActiveMQ(filmDTOS);
            System.out.println("send to mq");
        } catch (Exception e) {
            System.out.println("don`t send to mq");
        }


    }

    private String getGenreForDay(DayOfWeek day, JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JSONObject genresMap = new JSONObject(jobDataMap.getString("genres"));
        return genresMap.getString(day.toString());
    }

    private List<FilmDTO> fetchFilmsFromApi(String genreId){
        List<FilmDTO> allFilms = new ArrayList<>();
        int page = 1;
        int maxFilms = 50;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token);

        while (allFilms.size() < maxFilms) {
            String urlGenres = String.format(
                    "%s?genres=%s&order=RATING&type=FILM&page=%d",
                    url, genreId, page
            );

            ResponseEntity<FilmResponse> response = restTemplate.exchange(
                    urlGenres,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    FilmResponse.class
            );

            FilmDTO[] films = response.getBody().getItems();
            if (films == null || films.length == 0) {
                break;
            }

            int remaining = maxFilms - allFilms.size();
            if (films.length >= remaining) {
                allFilms.addAll(Arrays.asList(Arrays.copyOf(films, remaining)));
                break;
            } else {
                allFilms.addAll(Arrays.asList(films));
            }
            page++;
        }

        return allFilms;
    }

    private void sendToActiveMQ(List<FilmDTO> films){
        jmsTemplate.convertAndSend("filmsQueue", films, message -> {
            message.setStringProperty("_type", "filmList");
            return message;
        });
    }
}
