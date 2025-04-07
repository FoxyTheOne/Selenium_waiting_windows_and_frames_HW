package ui;

import config.TestPropertiesConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoadingImagesTests {
    private static final String BASE_IMAGE_URL = "https://bonigarcia.dev/selenium-webdriver-java/loading-images.html";
    TestPropertiesConfig config = ConfigFactory.create(TestPropertiesConfig.class, System.getProperties());
    private WebDriver driver;
    private WebDriverWait wait10sec;

    @BeforeAll
    void setUpAll() {
        if (!config.isRemote()) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait10sec = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void tearDown() {
        driver.getPageSource();
        driver.quit();
    }

    @BeforeEach
    void setup() {
        driver.get(BASE_IMAGE_URL);
    }

    @Test
    void openWebFormPageTest() {
        String actualMainTitle = driver.getTitle();
        WebElement actualPageTitle = driver.findElement(By.className("display-6"));

        assertAll(
                () -> assertEquals("Hands-On Selenium WebDriver with Java", actualMainTitle),
                () -> assertEquals("Loading images", actualPageTitle.getText())
        );
    }

    @Test
    void loadingLastImageTest() {
        WebElement imageLandscape = wait10sec.until(ExpectedConditions.presenceOfElementLocated(By.id("landscape")));
        WebElement imageCompass = driver.findElement(By.id("compass"));
        WebElement imageCalendar = driver.findElement(By.id("calendar"));
        WebElement imageAward = driver.findElement(By.id("award"));

        assertAll(
                () -> assertTrue(imageLandscape.getDomAttribute("src").toLowerCase().contains("landscape")),
                () -> assertTrue(imageCompass.getDomAttribute("src").toLowerCase().contains("compass")),
                () -> assertTrue(imageCalendar.getDomAttribute("src").toLowerCase().contains("calendar")),
                () -> assertTrue(imageAward.getDomAttribute("src").toLowerCase().contains("award"))
        );
    }

    @Test
    void loadingAllImagesTest() {
        wait10sec.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@id = 'image-container']/descendant::img"), 4));
        WebElement doneTextElement = driver.findElement(By.id("text"));

        assertEquals("Done!", doneTextElement.getText());
    }
}
