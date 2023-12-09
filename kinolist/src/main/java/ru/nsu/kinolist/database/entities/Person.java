package ru.nsu.kinolist.database.entities;

import javax.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int userId;
    @Column(name = "chat_id")
    String chatId;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Person_Film_Wish",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    List<Film> wishList;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Person_Film_Tracked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    List<Film> trackedList;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Person_Film_Viewed",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    List<Film> viewedList;

    public Person() {
    }

    public Person(String chatId) {
        this.chatId = chatId;
    }
}
