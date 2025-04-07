package ui;

/**
 * Класс для хранения констант, необходимых для тестирования и используемых в разных классах
 * (final класс - для предотвращения наследования и модификации класса).
 */
public class Constants {
    // Приватный конструктор предотвращает создание экземпляров этого класса
    private Constants() {
        throw new AssertionError("Cannot instantiate ui.Constants class");
    }

    public static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/";
    public static final String NEW_KEY = "new key";
    public static final String NEW_VALUE = "new value";
}
