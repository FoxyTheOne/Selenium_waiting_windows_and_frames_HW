package steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class Chapter4Steps {
    private Chapter4Steps() {
        // Not called
    }

//    @Step ("Open infinite scroll page")
    public static void openInfiniteScrollPage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='infinite-scroll']")).click();
    }

    public static void openShadowDomPage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='shadow-dom']")).click();
    }

    public static void openCookiesPage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='cookies']")).click();
    }

    public static void openIFramePage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='iframes']")).click();
    }

    public static void openDialogBoxesPage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='dialog-boxes']")).click();
    }

    public static void openWebStoragePage(WebDriver driver){
        driver.findElement(By.cssSelector("[href^='web-storage']")).click();
    }
}
