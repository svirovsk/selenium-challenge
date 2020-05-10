package tests;


import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.github.javafaker.Faker;
import org.openqa.selenium.support.ui.Select;

public class TestSelenium {

    private WebDriver driver;
    private Faker faker = new Faker(new Locale("de"));
	private JavascriptExecutor js;
	private Random rand = new Random();

	@BeforeTest
	@Parameters("browser")
	public void setup(String browser) throws Exception{
		//Check if parameter passed is 'firefox'
		if(browser.equalsIgnoreCase("firefox")){
		//create firefox instance
			System.setProperty("webdriver.gecko.driver", ".\\\\Driver\\\\geckodriver.exe");
			driver = new FirefoxDriver();
		}
		//Check if parameter passed is 'chrome'
		else if(browser.equalsIgnoreCase("chrome")){
			//create chrome instance
			System.setProperty("webdriver.chrome.driver",".\\\\Driver\\\\chromedriver.exe");
			driver = new ChromeDriver();
		}
		else{
			//If no browser passed throw exception
			throw new Exception("Browser is not correct");
		}
		js = (JavascriptExecutor) driver;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

    @AfterTest
    public void afterClass() {
        driver.quit();
    }

    @Test
    public void verifyCreatePolicy() {
    	

        //maximize window
        driver.manage().window().maximize();

        driver.get("https://s3.eu-central-1.amazonaws.com/faircare-registration-beta/beta12/index.html?flow=schema_da_direkt_bas_qa_challenge&showDaTheme=true#address");
        
        // 1. Schritt
        //verify the name of the first step
        List<WebElement> stepName=driver.findElements(By.xpath("//*[text()='Tarifauswahl']"));
        Assert.assertTrue(!stepName.isEmpty(), "1. page is not loaded - Tarifauswahl");
        
        //Enter data
        Select policySelect = new Select(driver.findElement(By.id( "policy_category")));
        policySelect.selectByIndex(rand.nextInt(3));
        Select daySelect = new Select(driver.findElement(By.name( "day")));
        daySelect.selectByIndex(rand.nextInt(32));
        Select monthSelect = new Select(driver.findElement(By.name( "month")));
        monthSelect.selectByIndex(rand.nextInt(10));
        Select yearSelect = new Select(driver.findElement(By.name( "year")));
        yearSelect.selectByVisibleText("1996");
        
        //Go to next step
        driver.findElement(By.xpath("//*[text()='Weiter']")).click();
        
        // 2.Schritt
        //verify the name of the next step and if it is loaded
        stepName=driver.findElements(By.xpath("//*[text()='Vertragsangaben']"));
        Assert.assertTrue(!stepName.isEmpty(), "2. page is not loaded - Vertragsangaben");
        
        Select startingDateSelect = new Select(driver.findElement(By.id( "starting_at")));
        startingDateSelect.selectByIndex(rand.nextInt(3));
        WebElement insured=driver.findElement(By.id("yes"));
        insured.click();
        WebElement owner=driver.findElement(By.id("me"));
        owner.click();
        
        // Go to next step
        driver.findElement(By.xpath("//*[text()='Weiter']")).click();
        
        // 3.Schritt
        //verify the name of the next step and if it is loaded
        stepName=driver.findElements(By.xpath("//*[text()='Angaben zum Versicherungsnehmer']"));
        Assert.assertTrue(!stepName.isEmpty(), "3. page is not loaded - Angaben zum Versicherungsnehmer");
        
        driver.findElement(By.id("female")).click();
        
        // fill the fields with random user data
        String firstName=faker.name().firstName();
        driver.findElement(By.id("first_name")).sendKeys(firstName);
        String lastName = faker.name().lastName();
        driver.findElement(By.id("last_name")).sendKeys(lastName);
        String emailAdress=faker.bothify("??????##??@gmail.com");
        driver.findElement(By.id("email")).sendKeys(emailAdress);
        String mobile=faker.numerify("176########");
        driver.findElement(By.id("phone_number")).sendKeys(mobile);
        String streetName=faker.address().streetName();
        driver.findElement(By.name("street-address")).sendKeys(streetName);

        String streetNumber=faker.address().buildingNumber();
        driver.findElement(By.name("house-number")).sendKeys(streetNumber);
        String postalCode= faker.numerify("#####");
        driver.findElement(By.id("postalcode_city")).sendKeys(postalCode);
        String city= faker.address().city().replaceAll("\\s+","");
        driver.findElements(By.id("postalcode_city")).get(1).sendKeys(city);

        js.executeScript("scroll(0, document.body.scrollHeight);");
        driver.findElement(By.xpath("//*[text()='Weiter']")).click();


        //4. Schritt
        //verify the name of the next step and if it is loaded
        stepName=driver.findElements(By.xpath("//*[text()='Zahlungsdaten']"));
        Assert.assertTrue(!stepName.isEmpty(), "4. page is not loaded - Zahlungsdaten");
        String iban="DE89 3704 0044 0532 0130 00";
        driver.findElement(By.id("iban")).sendKeys(iban);
        driver.findElement(By.xpath("//*[text()='Weiter']")).click();

        
        //5. Schritt
        //verify the name of the next step and if it is loaded
        stepName=driver.findElements(By.xpath("//*[contains(text(),' Versicherungsschutz')]"));
        Assert.assertTrue(!stepName.isEmpty(), "5. page is not loaded - Ausgewählter Versicherungsschutz");
        driver.findElement(By.id("documents_accepted_at")).click();
        driver.findElement(By.xpath("//*[text()='Zustimmen']")).click();
        driver.findElement(By.xpath("//*[text()='Jetzt Unterlagen senden']")).click();
        
        //6. Schritt
        //verify the name of the next step and if it is loaded
        stepName=driver.findElements(By.xpath("//*[text()='Versicherungsschutz']"));
        Assert.assertTrue(!stepName.isEmpty(), "6. page is not loaded - Versicherungsschutz");
        driver.findElement(By.xpath("//*[text()='Jetzt kostenpflichtig beantragen']")).click();
        

        List<WebElement> success=driver.findElements(By.xpath("//*[text()='Vielen Dank! Der Abschluss war erfolgreich.']"));

        Assert.assertTrue(!success.isEmpty(), "The process was not seccessful");
    }

}
