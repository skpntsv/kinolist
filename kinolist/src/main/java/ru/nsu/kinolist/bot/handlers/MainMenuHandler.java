package ru.nsu.kinolist.bot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

@Component
public class MainMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;

    public MainMenuHandler(MainMenuService mainMenuService) {
        this.mainMenuService = mainMenuService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }
}
