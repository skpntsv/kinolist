package ru.nsu.kinolist.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "chat_id")
    private String chatId;

    @ManyToMany
    @JoinTable(
            name = "Person_Film_Wish",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> wishList;

    @ManyToMany
    @JoinTable(
            name = "Person_Film_Tracked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> trackedList;

    @ManyToMany
    @JoinTable(
            name = "Person_Film_Viewed",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    private List<Film> viewedList;

    public Person() {
    }

    public Person(String chatId) {
        this.chatId = chatId;
    }
}
