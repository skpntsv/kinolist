package ru.nsu.kinolist.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Film")
public class Film {
    @Id
    @Column(name = "film_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int filmId;

    @Column(name = "name")
    private String filmName;

    @Column(name = "year")
    private Integer releaseYear;

    @Column(name = "url")
    private String url;

    @Column(name = "kinopoisk_id")
    private Integer kinopoiskId;

    @Column(name = "is_series")
    private Boolean isSeries;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "annotation")
    private String annotation;

    @ManyToMany(mappedBy = "wishList")
    private List<Person> wishingPeople;

    @ManyToMany(mappedBy = "trackedList")
    private List<Person> trackingPeople;

    @ManyToMany(mappedBy = "viewedList")
    private List<Person> peopleWhoViewed;

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
