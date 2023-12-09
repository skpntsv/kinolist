package ru.nsu.kinolist.filmApi.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.response.SearchResponse;
import ru.nsu.kinolist.filmApi.response.SeasonsResponse;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public class FilmApiService {

    private final RestTemplate restTemplate = new RestTemplate();
   // private String url = "URL";
    private final String apiKey = "43e4ac25-ce79-4347-beec-ba74c9d2165e";
    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword={word}";
    private HttpHeaders headers;
    public FilmApiService() {
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", apiKey);
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
        SeasonsResponse results = restTemplate.exchange(url, HttpMethod.GET, request, SeasonsResponse.class, id).getBody();
        return results;

    }
}
