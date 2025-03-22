package com.kinopoisk.kinopoisk_api.job;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.dto.FilmResponse;
import org.json.JSONObject;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmFetchJob extends QuartzJobBean {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";
    private final String token = "2b2d1166-abdd-4279-8f11-99bee436f6a8";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        String genreId = getGenreForDay(day, context);

        List<FilmDTO> filmDTOS = fetchFilmsFromApi(genreId);
        sendToActiveMQ(filmDTOS);
    }

    private String getGenreForDay(DayOfWeek day, JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JSONObject genresMap = new JSONObject(jobDataMap.getString("genres"));
        return genresMap.getString(day.toString());
    }

    private List<FilmDTO> fetchFilmsFromApi(String genreId){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String urlGenres = String.format("%s?genres=%s&order=RATING&type=FILM&page=1",
                url, genreId);
        ResponseEntity<FilmResponse> response = restTemplate.exchange(urlGenres, HttpMethod.GET, entity, FilmResponse.class);
        return Arrays.stream(response.getBody().getItems())
                .limit(50)
                .collect(Collectors.toList());
    }

    private void sendToActiveMQ(List<FilmDTO> filmDTOS){
        jmsTemplate.convertAndSend("filmQueue", filmDTOS);
    }
}
