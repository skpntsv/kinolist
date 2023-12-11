package ru.nsu.kinolist.jobs;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.filmApi.response.Episode;
import ru.nsu.kinolist.filmApi.response.Season;
import ru.nsu.kinolist.filmApi.response.SeasonsResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;


import java.time.LocalDate;
import java.util.List;
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
            Optional<Episode> episode = getInfoByTrackedFilm(film);
            if (episode.isEmpty()) {
                continue;
            }
            sendToPeople(film, episode.get());
        }
    }

    private void sendToPeople(Film film, Episode episode) {
        List<Person> people = filmDAO.getPeopleByTrackedFilm(film);
        for (Person person: people) {
           //TODO
            // trackedListController.sendMessage(person.getChatId(), episode);
        }
    }

    private Optional<Episode> getInfoByTrackedFilm(Film film) {
        SeasonsResponse seasonsResponse = filmApiService.sendRequestForSeries(film.getKinopoiskId()).get();
        for (Season season: seasonsResponse.getItems()) {
            for (Episode episode: season.getEpisodes()) {
                if (episode.getReleaseDate().isEqual(LocalDate.now()) ) {
                    episode.setFilmName(film.getFilmName());
                    return Optional.of(episode);
                }
            }
        }
        return Optional.empty();
    }
}
