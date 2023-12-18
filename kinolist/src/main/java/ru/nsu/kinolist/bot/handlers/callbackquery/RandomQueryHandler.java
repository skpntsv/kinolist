package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import ru.nsu.kinolist.filmApi.response.FilmResponseByRandom;

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

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Поиск по вишлисту",
                CallbackQueryType.RANDOM.name() + "|" + CallbackQueryType.WISHLIST.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Поиск по всему интернету",
                CallbackQueryType.RANDOM.name() + "|" + CallbackQueryType.WORLD.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        messages.add(editedMessage);
        messages.add(editMessageReplyMarkup);
        return messages;
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendRandomFilmFromWishList(Long chatId) {
        Film film = wishListController.getRandomFilmByUser(String.valueOf(chatId));
        // Отправка изображения фильма
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        return List.of(MessagesService.createMessageTemplate(chatId, FilmMessageBuilder.buildFilmString(film)), sendPhoto);
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendAnyRandomFilm(Long chatId) {
        Film movie = randomFilmController.getRandomFilm(new Categories());
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        // Добавление постера
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(movie.getUrl()));
        messages.add(sendPhoto);

        // Добавления описания
        StringBuilder messageText = new StringBuilder();
        messageText.append("Фильм: ").append(movie.getFilmName()).append(" (").append(movie.getReleaseYear()).append(")\n");
        messageText.append("Жанры: ").append(movie.getGenre()).append("\n");
        messageText.append("Рейтинг (Kinopoisk): ").append(movie.getRating()).append("\n");
        //messageText.append("Рейтинг (IMDb): ").append(movie.getRatingImdb()).append("\n");
        messageText.append("Тип: ").append(movie.getIsSeries() ? "Сериал" : "Фильм").append("\n");

        // Создаем текстовое сообщение и добавляем его в список сообщений
        SendMessage message = new SendMessage();
        message.setText(messageText.toString());
        message.setChatId(chatId);
        messages.add(message);

        return messages;
    }
}
