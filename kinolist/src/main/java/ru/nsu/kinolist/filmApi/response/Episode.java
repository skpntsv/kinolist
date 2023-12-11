package ru.nsu.kinolist.filmApi.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Episode {
    private int seasonNumber;

    private int episodeNumber;

    private String nameRu;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Setter
    private String filmName;
}
