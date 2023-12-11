package ru.nsu.kinolist.bot.util;

public enum BotState {
    START,
    SHOW_MAIN_MENU,
    SHOW_HELP,
    SHOW_PLAYLISTS_MENU,
    IDLE,             // Начальное состояние
    AWAITING_INPUT,    // Ожидание ввода от пользователя
    WORK_ON_WISHLIST,
    WORK_ON_WATCHEDLIST,
    WORK_ON_TRACKEDLIST
}
