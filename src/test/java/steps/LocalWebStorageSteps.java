package steps;

import org.openqa.selenium.JavascriptExecutor;

import java.util.*;

public class LocalWebStorageSteps implements IWebStorageSteps{
    private final JavascriptExecutor js;

    public LocalWebStorageSteps(JavascriptExecutor js) {
        this.js = js;
    }

    @Override
    public String getItem(String key) {
        return (String) this.js.executeScript("return localStorage.getItem(arguments[0]);", key);
    }

    @Override
    public Set<String> keySet() {
//        return new HashSet<>(Arrays.asList((String[]) Objects.requireNonNull(this.js.executeScript("return Object.keys(localStorage);"))));
        // Получаем список ключей в виде массива из JavaScript
        List<String> keys = Objects.requireNonNull((List<String>) this.js.executeScript("return Object.keys(localStorage);"));
        // Конвертируем List в Set
        return new HashSet<>(keys);
    }

    @Override
    public void setItem(String key, String value) {
        this.js.executeScript("localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    @Override
    public String removeItem(String key) {
        String value = this.getItem(key);
        this.js.executeScript("localStorage.removeItem(arguments[0]);", key);
        return value;
    }

    @Override
    public void clear() {
        this.js.executeScript("localStorage.clear();");
    }

    @Override
    public int size() {
        return ((Number) Objects.requireNonNull(this.js.executeScript("return localStorage.length;"))).intValue();
    }
}
