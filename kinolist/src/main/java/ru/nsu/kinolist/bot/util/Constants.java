package ru.nsu.kinolist.bot.util;

public class Constants {
    public static final String START_COMMAND = "/start";
    public static final String START_COMMAND_DESCRIPTION = "Перезапуск";

    public static final String HELP_COMMAND = "/help";
    public static final String HELP_COMMAND_DESCRIPTION = "Помощь";

    public static final String MENU_COMMAND = "/menu";
    public static final String MAIN_MENU_COMMAND_TEXT = "Главное меню";

    public static final String PLAYLISTS_COMMAND_TEXT = "Мои плейлисты";
    public static final String ADD_WATCHEDLIST_COMMAND_TEXT = "Добавить в список просмотренных";
    public static final String ADD_WISHLIST_COMMAND_TEXT = "Добавить в список желаемого";
    public static final String ADD_TRACKEDLIST_COMMAND_TEXT = "Добавить в список отслеживаемого";
    public static final String SEARCH_RANDOM_COMMAND_TEXT = "Найти случайный фильм";

    public static final String START_MESSAGE_NEW_USER = """
                **Добро пожаловать в бот, %s!**
                
                Здесь Вы сможете добавить фильм в список желания, просмотров.
                Поставить напоминание о выходе новой серии
                и не только...
                """;

    public static final String START_MESSAGE_OLD_USER = """
                **Я успешно перезапущен.
                /help
                """;
    public static final String HELP_MESSAGE = """
                **Справочная информация по боту**
                
                **Весь функционал находится в главном меню,
                просто напиши /menu или нажми на кнопку **Главное меню**
                """;
    public static final String UNKNOWN_MESSAGE = """
                Не удалось распознать команду!
                Введите команду /help
                """;
}
