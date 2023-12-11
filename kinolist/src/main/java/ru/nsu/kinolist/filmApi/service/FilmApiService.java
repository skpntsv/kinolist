package ru.nsu.kinolist.filmApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.response.SearchResponse;
import ru.nsu.kinolist.filmApi.response.SeasonsResponse;

import java.util.Objects;
import java.util.Optional;

@Component
public class FilmApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword={word}";

    private final HttpHeaders headers;

    @Autowired
    public FilmApiService(HttpHeaders headers) {
        this.headers = headers;
    }

    public Optional<FilmResponse> sendRequestByName(String filmName) {
        HttpEntity<String> request = new HttpEntity<>(headers);

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

}
