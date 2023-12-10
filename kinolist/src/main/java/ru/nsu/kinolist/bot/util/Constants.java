package ru.nsu.kinolist.bot.util;

public class Constants {
    public static final String START_COMMAND_TEXT = "Начало работы";
    public static final String HELP_COMMAND_TEXT = "Помощь";
    public static final String SHOW_PLAYLISTS_COMMAND_TEXT = "Показать мои плейлисты";
    public static final String MAIN_MENU = "Главное меню";
    public static final String START_MESSAGE = """
                **Добро пожаловать в бот, %s!**
                
                Здесь Вы сможете добавить фильм в wishlist.
                Поставить напоминание о выходе новой серии
                и не только...
                """;
    public static final String HELP_MESSAGE = """
                **Справочная информация по боту**
                
                **Для добавления фильма в список желаемого воспользуйтесь:**
                TODO добавить команды
                """;
    public static final String UNKNOWN_MESSAGE = """
                Не удалось распознать команду!
                Введите команда /help
                """;
}
