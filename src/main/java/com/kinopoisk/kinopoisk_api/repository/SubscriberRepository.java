package com.kinopoisk.kinopoisk_api.repository;

import com.kinopoisk.kinopoisk_api.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

}
