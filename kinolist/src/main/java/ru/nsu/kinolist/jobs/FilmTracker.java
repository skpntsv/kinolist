package ru.nsu.kinolist.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.database.entities.Person;
import ru.nsu.kinolist.filmApi.response.Episode;
import ru.nsu.kinolist.filmApi.response.Season;
import ru.nsu.kinolist.filmApi.response.SeasonsResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class FilmTracker {
    private final FilmApiService filmApiService;

    private final FilmDAO filmDAO;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String INFO_BOT_TOKEN;

    private final String NOTIFICATION;

    @Autowired
    public FilmTracker(FilmApiService filmApiService, FilmDAO filmDAO, @Value("${bot.token}")String tokken, @Value("${bot.notification}")String notification) {
        this.filmApiService = filmApiService;
        this.filmDAO = filmDAO;
        this.INFO_BOT_TOKEN = tokken;
        NOTIFICATION = notification;
    }

    private final ArrayList<Film> checkedFilmsToday = new ArrayList<>();

    @Scheduled(cron = "0 12 * * * *")
    private void checkInfoForTrackedFilms() {
        List<Film> filmList = filmDAO.getAllFilmsFromTracking();
        for (Film film: filmList) {
            if (checkedFilmsToday.contains(film)) {
                continue;
            }
            Optional<Episode> episode = getInfoByTrackedFilm(film);
            if (episode.isEmpty()) {
                continue;
            }
            checkedFilmsToday.add(film);
            sendToPeople(film, episode.get());
        }
    }

    private void sendToPeople(Film film, Episode episode) {
        List<Person> people = filmDAO.getPeopleByTrackedFilm(film);
        for (Person person: people) {
            sendMessageToBot(person.getChatId(), episode);
        }
    }

    private Optional<Episode> getInfoByTrackedFilm(Film film) {
        SeasonsResponse seasonsResponse = filmApiService.sendRequestForSeries(film.getKinopoiskId());
        System.out.println(film.getFilmName() + " " + seasonsResponse.getItems());
        for (Season season: seasonsResponse.getItems()) {
            for (Episode episode: season.getEpisodes()) {
                if (episode.getReleaseDate() != null && episode.getReleaseDate().isEqual(LocalDate.now()) ) {
                    episode.setFilmName(film.getFilmName());
                    return Optional.of(episode);
                }
            }
        }
        return Optional.empty();
    }
    private void sendMessageToBot(String chatId, Episode episode) {
        String message = NOTIFICATION + "\n"
                +"Сериал: \"" + episode.getFilmName() + "\"\n"
                + "\"" + (episode.getNameRu() != null ? episode.getNameRu() : episode.getNameEn()) + "\", " + episode.getEpisodeNumber() + " серия " + episode.getSeasonNumber() + " сезона.";
      //  System.out.println(INFO_BOT_TOKEN + " TOKKEN");
        String url = "https://api.telegram.org/bot"+ INFO_BOT_TOKEN + "/sendMessage?chat_id="+ chatId + "&text=" + message;
        restTemplate.getForObject(url, String.class);
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void clearCheckedFilms() {
        checkedFilmsToday.clear();
    }

}
