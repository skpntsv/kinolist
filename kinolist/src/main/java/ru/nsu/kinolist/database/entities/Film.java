package ru.nsu.kinolist.database.entities;

import javax.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "Film")
public class Film {
    @Id
    @Column(name = "film_id")
    int filmId;
    @Column(name = "name")
    String filmName;
    @Column(name = "year")
    String releaseYear;
    @Column(name = "url")
    String url;
    @Column(name = "kinopoisk_id")
    String kinopoiskId;
    @Column(name = "is_series")
    String isSeries;
    @Column(name = "rating")
    String rating;
    @Column(name = "annotation")
    String annotation;
    @ManyToMany(mappedBy = "wishList")
    List<Person> wishingPeople;
    @ManyToMany(mappedBy = "trackedList")
    List<Person> trackingPeople;
    @ManyToMany(mappedBy = "viewedList")
    List<Person> peopleWhoViewed;
}
