package ru.nsu.kinolist.bot.handlers.random;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.handlers.CallbackQueryHandler;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.util.ParseQueryData;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.util.FilmMessageBuilder;
import ru.nsu.kinolist.controllers.RandomFilmController;
import ru.nsu.kinolist.controllers.WishListController;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.filmApi.response.Categories;
import ru.nsu.kinolist.utils.GenreType;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Component
public class RandomQueryHandler implements CallbackQueryHandler {
    private final RandomFilmController randomFilmController;
    private final WishListController wishListController;
    @Autowired
    public RandomQueryHandler(RandomFilmController randomFilmController, WishListController wishListController) {
        this.randomFilmController = randomFilmController;
        this.wishListController = wishListController;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        if (ParseQueryData.hasRandomList(callbackQuery)) {
            if (Objects.equals(ParseQueryData.parseListRandom(callbackQuery), CallbackQueryType.WISHLIST.name())) {

                return sendRandomFilmFromWishList(callbackQuery.getMessage().getChatId());
            } else if (Objects.equals(ParseQueryData.parseListRandom(callbackQuery), CallbackQueryType.WORLD.name())) {
                if (ParseQueryData.hasRandomGenre(callbackQuery)) {
                    GenreType genre = GenreType.valueOf(ParseQueryData.parseGenreRandom(callbackQuery));
                    return sendAnyRandomFilmByGenre(callbackQuery.getMessage().getChatId(), genre);
                } else {
                    return sendChoiceGenre(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
                }
            } else {
                log.info("Неизвестный запрос от пользователя при выборе рандомного фильма {}", callbackQuery.getData());

                return List.of(MessagesService.createMessageTemplate(callbackQuery.getMessage().getChatId(), "Что-то пошло не так, попробуйте ещё раз"));
            }
        }

        log.info("user[{}] call random menu", callbackQuery.getMessage().getChatId());
        return whereToSearch(callbackQuery);
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.RANDOM;
    }

    private List<PartialBotApiMethod<? extends Serializable>> whereToSearch(CallbackQuery callbackQuery) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(callbackQuery.getMessage().getChatId());
        editedMessage.setMessageId(callbackQuery.getMessage().getMessageId());

        editedMessage.setText("Откуда будем искать фильмы?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Случайный из желаемых",
                ParseQueryData.createCallbackData(CallbackQueryType.RANDOM.name(), CallbackQueryType.WISHLIST.name()))));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Случайный по всему интернету с любым жанром",
                ParseQueryData.createCallbackData(CallbackQueryType.RANDOM.name(), CallbackQueryType.WORLD.name(), GenreType.ALL.name()))));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Случайный по всему интернету по жанру",
                ParseQueryData.createCallbackData(CallbackQueryType.RANDOM.name(), CallbackQueryType.WORLD.name()))));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(editedMessage, editMessageReplyMarkup);
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendRandomFilmFromWishList(Long chatId) {
        Optional<Film> movie = wishListController.getRandomFilmByUser(String.valueOf(chatId));
        if (movie.isEmpty()) {
            return List.of(MessagesService.createMessageTemplate(chatId, "Ваш список желаемого пуст! Сначала добавьте туда что-нибудь"));
        }
        Film film = movie.get();
        // Отправка изображения фильма
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        return List.of(sendPhoto, MessagesService.createMessageTemplate(chatId, FilmMessageBuilder.buildFilmString(film)));
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendChoiceGenre(Long chatId, Integer messageId) {
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        for (GenreType genre : GenreType.values()) {
            row.add(MessagesService.getButton(genre.getName(), ParseQueryData.createCallbackData(CallbackQueryType.RANDOM.name(), CallbackQueryType.WORLD.name(), genre.name())));

            if (row.size() == 3) {
                inlineKeyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            inlineKeyboard.add(row);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);
        editedMessage.setText("Что вы предпочитаете?");

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(editedMessage, editMessageReplyMarkup);
    }


    private List<PartialBotApiMethod<? extends Serializable>> sendAnyRandomFilmByGenre(Long chatId, GenreType genre) {
        Film film = randomFilmController.getRandomFilm(new Categories(genre.getId()));

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        return List.of(sendPhoto, MessagesService.createMessageTemplate(chatId, FilmMessageBuilder.buildFilmString(film)));
    }
}
