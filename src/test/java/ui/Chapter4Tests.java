package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Chapter4Tests {
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/";
    private WebDriver driver;
    private Actions actions;
    private JavascriptExecutor js;
    private WebDriverWait wait5sec;

    @BeforeAll
    void setUpAll() {
        if (System.getProperty("local").equals("true")) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        wait5sec = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterAll
    void tearDown() {
        driver.getPageSource();
        driver.quit();
    }

    @BeforeEach
    void setup() {
        driver.get(BASE_URL);
    }

    @Test
    void openWebFormPageTest() {
        String actualMainTitle = driver.getTitle();
        assertEquals("Hands-On Selenium WebDriver with Java", actualMainTitle);
    }

    @Test
    void infiniteScrollTest() {
        driver.findElement(By.cssSelector("[href^='infinite-scroll']")).click();

        String firstParagraphText = "Lorem ipsum dolor sit amet consectetur adipiscing elit habitant metus, tincidunt maecenas posuere sollicitudin augue duis bibendum mauris eu, et dignissim magna ad nascetur suspendisse quis nunc. Fames est ligula molestie aliquam pretium bibendum nullam, sociosqu maecenas mus etiam consequat ornare leo, sem mattis varius luctus litora senectus. Parturient quis tristique erat natoque tortor nascetur, primis augue vivamus habitasse senectus porta leo, aenean potenti ante a nam.";
//        WebElement divWithParagraphs = driver.findElement(By.id("content")); // <-- 3 раза из 4х падает с ошибкой
//        WebElement firstParagraph = divWithParagraphs.findElement(By.className("lead")); // <-- 3 раза из 4х падает с ошибкой
        WebElement firstParagraph = wait5sec.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='content']/descendant::p")));

        WebElement divWithParagraphs = driver.findElement(By.xpath("//div[@id='content']"));
        List<WebElement> availableParagraphs = divWithParagraphs.findElements(By.className("lead"));
        assertFalse(availableParagraphs.isEmpty());
        System.out.println("availableParagraphs count: " + availableParagraphs.size());

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
        driver.findElement(By.cssSelector("[href^='shadow-dom']")).click();

        WebElement shadowElement = driver.findElement(By.id("content"));
        SearchContext shadowRoot = shadowElement.getShadowRoot();
        WebElement shadowTextElement = shadowRoot.findElement(By.cssSelector("p"));

        assertEquals("Hello Shadow DOM", shadowTextElement.getText());
    }

    @Test
    void cookiesTest() {
        driver.findElement(By.cssSelector("[href^='cookies']")).click();

        Set<Cookie> cookies = driver.manage().getCookies();
        assertFalse(cookies.isEmpty());
        int cookiesInitialSize = cookies.size();
        System.out.println(cookies);

        Cookie username = driver.manage().getCookieNamed("username");
        assertNotNull(username);
        assertEquals("John Doe", username.getValue());

        driver.findElement(By.id("refresh-cookies")).click();

        driver.manage().addCookie(new Cookie("new key", "test value"));
        Cookie newKey = driver.manage().getCookieNamed("new key");
        System.out.println(driver.manage().getCookies());
        assertNotNull(newKey);
        assertEquals("test value", newKey.getValue());

        int cookiesNewSize = driver.manage().getCookies().size();
        assertEquals(cookiesInitialSize + 1, cookiesNewSize);
    }

    @Test
    void iFrameTest() {
        driver.findElement(By.cssSelector("[href^='iframes']")).click();

        String expectedFirstParagraphText = "Lorem ipsum dolor sit amet consectetur adipiscing elit habitant metus, tincidunt maecenas posuere sollicitudin augue duis bibendum mauris eu, et dignissim magna ad nascetur suspendisse quis nunc. Fames est ligula molestie aliquam pretium bibendum nullam, sociosqu maecenas mus etiam consequat ornare leo, sem mattis varius luctus litora senectus. Parturient quis tristique erat natoque tortor nascetur, primis augue vivamus habitasse senectus porta leo, aenean potenti ante a nam.";

        WebElement iFrameElement = driver.findElement(By.id("my-iframe"));
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className("lead")));

        driver.switchTo().frame(iFrameElement);
        WebElement firstParagraph = driver.findElement(By.xpath("//div[@id='content']/descendant::p"));
        assertEquals(expectedFirstParagraphText, firstParagraph.getText());
    }

    @Test
    void dialogBoxesTest(){
        driver.findElement(By.cssSelector("[href^='dialog-boxes']")).click();

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

    List<WebElement> scrollAndCheckParagraphsAmount(WebElement divWithParagraphs, List<WebElement> availableParagraphs) {
        WebElement lastParagraph = availableParagraphs.getLast();
        wait5sec.until(driver -> lastParagraph.isDisplayed());
//        actions.moveToElement(lastParagraph).perform(); // <-- исключение "move target out of bounds (Session info: chrome=109.0.5414.120)"
//        actions.scrollToElement(lastParagraph).perform(); // <-- исключение "move target out of bounds (Session info: chrome=109.0.5414.120)"
        js.executeScript("arguments[0].scrollIntoView(true);", lastParagraph);

        wait5sec.until(driver -> divWithParagraphs.findElements(By.className("lead")).size() > availableParagraphs.size());

        List<WebElement> availableAfterScrollParagraphs = divWithParagraphs.findElements(By.className("lead"));
        assertFalse(availableAfterScrollParagraphs.isEmpty());
        System.out.println("availableAfterScrollParagraphs count: " + availableAfterScrollParagraphs.size());

        return availableAfterScrollParagraphs;
    }
}
