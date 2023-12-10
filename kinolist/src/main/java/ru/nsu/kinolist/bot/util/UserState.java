package ru.nsu.kinolist.bot.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserState {
    private BotState botState;
    private int filmId; // Имя фильма, с которым работает пользователь

    public UserState(BotState botState) {
        this.botState = botState;
    }

}

