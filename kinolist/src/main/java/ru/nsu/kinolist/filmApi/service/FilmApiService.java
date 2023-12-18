package ru.nsu.kinolist.filmApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nsu.kinolist.filmApi.response.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Component
public class FilmApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final HttpHeaders headers;

    @Autowired
    public FilmApiService(HttpHeaders headers) {
        this.headers = headers;
    }

    public Optional<FilmResponse> sendRequestByName(String filmName) {
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword={word}";
        SearchResponse results = restTemplate.exchange(url, HttpMethod.GET, request, SearchResponse.class, filmName).getBody();
        if (Objects.requireNonNull(results).getSearchFilmsCountResult() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(results.getFilms().get(0));
    }

    public SeasonsResponse sendRequestForSeries(int id) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/{id}/seasons";
        return restTemplate.exchange(url, HttpMethod.GET, request, SeasonsResponse.class, id).getBody();
    }

    public FilmResponseByRandom sendRequestForRandomFilm(Categories categories) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films?order={o}&type={t}&ratingFrom={r1}&ratingTo={r2}&yearFrom={y1}&yearTo={y2}&page={p}";
        String genres = "&genres=";
        if (categories.getGenre() != 0) {
            url = url + genres + categories.getGenre();
        }

        RandomFilmsResponse randomFilmsResponse = restTemplate.exchange(url, HttpMethod.GET, request, RandomFilmsResponse.class,
                categories.getOrder(), categories.getType(), categories.getRatingFrom(), categories.getRatingTo(), categories.getYearFrom(), categories.getYearTo(), 1).getBody();
        int pages = randomFilmsResponse.getTotalPages();
        int filmsOnPage = randomFilmsResponse.getTotal() / randomFilmsResponse.getTotalPages();
        Random random = new Random();
        int page = random.nextInt(pages) + 1;
        int filmIdx =random.nextInt(filmsOnPage);

        randomFilmsResponse = restTemplate.exchange(url, HttpMethod.GET, request, RandomFilmsResponse.class,
                categories.getOrder(), categories.getType(), categories.getRatingFrom(), categories.getRatingTo(), categories.getYearFrom(), categories.getYearTo(), page).getBody();
        FilmResponseByRandom filmResponseByRandom = randomFilmsResponse.getItems().get(filmIdx);
        return filmResponseByRandom;
    }

    public String sendRequestForDescrById(int kinopoiskId) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/{id}";
        return Objects.requireNonNull(restTemplate.exchange(url, HttpMethod.GET, request, FilmResponse.class, kinopoiskId).getBody()).getDescription();
    }
}
