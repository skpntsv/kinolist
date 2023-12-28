package ru.nsu.kinolist.bot.handlers.playlists.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.handlers.CallbackQueryHandler;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.handlers.playlists.PlaylistsMenuService;

import java.io.Serializable;
import java.util.List;

@Component
public class ShowPlaylistHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SHOW_PLAYLIST;

    private final PlaylistsMenuService playlistsService;

    public ShowPlaylistHandler(PlaylistsMenuService playlistsService) {
        this.playlistsService = playlistsService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        return playlistsService.getPlaylistMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }
}
