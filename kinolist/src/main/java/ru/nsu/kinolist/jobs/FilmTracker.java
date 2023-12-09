package ru.nsu.kinolist.jobs;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.response.SeasonsResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;
import ru.nsu.kinolist.service.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@EnableScheduling
public class FilmTracker {
    private FilmApiService filmApiService;
    private FilmDAO filmDAO;
    private TrackedListController trackedListController;

    //TODO last message

    @Scheduled(cron = "0 0 * * * *")
    void checkInfoForTrackedFilms() {
        List<Film> filmList = filmDAO.getAllFilmsFromTracking();
        for (Film film: filmList) {
            Optional<String> message = getInfoByTrackedFilm(film);
            if (message.isEmpty()) {
                continue;
            }
            sendToPeople(film, message.get());
        }
    }

    private void sendToPeople(Film film, String message) {
        List<Person> people = filmDAO.getPeopleByTrackedFilm(film);
        for (Person person: people) {
            trackedListController.sendMessage(person.getChatId(), message);
        }
    }

    private Optional<String> getInfoByTrackedFilm(Film film) {
        SeasonsResponse filmResponse = filmApiService.sendRequestForSeries(film.getKinopoiskId()).get();
        //TODO parsing and receive info about new series
        return Optional.empty();
    }
}
