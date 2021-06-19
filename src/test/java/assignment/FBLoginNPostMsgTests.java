package assignment;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class FBLoginNPostMsgTests {

    static Logger log = Logger.getLogger(FBLoginNPostMsgTests.class);
    @FindBy(id = "email")
    WebElement emailField;

    @FindBy(id = "pass")
    WebElement passwordField;

    @FindBy(name = "login")
    WebElement loginButton;

    @FindBy(xpath = "/html/body/div[1]/div/div[1]/div/div[2]/div[4]/div[1]/span/div/div[1]/img")
    WebElement account;

    @FindBy(xpath = "/html/body/div[1]/div/div[1]/div/div[2]/div[4]/div[2]/div/div[2]/div[1]/div[1]/div/div/div/div/" +
            "div/div/div/div/div[1]/div/div[3]/div/div[4]/div/div[1]/div[1]/div/i")
    WebElement logOut;

    @FindBy(xpath = "/html/body/div[1]/div/div[1]/div/div[2]/div[4]/div[1]/div[3]/span/div/i")
    WebElement createButton;

    @FindBy(xpath = "/html/body/div[1]/div/div[1]/div/div[2]/div[4]/div[2]/div/div[2]/div[1]/div[1]/div/div/div/div/" +
            "div/div/div/div[2]/div[1]/div/div[1]/div[1]/div/i")
    WebElement postButton;

    @FindBy(xpath = "//*[@id=\"facebook\"]/body/div[3]/div[1]/div/div[2]/div/div/div/form/div/div[1]/div/div/div/" +
            "div[2]/div[1]/div[1]/div[1]/div/div/div/div/div[2]/div/div/div/div")
    WebElement postTextField;

    @FindBy(xpath = "/html/body/div[1]/div/div[1]/div/div[2]/div[3]/div/div[1]/div[1]/ul/li[1]/span/div/a")
    WebElement homeButton;


    @DataProvider
    public Object[][] loginTestData() throws Exception {
        JSONObject inputObj = Utility.readFile(Constants.TEST_DATA_FILE_NAME);
        Set<String> fields = inputObj.keySet();
        int numberOfElements = inputObj.keySet().size();
        Object[][] result = new Object[numberOfElements][1];
        int count = 0;
        for (String key : fields) {
            if (key.equals("FBLoginData")) {
                JSONArray input = (JSONArray) inputObj.get(key);
                Iterator<JSONObject> elements = input.iterator();
                List<String[]> inputList = new ArrayList<String[]>();
                while (elements.hasNext()) {
                    JSONObject obj = elements.next();
                    inputList.add(new String[]{obj.get("user_email").toString(),
                            obj.get("user_phone").toString(), obj.get("password").toString()});
                }
                result[count][0] = inputList;
                count++;
                break;
            }
        }
        return result;
    }

    @DataProvider
    public Object[][] messagesTestData() throws Exception {
        JSONObject inputObj = Utility.readFile(Constants.TEST_DATA_FILE_NAME);
        Set<String> fields = inputObj.keySet();
        int numberOfElements = inputObj.keySet().size();
        Object[][] result = new Object[numberOfElements][1];
        int count = 0;
        for (String key : fields) {
            if (key.equals("postMessages")) {
                JSONArray input = (JSONArray) inputObj.get(key);
                List<String[]> inputList = new ArrayList<String[]>();
                if (input != null) {
                    for (int i = 0; i < input.size(); i++) {
                        inputList.add(new String[]{input.get(i).toString()});
                    }

                }
                result[count][0] = inputList;
                count++;
                break;
            }
        }
        return result;
    }

    @Test(dataProvider = "loginTestData", enabled = false, priority = 1)
    public void loginTest(List testData) throws InterruptedException {
        log.info("Started LoginTest");
        if (testData == null) {
            log.info("No Test Data Provided. Skipping loginTest");
            return;
        }
        Iterator<String[]> iterator = testData.iterator();
        while (iterator.hasNext()) {

            String[] inputValues = iterator.next();
            String user_email = inputValues[0];
            String user_phone = inputValues[1];
            String password = inputValues[2];
            String username = "";

            if (!user_email.trim().equals("")) {
                username = user_email;
            } else if (!user_phone.trim().equals("")) {
                username = user_phone;
            }
            System.out.println("Started LoginTest for username : "+username);
            // Setup driver
            Utility.setWebDriverSystemProperty();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            WebDriver driver = new ChromeDriver(options);
            driver.get(Constants.FACEBOOK_URL);
            driver.manage().window().maximize();
            PageFactory.initElements(driver, this);


            //Enter keys to login
            emailField.sendKeys(username);
            passwordField.sendKeys(password);
            loginButton.click();
            Thread.sleep(10000);

            Assert.assertEquals(driver.getCurrentUrl(), Constants.FACEBOOK_WELCOME_URL,
                    "Login not Successful for " + username);

            //logout
            account.click();
            logOut.click();
            driver.close();

        }
        log.info("Completed LoginTest");
    }

    @Test(dataProvider = "messagesTestData", enabled = true, priority = 2)
    public void postMessageTest(List messages) {
        log.info("Started PostMessagesTest");
        if (messages == null) {
            log.info("No Test Data Provided. Skipping loginTest");
            return;
        }
        String[] loginCreds = Utility.getUserCreds("facebookUserCreds");
        Iterator<String[]> iterator = messages.iterator();
        while (iterator.hasNext()) {
            String message = iterator.next()[0];
            Utility.setWebDriverSystemProperty();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            WebDriver driver = new ChromeDriver(options);
            System.out.println("Started PostMessageTest for message : "+message);
            try {

                driver.get(Constants.FACEBOOK_URL);
                Actions actions = new Actions(driver);
                driver.manage().window().maximize();
                PageFactory.initElements(driver, this);

                //Enter keys to login
                emailField.sendKeys(loginCreds[0]);
                passwordField.sendKeys(loginCreds[0]);
                loginButton.click();
                Thread.sleep(10000);

                if (driver.getCurrentUrl().equals(Constants.FACEBOOK_WELCOME_URL)) {
                    log.info("Login Successful");
                    // Post Message Create->Post->EnterMessage->PostIt
                    createButton.click();
                    driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                    postButton.click();
                    driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                    postTextField.sendKeys(message);
                    // Enter TAB 10 times to move to Post It Button
                    for (int i = 0; i < Constants.TAB_COUNT_POST_MESSAGE; i++) {
                        actions.sendKeys(Keys.TAB).build().perform();
                    }
                    actions.sendKeys(Keys.RETURN).build().perform();
                    Thread.sleep(5000);

                    // click on Home Img to check if message is posted
                    homeButton.click();
                    Thread.sleep(5000);
                    Assert.assertTrue(driver.getPageSource().contains(message));

                    //Logout
                    account.click();
                    logOut.click();
                    driver.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Completed PostMessagesTest");
    }
}
