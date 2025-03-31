import java.awt.*;

/**
 * Класс для хранения констант, необходимых для тестирования и используемых в разных классах
 * (final класс - для предотвращения наследования и модификации класса).
 */
public class Constants {
    // Приватный конструктор предотвращает создание экземпляров этого класса
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    public static final String TEST_TEXT = "Test";
    public static final String EMPTY_TEXT = "";
    public static final String VALUE = "value";
}
