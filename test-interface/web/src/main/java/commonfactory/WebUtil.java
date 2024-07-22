package commonfactory;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import support.BasePage;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WebUtil extends BasePage {

    //waiting for locators to be present or displayed
    public List<WebElement> waitForElements(By by, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 60;
        driverWait(timeOut).until(ExpectedConditions.or(
                ExpectedConditions.presenceOfAllElementsLocatedBy(by),
                ExpectedConditions.visibilityOfAllElementsLocatedBy(by)
        ));
        return driver.findElements(by);
    }

    //waiting for elements to be present or displayed
    public List<WebElement> waitForElements(List<WebElement> elements, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 60;
        By by; //work-around if failed on 1st try
        try {
            by = getByFromElement(elements.get(0));
        } catch (Exception e) {
            by = getByFromElement(elements.get(0));
        }
        return waitForElements(by, timeOut);
    }

    public void waitForElementInvisibility(WebElement element, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 10;
        try {
            driverWait(timeOut).until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
        }
    }

    public int getElementsCount(By by) {
        return driver.findElements(by).size();
    }

    /**
     * Sets the Selenium WebDriver wait time before throwing an exception.
     * Implicit Wait stays in place for the entire duration for which the browser is open.
     *
     * @param time amount of time for implicit wait
     */
    public void setSeleniumImplicitWaitTime(int... time) {
        int timeOut = time.length > 0 ? time[0] : 5000;
        driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.MILLISECONDS);
    }

    public void scrollToElement(WebElement element) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(1000);
    }

    public void scrollToObject(WebElement element) {
        //added to scroll the object just in the middle of the page
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);" +
                "window.scrollBy(0, -window.innerHeight / 4);", element);
    }

    public void scrollToLocator(By by) throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
        Thread.sleep(1000);
    }

    public void shortWait() throws InterruptedException {
        Thread.sleep(50);
    }

    public void mediumWait() throws InterruptedException {
        Thread.sleep(250);
    }

    public void bigWait() throws InterruptedException {
        Thread.sleep(1000);
    }


    public boolean isElementPresent(WebElement element, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 10;
        waitForPageLoadInMilliSec(timeOut);
        try {
            if ((element.isDisplayed()) || (element.isEnabled())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if element is displayed
     *
     * @param element
     * @return
     */
    public boolean isElementThere(WebElement element) {
        try {
            if ((element.isDisplayed()) || (element.isEnabled())) return true;
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * check if element is displayed using locator
     *
     * @param locator
     * @param timeOutInSeconds
     * @return
     */
    public boolean isElementThere(By locator, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 30;
        try {
            WebElement element = waitForElementToBeDisplayed(locator, timeOut);
            return isElementThere(element);
        } catch (Exception e) {
        }

        return false;
    }

    public void scrollToTopOfPage(WebElement element, int... scrollUpCount) {
        int scroll = scrollUpCount.length > 0 ? scrollUpCount[0] * 250 : 250;
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,-" + scroll + ")", element);
    }

    public void scrollToBottomOfPage(WebElement element, int... scrollDownCount) {
        int scroll = scrollDownCount.length > 0 ? scrollDownCount[0] * 250 : 250;
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0, " + scroll + ")", element);
    }

    public void scrollToBottomOfPage() {
        driver.findElement(By.tagName("body")).sendKeys(Keys.END);
    }

    public void clickBtn(WebElement element) {
        element.click();
    }

    public void clearField(WebElement element, int... waitTimeInSeconds) throws Exception {
        int waitTime = waitTimeInSeconds.length > 0 ? waitTimeInSeconds[0] : 60;
        try {
            dynamicWait(element, waitTime);
            element.clear();
        } catch (Exception e) {
            throw new Exception("Unable to clear text field.");
        }
    }

    public void switchToNewWindow() {
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(tabs2.size() - 1));
    }

    public void clickEleAndWaitForNewWindowAndSwitchTo(WebElement triggerElement, WebElement... checkElementOnNewTab) throws Exception {
        int initialWinCount = driver.getWindowHandles().size();
        clickElement(triggerElement);
        driverWait(120).until(ExpectedConditions.numberOfWindowsToBe(initialWinCount + 1));
        switchToNewWindow();
        if (checkElementOnNewTab.length > 0) {
            waitForElementToBeDisplayed(checkElementOnNewTab[0]);
        }
    }

    public void switchToLastWindow() {
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        int latest = tabs2.size() - 1;
        driver.switchTo().window(tabs2.get(latest));
    }

    public void closeCurrentTab() {
        driver.close();
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
    }

    /**
     * Input Keys using JS.
     *
     * @param elem
     * @param value
     */
    public void jsSendKeys(WebElement elem, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value='" + value + "';", elem
        );
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ie) {
            ; /* ignore */
        }
    }

    public void jsClickElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            System.out.println("Error clicking element: " + e.getMessage());
        }
    }

    /**
     * Wait for Visibility on an element.
     *
     * @param ele
     * @throws Exception
     */
    public void waitForVisibility(WebElement ele) {
        driverWait(100).until(ExpectedConditions.visibilityOf(ele));
    }

    public void waitToBeClickable(WebElement ele) {
        driverWait(70).until(ExpectedConditions.elementToBeClickable(ele));
    }

    public String getCurrentYear() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void clickWebElement(WebElement we) {
        try {
            dynamicWait(we);
            we.click();
        } catch (Exception e) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", we);
        }
    }

    protected WebElement findWebElement(String xpath, int... timeoutInSeconds) {
        return waitForElement(By.xpath(xpath), timeoutInSeconds.length > 0 ? timeoutInSeconds[0] : 60);
    }

    public boolean dynamicWait(WebElement we, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 30;
        try {
            driverWait(timeOut).until(ExpectedConditions.visibilityOf(we));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void enterText(WebElement we, CharSequence... text) {

        try {
            dynamicWait(we);
            we.sendKeys(text);
        } catch (Exception e) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].value='" + Arrays.toString(text) + "';", we);
        }

    }

    /**
     * Store all type of chaaracter in one string and create a password randomly choosing elements from that
     *
     * @param length-the length of the password
     *                   throws Exception
     * @return string (Created Password)
     */
    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(Constants.ALL_CHARACTERS.length());
            password.append(Constants.ALL_CHARACTERS.charAt(randomIndex));
        }
        return password.toString();
    }

    public void waitForPageLoadInMilliSec(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void switchIframe(WebElement we, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 60;
        dynamicWait(we, timeOut);
        driver.switchTo().frame(we);
    }

    /**
     * Switch to default content
     */
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    //similar to jsClick and check element visibility before action
    public void clickWithJavaScript(WebElement we) {
        try {
            dynamicWait(we);
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", we);
        } catch (Exception e) {

        }
    }

    public String getTextFromPage(WebElement we) {
        String str = null;
        try {
            dynamicWait(we);
            str = we.getText();
        } catch (Exception e) {
        }
        return str;

    }


    public String parseDate(String inputDate, String fromFormat, String toFormat) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(fromFormat);
        Date date = formatter.parse(inputDate);
        DateFormat dateFormat = new SimpleDateFormat(toFormat);
        return dateFormat.format(date);
    }

    public boolean isButtonByNameExists(String label) {
        return !waitForElements(By.xpath("//button[contains(., '" + label + "')]")).isEmpty();
    }

    public List<String> delimitedStringToList(String value) {
        return Stream.of(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private By getByFromElement(WebElement element) {
        By by = null;
        String[] pathVariables;
        if (element.toString().contains("->")) {
            pathVariables = (element.toString().split("->")[1]
                    .replaceFirst("(?s)(.*)\\]", "$1" + "")).split(": ");
        } else {
            pathVariables = (element.toString().split("By.")[1]
                    .replaceFirst("(?s)(.*)\\'", "$1" + "")).split(": ");
        }

        String selector = pathVariables[0].trim();
        String value = pathVariables[1].trim();

        switch (selector) {
            case "id":
                by = By.id(value);
                break;
            case "className":
            case "class name":
                by = By.className(value);
                break;
            case "tagName":
                by = By.tagName(value);
                break;
            case "xpath":
                by = By.xpath(value);
                break;
            case "css selector":
            case "cssSelector":
                by = By.cssSelector(value);
                break;
            case "linkText":
                by = By.linkText(value);
                break;
            case "name":
                by = By.name(value);
                break;
            case "partialLinkText":
                by = By.partialLinkText(value);
                break;
            default:
                throw new IllegalStateException("locator : " + selector + " not found!!!");
        }
        return by;
    }


    public void verifyFail(String message) {
        softAssert.fail(message + " expected [true] but found [false]");
    }

    public void clearFields(WebElement element) {
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
    }

    public String trimStringBySize(String value, int size) {
        return value.substring(0, Math.min(value.length(), size));
    }


    public void refreshPage(int... milliseconds) throws Exception {
        int timeOut = milliseconds.length > 0 ? milliseconds[0] : 7500;
        try {
            driver.navigate().refresh();
        } catch (Exception e) {
            if (checkIfAlertIsPresent()) driver.switchTo().alert().accept();
            ((JavascriptExecutor) driver).executeScript("history.go(0)");
        }
        if (checkIfAlertIsPresent()) driver.switchTo().alert().accept();
        waitForPageLoadInMilliSec(timeOut);
    }

    public WebElement getShadowRootElement(WebElement element) throws Exception {
        WebElement returnObj = null;
        Object shadowRoot = ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", element);
        if (shadowRoot instanceof WebElement) {
            returnObj = (WebElement) shadowRoot;
        } else if (shadowRoot instanceof Map) {
            Map<String, Object> shadowRootMap = (Map<String, Object>) shadowRoot;
            String shadowRootKey = (String) shadowRootMap.keySet().toArray()[0];
            String id = (String) shadowRootMap.get(shadowRootKey);
            RemoteWebElement remoteWebElement = new RemoteWebElement();
            remoteWebElement.setParent((RemoteWebDriver) driver);
            remoteWebElement.setId(id);
            returnObj = remoteWebElement;
        } else {
            throw new Exception("Unexpected return type for shadowRoot in expandRootElement()");
        }
        return returnObj;
    }

    public WebElement getShadowWebElement(String jsLocator) throws InterruptedException {
        return (WebElement) ((JavascriptExecutor) driver).executeScript("return " + jsLocator);
    }


    public void verifyElementNotPresent(WebElement element, String... elementName) {
        String name = elementName.length > 0 ? elementName[0] : "";
        try {
            softAssert.assertTrue(!element.isDisplayed(), "Element " + name + " is not present");
        } catch (NoSuchElementException e) {
            softAssert.assertTrue(true, "Element " + name + " is present");
        }
    }

    public void verifyElementIsPresent(WebElement element, String... elementName) {
        String name = elementName.length > 0 ? elementName[0] : "";
        softAssert.assertTrue(isElementPresent(element), "Element '" + name + "' is present");
    }


    public void waitForURLToBeUpdated(String url) {
        WebDriverWait waitURL = new WebDriverWait(driver, Duration.ofSeconds(60));
        waitURL.until(ExpectedConditions.urlContains(url));
    }

    public void hoverToElement(WebElement element, int... timeOutInSeconds) {
        int timeOut = timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 1;
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
        waitForPageLoadInMilliSec(timeOut * 1000);
    }

    public void doubleClick(WebElement element) throws Exception {
        // Actions class does not work as it clicks on a different element for the second click
        clickElement(element);
        clickElement(element);
    }

    /**
     * Common method to assert if two texts are equal
     *
     * @param actual   String 1
     * @param expected String 2
     */
    public void verifyEquals(String actual, String expected, String... msg) {
        String message = msg.length > 0 ? msg[0] : "";
        String string1 = (actual != null) ? actual : "";
        String string2 = (expected != null) ? expected : "";
        softAssert.assertEquals(string1, string2, message);
    }

    public void verifyEquals(boolean actual, boolean expected, String msg) {
        softAssert.assertEquals(actual, expected, msg);
    }

    public void verifyEquals(int actual, int expected, String msg) {
        softAssert.assertEquals(actual, expected, msg);
    }


    /**
     * Wait for elememt to contain text, retrying with intervals
     *
     * @param element
     * @param text
     * @param retryLimit            - No of retries needed
     * @param timeOutInMilliSeconds - Timeout for checking
     * @throws Exception
     */
    public void waitTextByInterval(WebElement element, String text, int retryLimit, int... timeOutInMilliSeconds) throws Exception {
        int timeOut = timeOutInMilliSeconds.length > 0 ? timeOutInMilliSeconds[0] : 1;
        int retry = 0;
        while (!element.getText().contains(text) && retry <= retryLimit) {
            waitTillElementsLoads(timeOut);
            driver.navigate().refresh();
            waitTillElementsLoads(4000);
            retry++;
        }
    }

    /**
     * verify if statement is True
     *
     * @param condition
     * @param msg
     */
    public void verifyTrue(boolean condition, String... msg) {
        softAssert.assertTrue(condition, msg.length > 0 ? msg[0] : "Condition is ");
    }

    /**
     * verify if statement is False
     *
     * @param condition
     * @param msg
     */
    public void verifyFalse(boolean condition, String... msg) {
        softAssert.assertFalse(condition, msg.length > 0 ? msg[0] : "Condition is ");
    }

    /**
     * verify if toast message is present
     *
     * @param toastMessage
     */
    public void verifyToastMessageIsPresent(WebElement toastMessage) {
        softAssert.assertTrue(dynamicWait(toastMessage), "Toast message is hidden");
    }

    /**
     * Wait for page title to contain certain text
     *
     * @param text             expected page title
     * @param timeOutInSeconds optional timeout
     */
    public void waitUntilPageTitleContains(String text, int... timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : 10));
        wait.until(ExpectedConditions.titleContains(text));
    }


    /**
     * Verifies that a table contains expected string in a given row number
     *
     * @param table WebElement should be a properly defined HTML table
     * @param row   row number to search at
     * @param text  string to search for
     */
    public void verifyTableRowContains(WebElement table, int row, String text, String... message) {
        List<WebElement> tableRows = table.findElements(By.xpath(".//tbody/tr"));
        verifyTrue(tableRows.get(row).getText().contains(text), message.length > 0 ? message[0] : "Table row contains " + text);
    }

    /**
     * Retrieves the text attribute of a given element
     *
     * @param element WebElement
     * @return String text retrieved
     */
    public String getElementText(WebElement element) {
        try {
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isElementVisible(WebElement element, int... timeOutInSeconds) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException nsee) {
            return false;
        }
    }

    /**
     * verify if text is present on the page
     *
     * @param text
     * @return
     */
    public boolean verifyPageContainsText(String text) {
        return driver.getPageSource().contains(text);
    }

    /**
     * Verifies that a given text contains expected list of text
     *
     * @param thisText     text to search at
     * @param containsText List of text to search for
     **/
    protected void verifyTextContains(String thisText, String... containsText) {
        for (String text : containsText) {
            verifyTrue(thisText.contains(text), "Given text contains " + text);
        }
    }

    /**
     * Verifies that a given text does not contain expected list of text
     *
     * @param thisText     text to search at
     * @param containsText List of text to search for
     **/
    protected void verifyTextNotContains(String thisText, String... containsText) {
        for (String text : containsText) {
            verifyTrue(!thisText.contains(text), "Given text not contains " + text);
        }
    }


    /**
     * Retrieves all descendant nodes of a given WebElement
     *
     * @param element WebElement
     **/
    protected List<WebElement> getElementDescendants(WebElement element) {
        return element.findElements(By.xpath(".//*"));
    }

    /**
     * retry element click based on a condition
     *
     * @param btnToClick
     * @param condition
     * @param retryCount
     * @throws Exception
     */
    public void retryClick(WebElement btnToClick, Predicate<Boolean> condition, int... retryCount) throws Exception {
        int retries = retryCount.length > 0 ? retryCount[0] : 3;
        int cnt = 0;
        while (condition.test(true)) {
            jsClickElement(btnToClick);
            waitForPageLoadInMilliSec(2000);
            cnt++;
            if (cnt == retries) break;
        }
    }

    /**
     * Common method to select a date from datepicker
     * that does not support direct input of value
     *
     * @param datePickerBtn WebElement
     * @param day           int
     * @throws Exception .
     */
    public void selectDateFromDatePicker(WebElement datePickerBtn, int day) throws Exception {
        clickElement(datePickerBtn);
        String xpath = "//td//div[contains(text(),' sometext ')]";
        if (day > LocalDate.now(ZoneId.of("Australia/Sydney")).lengthOfMonth()) {
            clickElement(findWebElement("//button[@aria-label='Next month']"));
            day = 1;
        }
        String finalXpath = xpath.replace("sometext", Integer.toString(day));
        clickElement(findWebElement(finalXpath));
    }

    /**
     * Common method to select a text from mat-select element
     *
     * @param dropdown   mat-select
     * @param optionText String
     */
    public void selectOptionFromMatSelect(WebElement dropdown, String optionText) throws Exception {
        clickElement(dropdown);
        clickElement(findWebElement("//mat-option/span[contains(text(),'" + optionText + "')]"));
    }

    /**
     * Enters text slowly how real user does
     *
     * @param we
     * @param text - the text to input
     */
    public void enterTextSlowly(WebElement we, String text) {
        dynamicWait(we);
        IntStream.range(0, text.length()).forEach(characterIndex -> {
            char character = text.charAt(characterIndex);
            we.sendKeys(String.valueOf(character));
            try {
                waitForPageLoadInMilliSec(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method is used to click on button by display name
     *
     * @param buttonname Name of the button display on UI
     * @throws Exception
     */
    public void clickButtonByDisplayName(String buttonname, int... index) throws Exception {
        int ind = index.length > 0 ? index[0] : 1;
        String xpath = "(//button[text()='" + buttonname + "'])[" + ind + "]";
        WebElement button = waitForElement(By.xpath(xpath), 2);
        clickElement(button);
    }

    /**
     * Wait until text is present in page (even once only)
     *
     * @param text
     * @param waitTimeInSeconds
     */
    public void waitUntilTextIsPresent(String text, int... waitTimeInSeconds) {
        int waitTime = waitTimeInSeconds.length > 0 ? waitTimeInSeconds[0] : 10;
        By by = By.xpath("(//*[normalize-space()='" + text + "'])[1]");
        driverWait(waitTime).until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(by),
                ExpectedConditions.visibilityOfElementLocated(by)
        ));
    }

    /**
     * check if alert is displayed
     *
     * @param waitTimeInSeconds
     * @return
     */
    public boolean checkIfAlertIsPresent(int... waitTimeInSeconds) {
        int waitTime = waitTimeInSeconds.length > 0 ? waitTimeInSeconds[0] : 10;
        try {
            driverWait(waitTime).until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void browserBack() {
        driver.navigate().back();
    }

    public void navigateToURL(String url) {
        driver.navigate().to(url);
        waitForPageLoadInMilliSec(5000);
    }

    /**
     * get the total column of table
     *
     * @param table
     * @return
     */
    public int getColumnCount(String table) throws Exception {
        int col = driver.findElements(By.xpath(table + "/thead/tr/th")).size();
        return col;
    }

    /**
     * get the total row of table
     *
     * @param table
     * @return
     */
    public int getRowCount(String table) throws Exception {
        int rows = driver.findElements(By.xpath(table + "/tbody/tr")).size();
        return rows;
    }

    /**
     * Finds window count
     *
     * @return
     */
    public int findWindowCount() {
        return driver.getWindowHandles().size();
    }


    /**
     * click on the button based on passed parameter
     *
     * @param label
     * @return
     */
    public void clickButtonByLabel(String label) throws Exception {
        String xpath = "//a[contains(., '" + label + "')]";
        WebElement button = waitForElement(By.xpath(xpath), 10);
        scrollToObject(button);
        jsClickElement(button);
    }


}
