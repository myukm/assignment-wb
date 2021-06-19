package assignment;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WalletHubReviewTest {
    WebDriver driver = null;
    private String starSvgCN = "rvs-star-svg";
    private String reviewItemsXpath = "//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/div/ng-dropdown/" +
                                      "div/ul/li";

    @FindBy(xpath = "//*[@id=\"scroller\"]/main/div[1]/nav/div[2]/a[2]")
    WebElement reviewSection;

    @FindBy(xpath = "/html/body/web-app/div/div[1]/main/div[2]/div/div[3]/section/div[1]/div[3]/review-star/div")
    WebElement reviewStarsDiv;

    @FindBy(xpath = "//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/div/ng-dropdown/div")
    WebElement reviewDrpDown;

    @FindBy(xpath = "//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/div/div[1]/textarea")
    WebElement reviewText;

    @FindBy(xpath = "//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/sub-navigation/div/div[2]")
    WebElement reviewSubmit;

    @FindBy(xpath = "//*[@id=\"join-light\"]/form/div/nav/ul/li[2]/a")
    WebElement loginTab;

    @FindBy(id = "em-ipt")
    WebElement loginEmail;

    @FindBy(id = "pw1-ipt")
    WebElement password;

    @FindBy(xpath = "//*[@id=\"join-light\"]/form/div/div[3]/button")
    WebElement loginButton;

    @FindBy(xpath = "/html/body/web-app/div/header/div/nav[1]/div[5]/span")
    WebElement userAccount;

    @FindBy(xpath = "/html/body/web-app/div/header/div/nav[1]/div[5]/div/span")
    WebElement logOut;

    @BeforeTest
    public void browserSetUp()
    {
        Utility.setWebDriverSystemProperty();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }


    @Test
    public void walletHubReviewTest() throws InterruptedException {
        PageFactory.initElements(driver, this);
        driver.get(Constants.WALLETHUB_REVIEWS_URL);
        Actions actions = new Actions(driver);
        Thread.sleep(5000);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // Click on ReviewsSection
        reviewSection.click();

        // Select Review Stars Div and iterate over the star items till required review stars and click
        List<WebElement> stars = reviewStarsDiv.findElements(By.className(starSvgCN));
        Boolean check = false;
        for(int i=0; i<Constants.REVIEW_STARS; i++){
            check = stars.get(i).isDisplayed();
            if(!check) {
                break;
            }
            if(i == Constants.REVIEW_STARS - 1) {
                actions.moveToElement(stars.get(i)).click().perform();
            }
            else {
                actions.moveToElement(stars.get(i)).perform();
            }
        }
        Assert.assertTrue(check, "Review stars not Highlighted");
        Thread.sleep(2000);

        // Select type of review and write the review
        reviewDrpDown.click();
        List<WebElement> reviewDrpList = driver.findElements(By.xpath(reviewItemsXpath));
        Boolean isItemSelected = false;
        for(WebElement d: reviewDrpList){
            if(d.getText().equals(Constants.REVIEW_DRP_DOWN_ITEM)){
                d.click();
                if(!d.isDisplayed())
                    isItemSelected = true;
                break;
            }
        }
        Assert.assertTrue(isItemSelected, Constants.REVIEW_DRP_DOWN_ITEM + " not Selected");
        reviewText.sendKeys(Utility.getReviewMessage());
        reviewSubmit.click();
        Thread.sleep(10000);

        // enter login details to submit the review
        loginTab.click();
        String[] loginCreds = Utility.getUserCreds("walletHubLoginCreds");
        loginEmail.sendKeys(loginCreds[0]);
        password.sendKeys(loginCreds[1]);
        loginButton.submit();
        Thread.sleep(10000);
        // check to see if review is created
        Assert.assertTrue(driver.getPageSource().contains(Utility.getReviewMessage()));
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(0,-350)");
        Thread.sleep(2000);
        actions.moveToElement(userAccount).perform();
        Thread.sleep(2000);
        logOut.click();
    }

    @AfterTest
    public void closeAll(){
        driver.close();
    }

}
