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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Component
@Slf4j
public class KinoListBot extends TelegramLongPollingBot {
    private static final String WISHLIST = "Список желаемого";
    private static final String WATCHED_LIST = "Список просмотренного";
    private static final String TRACKED_LIST = "Список отслеживаемого";

    @Value("${bot.name}")
    private String botName;
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String SHOW_PLAYLISTS = "/show";
    private static final String MENU = "/menu";

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
        if (request.hasMessage() && request.getMessage().hasText()) {
            log.info("Working onUpdateReceived, request text[{}] from @{}",
                    request.getMessage().getText(), request.getMessage().getChat().getUserName());

            String message = request.getMessage().getText();
            Long chatId = request.getMessage().getChatId();
            switch (message) {
                case START -> {
                    String userName = request.getMessage().getChat().getFirstName();
                    startCommand(chatId, userName);
                }
                case HELP -> helpCommand(chatId);
                case MAIN_MENU, MENU -> showMainMenu(chatId);
                case SHOW_PLAYLISTS -> showMyPlaylistsInlineButtons(chatId);
                default -> unknownCommand(chatId);
            }
        } else if (request.hasCallbackQuery()) {
            log.info("get callback {}", request.getCallbackQuery().getData());
            onCallbackQueryReceived(request.getCallbackQuery());
        }
    }

    private void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case WISHLIST -> showPlaylist(chatId, "WISHLIST");
            case WATCHED_LIST -> showPlaylist(chatId, "WATCHED_LIST");
            case TRACKED_LIST -> showPlaylist(chatId, "TRACKED_LIST");
            case MAIN_MENU -> showMainMenu(chatId);
            default -> sendMessage(chatId, "пук-пук");
        }
    }

    private void showPlaylist(Long chatId, String playlistType) {
        // TODO добавить логику для отображения содержимого плейлиста

        String playlistContent = getPlaylistContent(playlistType);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(playlistContent);

        // TODO У списка желаемого должно быть два инлайна(перенести в просмотренные и удалить), а у остальных только одно(удалить)
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(Collections.singletonList(getButton("Добавить в плейлист", "ADD_TO_PLAYLIST")));
        rowsInline.add(Collections.singletonList(getButton("Удалить из плейлиста", "REMOVE_FROM_PLAYLIST")));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private String getPlaylistContent(String playlistType) {
        // TODO надо подключить контроллер
        return "Тут должно быть содержимое вашего плейлиста, но я этого ещё не сделал....";
    }

    private void showMainMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Главное меню:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(getButton("Список желаемого", "WISHLIST")));
        rowsInline.add(Collections.singletonList(getButton("Список просмотренного", "WATCHED_LIST")));
        rowsInline.add(Collections.singletonList(getButton("Список отслеживаемого", "TRACKED_LIST")));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(MAIN_MENU);
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }


    private void addBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START, START_COMMAND_TEXT));
        listOfCommands.add(new BotCommand(MENU, MAIN_MENU));
        listOfCommands.add(new BotCommand(HELP, HELP_COMMAND_TEXT));
        listOfCommands.add(new BotCommand(SHOW_PLAYLISTS, SHOW_PLAYLISTS_COMMAND_TEXT));
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

    private void showMyPlaylistsInlineButtons(Long chatId) {
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
        log.info("Фотография [{}] успешно отправлено {}", imageURL, chatId);
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
