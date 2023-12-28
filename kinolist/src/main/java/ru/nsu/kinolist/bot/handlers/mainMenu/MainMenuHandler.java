package ru.nsu.kinolist.bot.handlers.mainMenu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.InputMessageHandler;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

@Component
public class MainMenuHandler implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private UserDataCache userDataCache;

    public MainMenuHandler(MainMenuService mainMenuService, UserDataCache userDataCache) {
        this.mainMenuService = mainMenuService;
        this.userDataCache = userDataCache;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        userDataCache.removeUsersCache(message.getChatId());
        userDataCache.removeCurrentMovieListOfUser(message.getChatId());
        return mainMenuService.getMainMenuMessage(message.getChatId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }
}
