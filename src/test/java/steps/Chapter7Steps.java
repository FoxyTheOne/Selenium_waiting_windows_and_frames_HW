package steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Chapter7Steps {
    private Chapter7Steps() {
        // Not called
    }

    public static void openLoginFormPage(WebDriver driver){
        driver.findElement(By.xpath("//a[@href = 'login-form.html']")).click();
    }
}
