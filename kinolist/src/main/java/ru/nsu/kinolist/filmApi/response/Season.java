package ru.nsu.kinolist.filmApi.response;

import lombok.Getter;

import java.util.List;

@Getter
public class Season {
    int number;
    List<Episode> episodes;
}
