package ui;

import config.TestPropertiesConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import steps.IWebStorageSteps;
import steps.LocalWebStorageSteps;
import steps.SessionWebStorageSteps;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static steps.Chapter4Steps.*;
import static ui.Constants.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Chapter4Tests {
    //    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/";
    private static final Logger LOGGER = LoggerFactory.getLogger(Chapter4Tests.class);
    TestPropertiesConfig config = ConfigFactory.create(TestPropertiesConfig.class, System.getProperties());
    private final String baseUrl = config.getBaseUrl();

    private WebDriver driver;
    //    private Actions actions;
    private JavascriptExecutor js;
    private WebDriverWait wait5sec;
    private IWebStorageSteps localWebStorageSteps;
    private IWebStorageSteps sessionWebStorageSteps;

    @BeforeAll
    void setUpAll() {
//        if (System.getProperty("local").equals("true")) {
//            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
//        }
        if(!config.isRemote()){
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        driver = new ChromeDriver();
        driver.manage().window().maximize();
//        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        wait5sec = new WebDriverWait(driver, Duration.ofSeconds(5));
        localWebStorageSteps = new LocalWebStorageSteps(js);
        sessionWebStorageSteps = new SessionWebStorageSteps(js);
    }

    @AfterAll
    void tearDown() {
        driver.getPageSource();
        driver.quit();
    }

    @BeforeEach
    void setup() {
        driver.get(baseUrl);
    }

    @Test
    void openWebFormPageTest() {
        String actualMainTitle = driver.getTitle();

        assertEquals(baseUrl, driver.getCurrentUrl());
        assertEquals("Hands-On Selenium WebDriver with Java", actualMainTitle);
    }

    @Test
    void infiniteScrollTest() {
        openInfiniteScrollPage(driver);
        String firstParagraphText = "Lorem ipsum dolor sit amet consectetur adipiscing elit habitant metus, tincidunt maecenas posuere sollicitudin augue duis bibendum mauris eu, et dignissim magna ad nascetur suspendisse quis nunc. Fames est ligula molestie aliquam pretium bibendum nullam, sociosqu maecenas mus etiam consequat ornare leo, sem mattis varius luctus litora senectus. Parturient quis tristique erat natoque tortor nascetur, primis augue vivamus habitasse senectus porta leo, aenean potenti ante a nam.";
        WebElement firstParagraph = wait5sec.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='content']/descendant::p")));
        WebElement divWithParagraphs = driver.findElement(By.xpath("//div[@id='content']"));
        List<WebElement> availableParagraphs = divWithParagraphs.findElements(By.className("lead"));

        assertFalse(availableParagraphs.isEmpty());
        LOGGER.info("availableParagraphs count: {}", availableParagraphs.size());

        List<WebElement> availableAfterScrollParagraphs = scrollAndCheckParagraphsAmount(divWithParagraphs, availableParagraphs);
        List<WebElement> availableAfterScrollParagraphs2 = scrollAndCheckParagraphsAmount(divWithParagraphs, availableAfterScrollParagraphs);
        List<WebElement> availableAfterScrollParagraphs3 = scrollAndCheckParagraphsAmount(divWithParagraphs, availableAfterScrollParagraphs2);

        assertAll(
                () -> assertEquals(firstParagraphText, firstParagraph.getText()),
                () -> assertTrue(availableAfterScrollParagraphs.size() > availableParagraphs.size(),
                        "Количество абзацев не увеличилось после прокрутки"),
                () -> assertTrue(availableAfterScrollParagraphs2.size() > availableAfterScrollParagraphs.size(),
                        "Количество абзацев не увеличилось после второй прокрутки"),
                () -> assertTrue(availableAfterScrollParagraphs3.size() > availableAfterScrollParagraphs2.size(),
                        "Количество абзацев не увеличилось после третьей прокрутки")

        );
    }

    @Test
    void shadowDomTest() {
        openShadowDomPage(driver);
        WebElement shadowElement = driver.findElement(By.id("content"));
        SearchContext shadowRoot = shadowElement.getShadowRoot();
        WebElement shadowTextElement = shadowRoot.findElement(By.cssSelector("p"));

        assertEquals("Hello Shadow DOM", shadowTextElement.getText());
    }

    @Test
    void cookiesTest() {
        openCookiesPage(driver);
        Set<Cookie> cookies = driver.manage().getCookies();

        assertFalse(cookies.isEmpty());

        int cookiesInitialSize = cookies.size();
        LOGGER.info(String.valueOf(cookies));
        Cookie username = driver.manage().getCookieNamed("username");

        assertNotNull(username);
        assertEquals("John Doe", username.getValue());

        driver.findElement(By.id("refresh-cookies")).click();
        driver.manage().addCookie(new Cookie(NEW_KEY, NEW_VALUE));
        Cookie newKey = driver.manage().getCookieNamed(NEW_KEY);
        LOGGER.info(String.valueOf(driver.manage().getCookies()));

        assertNotNull(newKey);
        assertEquals(NEW_VALUE, newKey.getValue());

        int cookiesNewSize = driver.manage().getCookies().size();

        assertEquals(cookiesInitialSize + 1, cookiesNewSize);
    }

    @Test
    void iFrameTest() {
        openIFramePage(driver);
        String expectedFirstParagraphText = "Lorem ipsum dolor sit amet consectetur adipiscing elit habitant metus, tincidunt maecenas posuere sollicitudin augue duis bibendum mauris eu, et dignissim magna ad nascetur suspendisse quis nunc. Fames est ligula molestie aliquam pretium bibendum nullam, sociosqu maecenas mus etiam consequat ornare leo, sem mattis varius luctus litora senectus. Parturient quis tristique erat natoque tortor nascetur, primis augue vivamus habitasse senectus porta leo, aenean potenti ante a nam.";
//        String html = driver.getPageSource();
        WebElement iFrameElement = wait5sec.until(ExpectedConditions.presenceOfElementLocated(By.id("my-iframe")));

        driver.switchTo().frame(iFrameElement);
        WebElement firstParagraph = driver.findElement(By.xpath("//div[@id='content']/descendant::p"));

        assertEquals(expectedFirstParagraphText, firstParagraph.getText());
    }

    @Test
    void dialogBoxesTest() {
        openDialogBoxesPage(driver);

        // Launch alert
        driver.findElement(By.id("my-alert")).click();
        wait5sec.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String actualAlertText = alert.getText();
        alert.accept();

        // Launch confirm
        driver.findElement(By.id("my-confirm")).click();
        wait5sec.until(ExpectedConditions.alertIsPresent());
        Alert alertConfirm = driver.switchTo().alert();
        String actualAlertConfirmText = alertConfirm.getText();
        alert.accept();

        driver.findElement(By.id("my-confirm")).click();
        wait5sec.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert();
        alert.dismiss();
        WebElement actualAlertConfirmPageText = driver.findElement(By.id("confirm-text"));

        // Launch prompt
        driver.findElement(By.id("my-prompt")).click();
        wait5sec.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().sendKeys("Test");
        driver.switchTo().alert().accept();
        WebElement actualPromptPageText = driver.findElement(By.id("prompt-text"));

        // Launch modal
        driver.findElement(By.id("my-modal")).click();
        WebElement saveButton = driver.findElement(By.xpath("//button[normalize-space() = 'Save changes']"));
        wait5sec.until(ExpectedConditions.elementToBeClickable(saveButton));
        saveButton.click();
        WebElement actualModalPageText = driver.findElement(By.id("modal-text"));

        assertAll(
                () -> assertEquals("Hello world!", actualAlertText),
                () -> assertEquals("Is this correct?", actualAlertConfirmText),
                () -> assertEquals("You chose: false", actualAlertConfirmPageText.getText()),
                () -> assertEquals("You typed: Test", actualPromptPageText.getText()),
                () -> assertEquals("You chose: Save changes", actualModalPageText.getText())
        );
    }

    @Test
    void localWebStorageTest() {
        openWebStoragePage(driver);
//        WebStorage webStorage = (WebStorage) driver; <-- deprecated
//        LocalStorage localStorage = webStorage.getLocalStorage(); <-- deprecated
        WebElement localStorageTextElement = driver.findElement(By.id("local-storage"));
        WebElement localStorageButtonElement = driver.findElement(By.id("display-local"));

        int localStorageInitSize = localWebStorageSteps.size();
//        localStorage.keySet()
//                .forEach(key ->
//                        System.out.printf("Local storage: {%s}={%s}\n", key, localStorage.getItem(key)));
        localWebStorageSteps.keySet()
                .forEach(key ->
                        LOGGER.info("Local storage: {{}}={{}}\n", key, localWebStorageSteps.getItem(key)));
        localWebStorageSteps.setItem("some key", "some value");
        int localStorageNewSize = localWebStorageSteps.size();

        assertEquals(localStorageInitSize + 1, localStorageNewSize);

//        localStorage.clear(); <-- deprecated
        localWebStorageSteps.clear();
        localStorageButtonElement.click();
        LOGGER.info("Local storage after clear(): {}", localStorageTextElement.getText());

        assertEquals("{}", localStorageTextElement.getText());

        localWebStorageSteps.setItem(NEW_KEY, NEW_VALUE);
        localStorageButtonElement.click();
        LOGGER.info("Local storage with new value: {}", localStorageTextElement.getText());

//        assertEquals("{\"%s\":\"%s\"}".formatted(NEW_KEY, NEW_VALUE), localStorageTextElement.getText());
        assertEquals(String.format("{\"%s\":\"%s\"}", NEW_KEY, NEW_VALUE), localStorageTextElement.getText());
    }

    @Test
    void sessionWebStorageTest() {
        openWebStoragePage(driver);
        WebElement sessionStorageTextElement = driver.findElement(By.id("session-storage"));
        WebElement sessionStorageButtonElement = driver.findElement(By.id("display-session"));

        int sessionStorageInitSize = sessionWebStorageSteps.size();
        sessionWebStorageSteps.keySet()
                .forEach(key ->
                        LOGGER.info("Session storage: {{}}={{}}\n", key, sessionWebStorageSteps.getItem(key)));
        sessionWebStorageSteps.setItem("some key", "some value");
        int sessionStorageNewSize = sessionWebStorageSteps.size();

        assertEquals(sessionStorageInitSize + 1, sessionStorageNewSize);

        sessionWebStorageSteps.clear();
        sessionStorageButtonElement.click();
        LOGGER.info("Session storage after clear(): {}", sessionStorageTextElement.getText());

        assertEquals("{}", sessionStorageTextElement.getText());

        sessionWebStorageSteps.setItem(NEW_KEY, NEW_VALUE);
        sessionStorageButtonElement.click();
        LOGGER.info("Session storage with new value: {}", sessionStorageTextElement.getText());

//        assertEquals("{\"%s\":\"%s\"}".formatted(NEW_KEY, NEW_VALUE), sessionStorageTextElement.getText());
        assertEquals(String.format("{\"%s\":\"%s\"}", NEW_KEY, NEW_VALUE), sessionStorageTextElement.getText());
    }

    List<WebElement> scrollAndCheckParagraphsAmount(WebElement divWithParagraphs, List<WebElement> availableParagraphs) {
        WebElement lastParagraph = availableParagraphs.get(availableParagraphs.size() - 1);
        wait5sec.until(driver -> lastParagraph.isDisplayed());
//        actions.scrollToElement(lastParagraph).perform(); // <-- исключение "move target out of bounds (Session info: chrome=109.0.5414.120)" . Нужно опуститься до самого низа страницы, а он опускается не до конца. Нужно добавить пиксели (узнать размеры параграфа и опуститься до самого низа) или привязаться к чему-то другому
        js.executeScript("arguments[0].scrollIntoView(true);", lastParagraph);

        wait5sec.until(driver -> divWithParagraphs.findElements(By.className("lead")).size() > availableParagraphs.size());

        List<WebElement> availableAfterScrollParagraphs = divWithParagraphs.findElements(By.className("lead"));
        assertFalse(availableAfterScrollParagraphs.isEmpty());
        LOGGER.info("availableAfterScrollParagraphs count: {}", availableAfterScrollParagraphs.size());

        return availableAfterScrollParagraphs;
    }
}
