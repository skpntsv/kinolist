package ru.nsu.kinolist.database.entities;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Table(name = "Film")
public class Film {
    @Id
    @Column(name = "film_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int filmId;

    @Column(name = "name")
    String filmName;

    @Column(name = "year")
    Integer releaseYear;

    @Column(name = "url")
    String url;

    @Column(name = "kinopoisk_id")
    Integer kinopoiskId;

    @Column(name = "is_series")
    Boolean isSeries;

    @Column(name = "rating")
    Double rating;

    @Column(name = "annotation")
    String annotation;

    @ManyToMany(mappedBy = "wishList")
    List<Person> wishingPeople;

    @ManyToMany(mappedBy = "trackedList")
    List<Person> trackingPeople;

    @ManyToMany(mappedBy = "viewedList")
    List<Person> peopleWhoViewed;

    public Film() {
    }

    public Film(String filmName, int releaseYear, String url, int kinopoiskId,
                boolean isSeries, double rating, String annotation) {
        this.filmName = filmName;
        this.releaseYear = releaseYear;
        this.url = url;
        this.kinopoiskId = kinopoiskId;
        this.isSeries = isSeries;
        this.rating = rating;
        this.annotation = annotation;
    }
}
