package ru.nsu.kinolist.utils;

public enum GenreType {
    THRILLER("Триллер", 1),
    DRAMA("Драма", 2),
    CRIMINAL("Криминал", 3),
    MELODRAMA("Мелодрама", 4),
    DETECTIVE("Детектив", 5),
    FANTASTIC("Фантастика", 6),
    ADVENTURES("Приключения", 7),
    BIOGRAPHY("Биография", 8),
    NOIR("Нуар", 9),
    WESTERN("Вестерн", 10),
    ACTION("Боевик", 11),
    FANTASY("Фэнтези", 12),
    COMEDY("Комедия", 13),
    MILITARY("Военнный", 14),
    HISTORY("История", 15),
    MUSIC("Музыка", 16),
    HORROR("Ужасы", 17),
    CARTOON("Мультфильм", 18),
    FAMILY("Семейный", 19),
    ANIME("Аниме", 24);

    GenreType(String name, int id) {}
}
