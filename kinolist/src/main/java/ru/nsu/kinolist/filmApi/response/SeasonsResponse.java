package ru.nsu.kinolist.filmApi.response;

import lombok.Getter;

import java.util.List;

@Getter
public class SeasonsResponse {
    private int total;

    private List<Season> items;
}
