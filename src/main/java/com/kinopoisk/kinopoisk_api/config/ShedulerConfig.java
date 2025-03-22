package com.kinopoisk.kinopoisk_api.config;

import com.kinopoisk.kinopoisk_api.job.FilmFetchJob;
import org.json.JSONObject;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.util.Map;

@Configuration
public class ShedulerConfig {

    private static final Map<DayOfWeek, String> genreByDayOfWeek = Map.of(
            DayOfWeek.MONDAY, "1",
            DayOfWeek.TUESDAY, "2",
            DayOfWeek.WEDNESDAY, "6",
            DayOfWeek.THURSDAY, "11",
            DayOfWeek.FRIDAY, "13",
            DayOfWeek.SATURDAY, "24",
            DayOfWeek.SUNDAY, "18"
    );

    @Bean
    public JobDetail filmFetchJobDetail(){
        return JobBuilder.newJob(FilmFetchJob.class)
                .withIdentity("filmFetchJob")
                .usingJobData("genres", new JSONObject(genreByDayOfWeek).toString())
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger filmFetchTrigger(JobDetail jobDetail){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("filmFetchTrigger")
                //.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(7, 0))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
    }

}
