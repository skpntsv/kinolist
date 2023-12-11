package ru.nsu.kinolist.filmApi.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Categories {
    private int genre = 0;

    private String order = "RATING";

    private String type = "ALL";

    private int ratingFrom = 5;

    private int ratingTo = 10;

    private int yearFrom = 1960;

    private int yearTo = LocalDate.now().getYear();
}
