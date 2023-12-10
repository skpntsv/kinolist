package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.kinolist.bot.util.BotState;
import ru.nsu.kinolist.bot.util.UserState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Component
@Slf4j
public class KinoListBot extends TelegramLongPollingBot {
    private Map<Long, BotState> userStates = new ConcurrentHashMap<>();
    private static final String WISHLIST = "Список желаемого";
    private static final String WATCHED_LIST = "Список просмотренного";
    private static final String TRACKED_LIST = "Список отслеживаемого";

    @Value("${bot.name}")
    private String botName;

    public KinoListBot(@Value("${bot.token}") String botToken) {
        super(botToken);

        this.addBotCommands();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update request) {
        if (request.hasMessage()) {
            if (request.hasCallbackQuery()) {
                log.info("get callback {}", request.getCallbackQuery().getData());
                onCallbackQueryReceived(request.getCallbackQuery());
            } else {
                Message message = request.getMessage();
                if (message != null && message.hasText()) {
                    log.info("Working onUpdateReceived, request text[{}] from @{} - {}",
                            request.getMessage().getText(), request.getMessage().getChat().getUserName(), request.getMessage().getChatId());
                    onInputMessage(message);
                }
            }
        }
    }

    private void onInputMessage(Message message) {
        log.info("Working onUpdateReceived, request text[{}] from @{} - {}",
                message.getText(), message.getChat().getUserName(), message.getChatId());

        Long chatId = message.getChatId();

        switch (message.getText()) {
            case START_COMMAND -> {
                String userName = message.getChat().getFirstName();
                startCommand(chatId, userName);
            }
            case HELP_COMMAND -> helpCommand(chatId);
            case MAIN_MENU_COMMAND_TEXT, MENU_COMMAND -> showMainMenu(chatId);
            case SHOW_PLAYLISTS_COMMAND -> showPlaylistsInlineButtons(chatId);
            default -> unknownCommand(chatId);
        }
    }

    private void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case MAIN_MENU_COMMAND_TEXT -> showMainMenu(chatId);
            case WISHLIST -> showWishList(chatId, messageId);
            case WATCHED_LIST -> showWatchedList(chatId, messageId);
            case TRACKED_LIST -> showTrackedList(chatId, messageId);
            case PLAYLISTS_COMMAND_TEXT -> showPlaylists(chatId, messageId);
            case ADD_WATCHEDLIST_COMMAND_TEXT -> requestUserInput(chatId);

            default -> sendMessage(chatId, "пук-пук");
        }
    }

    private void requestUserInput(Long chatId) {
        String text = "Введите название фильма/сериала";
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        userStates.replace(chatId, BotState.IDLE, BotState.AWAITING_INPUT);

        if (executeMessage(message)) {
            log.info("Сообщение [{}] успешно отправлено {}", text, chatId);
        }
    }


    private void showWatchedList(Long chatId, Integer messageId) {
        sendAction(chatId, ActionType.TYPING);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText(getWatchedList(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        // TODO сделать, константы, поставить нормальные callbacks
        rowsInline.add(Collections.singletonList(getButton(ADD_WATCHEDLIST_COMMAND_TEXT, ADD_WATCHEDLIST_COMMAND_TEXT)));
        rowsInline.add(Collections.singletonList(getButton("Удалить фильм", "REMOVE_FROM_WATCHEDLIST")));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editedMessage);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
    }

    private void showTrackedList(Long chatId, Integer messageId) {
        sendAction(chatId, ActionType.TYPING);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText(getTrackedList(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        // TODO сделать, константы, поставить нормальные callbacks
        rowsInline.add(Collections.singletonList(getButton("Добавить фильм/сериал", "TRANSFER_FROM_WISHLIST_TO_WATCHEDLIST")));
        rowsInline.add(Collections.singletonList(getButton("Удалить фильм", "REMOVE_FROM_WATCHEDLIST")));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editedMessage);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
    }

    private void showWishList(Long chatId, Integer messageId) {
        sendAction(chatId, ActionType.TYPING);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText(getWishList(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        // TODO сделать, константы, поставить нормальные callbacks
        rowsInline.add(Collections.singletonList(getButton("Добавить фильм/сериал", "TRANSFER_FROM_WISHLIST_TO_WATCHEDLIST")));
        rowsInline.add(Collections.singletonList(getButton("Перенести фильм/сериал", "TRANSFER_FROM_WISHLIST_TO_WATCHEDLIST")));
        rowsInline.add(Collections.singletonList(getButton("Удалить фильм/сериал из плейлиста", "REMOVE_FROM_WISHLIST")));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editedMessage);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
    }

    private String getWishList(Long ChatId) {
        // TODO нужно брать из БД список фильмов

        return """
                1. Матрица 4
                2. Побег из Шоушенко
                3. Рик и Морти (2013-н.в.)
                """;
    }

    private String getWatchedList(Long chatId) {
        // TODO нужно брать из БД список фильмов

        return """
                1. Шматрица
                2. Зеленый слоник
                3. С приветом по планетам
                """;
    }
    private String getTrackedList(Long chatId) {
        // TODO нужно брать из БД список фильмов

        return """
                1. Слово пацана
                """;
    }

    private void showPlaylists(Long chatId, Integer messageId) {
        sendAction(chatId, ActionType.TYPING);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText("Мои плейлисты: ");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(Collections.singletonList(getButton("Список желаемого", WISHLIST)));
        rowsInline.add(Collections.singletonList(getButton("Список просмотренного", WATCHED_LIST)));
        rowsInline.add(Collections.singletonList(getButton("Список отслеживаемого", TRACKED_LIST)));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editedMessage);
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
    }

    private void showMainMenu(Long chatId) {
        sendAction(chatId, ActionType.TYPING);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Главное меню:");   // TODO Сделать оформления главного меню

        message.setReplyMarkup(createMainInlineButtons());

        executeMessage(message);

        userStates.put(chatId, BotState.IDLE);
    }

    private InlineKeyboardMarkup createMainInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(getButton(PLAYLISTS_COMMAND_TEXT, PLAYLISTS_COMMAND_TEXT)));
        rowsInline.add(Collections.singletonList(getButton(ADD_WATCHEDLIST_COMMAND_TEXT, ADD_WATCHEDLIST_COMMAND_TEXT)));
        rowsInline.add(Collections.singletonList(getButton(ADD_WISHLIST_COMMAND_TEXT, ADD_WISHLIST_COMMAND_TEXT)));
        rowsInline.add(Collections.singletonList(getButton(ADD_TRACKEDLIST_COMMAND_TEXT, ADD_TRACKEDLIST_COMMAND_TEXT)));
        rowsInline.add(Collections.singletonList(getButton(SEARCH_RANDOM_COMMAND_TEXT, SEARCH_RANDOM_COMMAND_TEXT)));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(MAIN_MENU_COMMAND_TEXT);
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }


    private void addBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START_COMMAND, START_COMMAND_DESCRIPTION));
        listOfCommands.add(new BotCommand(MENU_COMMAND, MAIN_MENU_COMMAND_TEXT));
        listOfCommands.add(new BotCommand(HELP_COMMAND, HELP_COMMAND_DESCRIPTION));
        listOfCommands.add(new BotCommand(SHOW_PLAYLISTS_COMMAND, SHOW_PLAYLISTS_COMMAND_DESCRIPTION));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private void sendAction(Long chatId, ActionType actionType) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(actionType);

        try {
            execute(sendChatAction);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки действия чата " + e.getMessage());
        }
    }

    private void showPlaylistsInlineButtons(Long chatId) {
        sendAction(chatId, ActionType.TYPING);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Выберите плейлист:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(getButton(WISHLIST, WISHLIST)));
        rowsInline.add(Collections.singletonList(getButton(WATCHED_LIST, WATCHED_LIST)));
        rowsInline.add(Collections.singletonList(getButton(TRACKED_LIST, TRACKED_LIST)));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private InlineKeyboardButton getButton(String nameButton, String callBackData){
        var button = new InlineKeyboardButton();

        button.setText(nameButton);
        button.setCallbackData(callBackData);

        return button;
    }

    private void startCommand(Long chatId, String userName) {
        String formattedText = String.format(START_MESSAGE, userName);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);

        message.setChatId(chatId);
        message.setText(formattedText);
        message.setReplyMarkup(createMainKeyboard());

        if (executeMessage(message)) {
            log.info("Сообщение [{}] успешно отправлено {}", formattedText, chatId);
        }


    }

    private void helpCommand(Long chatId) {
        sendMessage(chatId, HELP_MESSAGE);
    }

    private void unknownCommand(Long chatId) {
        sendMessage(chatId, UNKNOWN_MESSAGE);
    }

    private void sendURLPhoto(Long chatId, String imageURL) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());

        sendPhoto.setPhoto(new InputFile(imageURL));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Изображение [{}] успешно отправлено {}", imageURL, chatId);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);

        message.setChatId(chatId);
        message.setText(textToSend);

        if (executeMessage(message)) {
            log.info("Сообщение [{}] успешно отправлено {}", textToSend, chatId);
        }
    }

    private boolean executeMessage(BotApiMethodMessage message) {
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
            return false;
        }
    }
}
