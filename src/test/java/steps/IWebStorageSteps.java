package steps;

import java.util.Set;

public interface IWebStorageSteps {
    String getItem(String key);

    Set<String> keySet();

    void setItem(String key, String value);

    String removeItem(String key);

    void clear();

    int size();
}
