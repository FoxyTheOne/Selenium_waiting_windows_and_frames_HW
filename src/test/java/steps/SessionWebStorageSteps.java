package steps;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SessionWebStorageSteps implements IWebStorageSteps{
    private final JavascriptExecutor js;

    public SessionWebStorageSteps(JavascriptExecutor js) {
        this.js = js;
    }

    @Override
    public String getItem(String key) {
        return (String) this.js.executeScript("return sessionStorage.getItem(arguments[0]);", key);
    }

    @Override
    public Set<String> keySet() {
//        return new HashSet<>(Arrays.asList((String[]) Objects.requireNonNull(this.js.executeScript("return Object.keys(sessionStorage);"))));
        // Получаем список ключей в виде массива из JavaScript
        List<String> keys = Objects.requireNonNull((List<String>) this.js.executeScript("return Object.keys(sessionStorage);"));
        // Конвертируем List в Set
        return new HashSet<>(keys);
    }

    @Override
    public void setItem(String key, String value) {
        this.js.executeScript("sessionStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    @Override
    public String removeItem(String key) {
        String value = this.getItem(key);
        this.js.executeScript("sessionStorage.removeItem(arguments[0]);", key);
        return value;
    }

    @Override
    public void clear() {
        this.js.executeScript("sessionStorage.clear();");
    }

    @Override
    public int size() {
        return ((Number) Objects.requireNonNull(this.js.executeScript("return sessionStorage.length;"))).intValue();
    }
}
