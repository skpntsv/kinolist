package ru.nsu.kinolist.bot.handlers.callbackquery;

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
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.util.FilmMessageBuilder;
import ru.nsu.kinolist.controllers.RandomFilmController;
import ru.nsu.kinolist.controllers.WishListController;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.filmApi.response.Categories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
                return sendAnyRandomFilm(callbackQuery.getMessage().getChatId());
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

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Случайний из желаемых",
                CallbackQueryType.RANDOM.name() + "|" + CallbackQueryType.WISHLIST.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Случайный по всему интернету",
                CallbackQueryType.RANDOM.name() + "|" + CallbackQueryType.WORLD.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(editedMessage, editMessageReplyMarkup);
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendRandomFilmFromWishList(Long chatId) {
        Film film = wishListController.getRandomFilmByUser(String.valueOf(chatId));
        // Отправка изображения фильма
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        return List.of(sendPhoto, MessagesService.createMessageTemplate(chatId, FilmMessageBuilder.buildFilmString(film)));
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendAnyRandomFilm(Long chatId) {
        Film film = randomFilmController.getRandomFilm(new Categories());

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        return List.of(sendPhoto, MessagesService.createMessageTemplate(chatId, FilmMessageBuilder.buildFilmString(film)));
    }
}
