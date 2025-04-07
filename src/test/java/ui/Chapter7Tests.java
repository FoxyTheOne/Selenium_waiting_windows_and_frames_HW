package ui;

import config.TestPropertiesConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.Chapter7Steps.openLoginFormPage;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Chapter7Tests {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(Chapter7Tests.class);
    TestPropertiesConfig config = ConfigFactory.create(TestPropertiesConfig.class, System.getProperties());
    private final String baseUrl = config.getBaseUrl();
    private WebDriver driver;

    @BeforeAll
    void setUpAll() {
        if (!config.isRemote()) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        driver = new ChromeDriver();
        driver.manage().window().maximize();
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
    void signInTest() {
        openLoginFormPage(driver);

        driver.findElement(By.id("username")).sendKeys(config.getUsername());
        driver.findElement(By.id("password")).sendKeys(config.getPassword());
        driver.findElement(By.xpath("//button[@type = 'submit']")).click();
        WebElement message = driver.findElement(By.className("alert"));

        assertEquals("Login successful", message.getText());
    }
}
