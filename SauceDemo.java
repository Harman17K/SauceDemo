package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SauceDemo {
    WebDriver driver;
    String BASE_URL="https://www.saucedemo.com/";

    @BeforeEach
    void Setup(){
        System.setProperty("webdriver.chrome.driver","C:\\Users\\harmsingh\\Desktop\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get(BASE_URL);
    }

    @AfterEach
    void cleanup(){
        driver.quit();
    }

    void SignIn(){
        WebElement username = driver.findElement(By.id("user-name"));
        waitForElement(username);
        username.sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        WebElement itemList = driver.findElement(By.className("inventory_list"));
        waitForElement(itemList);
    }

    void waitForElement(WebElement element){
        new WebDriverWait(driver,Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
    }

    void waitUntilElementDisapper(String element){
        new WebDriverWait(driver,Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(By.className(element)));
    }

    void add_Sauce_Backpack(){
        WebElement addBackupToCart = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addBackupToCart.click();
        WebElement remove = driver.findElement(By.id("remove-sauce-labs-backpack"));
        waitForElement(remove);
    }

    @Test
    void test_Valid_SignIn_Flow(){
        SignIn();
    }

    @Test
    void test_Invalid_SignIn_Flow(){
        String errorText= "Username and password do not match";
        WebElement username = driver.findElement(By.id("user-name"));
        waitForElement(username);
        username.sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("invalid_password");
        driver.findElement(By.id("login-button")).click();
        waitForElement(driver.findElement(By.xpath("//*[contains(text(),'" + errorText + "')]")));
    }

    @Test
    void test_Add_To_Cart_Check(){
        SignIn();
        add_Sauce_Backpack();
        waitForElement(driver.findElement(By.className("shopping_cart_badge")));
    }

    @Test
    void test_Remove_Button_Functionality(){
        SignIn();
        add_Sauce_Backpack();
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        waitUntilElementDisapper("shopping_cart_badge");
    }

    @Test
    void validateItemCheckout() {
        SignIn();
        add_Sauce_Backpack();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement cart = driver.findElement(By.className("shopping_cart_link"));
        js.executeScript("arguments[0].scrollIntoView()", cart);
        WebElement cartNotify = driver.findElement(By.className("shopping_cart_badge"));
        if(cartNotify.isDisplayed()){
            cartNotify.click();
        }
        else{
            throw new NoSuchElementException("Cart notifier not present");
        }
        driver.findElement(By.id("checkout")).isDisplayed();
        driver.findElement(By.id("checkout")).click();
        driver.findElement(By.id("first-name")).sendKeys("Harman");
        driver.findElement(By.id("last-name")).sendKeys("Harman");
        driver.findElement(By.id("postal-code")).sendKeys("Harman");
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("finish")).click();
        driver.findElement(By.xpath("//*[normalize-space()=\"Thank you for your order!\"]")).isDisplayed();
    }
}
