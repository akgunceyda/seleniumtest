package com.ceyda.celikel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class AmazonTest {

	WebDriver driver;
	
	public AmazonTest() {
		System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
	}
	
	@Before
	public void setup() {
		driver = new ChromeDriver();
		driver.get("https://www.amazon.com/");
		driver.manage().window().maximize();
	}

	@After
	public void teardown() {
		driver.quit();
	}
	
	@Test
	public void testAmazon() {
		
		login();
		search("samsung");
		openSecondSearchResultPage();
		String asin = selectThirdItem();
		addToWishList();
		openWishListPage();
		
		String dataItemId = validateIfItemIsInWishList(asin);
		clickDeleteButton(dataItemId);
		checkIfDeleted(dataItemId);
	}
	
	private void login(){
		driver.findElement(By.id("nav-link-accountList")).click();
		
		driver.findElement(By.id("ap_email")).sendKeys("testceyda@gmail.com");
		driver.findElement(By.id("ap_password")).sendKeys("testceyda!1");
		driver.findElement(By.id("signInSubmit")).click();
	}
	
	private void search(String criteria){
		String result = null;
		try{
			driver.findElement(By.id("twotabsearchtextbox")).sendKeys(criteria+"\n");
			result = driver.findElement(By.id("s-result-count")).getText();
			
			System.out.println(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		assertNotNull(result);
	}
	
	private void openSecondSearchResultPage(){
		boolean result = false;
		try{
			driver.findElement(By.xpath("//div[@id = 'pagn']/span[3]/a")).click();
			waitPageToBeLoaded();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		assertTrue(result);
	}
	
	private String selectThirdItem(){
		String asin = null;
		try{
			asin = driver.findElement(By.xpath("//div[@id = 'atfResults']/ul/li[3]")).getAttribute("data-asin");
			System.out.println(asin);
		}catch(Exception e){
			e.printStackTrace();
		}
		assertNotNull(asin);
		return asin;
	}
	
	private void addToWishList(){
		boolean result = false;
		try{
			driver.findElement(By.xpath("//div[@id = 'atfResults']/ul/li[3]/div/div/div/div[2]/div/div/a")).click();
			driver.findElement(By.id("add-to-wishlist-button-submit")).click();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		assertTrue(result);
		
		waitPageToBeLoaded();
		waitForAWhile(5000);
	}
	
	private void openWishListPage(){
		boolean result = false;
		try{
			driver.findElement(By.xpath("//table[@class='hucInfoTable']/tbody/tr/td/a[@id='WLHUC_viewlist']")).click();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		assertTrue(result);
		
		waitForAWhile(5000);
	}
	
	private String validateIfItemIsInWishList(String asin){
		String dataItemId = null;
		String result = "";
		try{
			dataItemId = driver.findElement(By.xpath("//*[@id='g-items-atf']/div[3]")).getAttribute("data-itemid");
			result = driver.findElement(By.xpath("//*[@id='itemName_"+dataItemId+"']")).getAttribute("href");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		assertTrue(result.indexOf(asin)>=0);
		assertNotNull(dataItemId);
		
		return dataItemId;
	}
	
	private void clickDeleteButton(String dataItemId){
		boolean result = false;
		try{
			driver.findElement(By.xpath("//*[@id='itemAction_"+dataItemId+"']/div/div/div[1]/div[2]/form/span/span/input")).click();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		assertTrue(result);
		
		waitForAWhile(5000);
	}
	
	private void checkIfDeleted(String dataItemId){
		
		String result = null;
		try{
			result = driver.findElement(By.cssSelector("#item_"+dataItemId+" > div > div > div > div")).getText();
		}catch(Exception e){
			e.printStackTrace();
		}

		assertNotNull(result);
		assertTrue(result.equalsIgnoreCase("Deleted"));
	}
	
	private void waitPageToBeLoaded(){
		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
	    wait.until(new Function<WebDriver, Boolean>() {
	        public Boolean apply(WebDriver driver) {
	            System.out.println("Current Window State       : "
	                + String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")));
	            return String
	                .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
	                .equals("complete");
	        }
	    });
	}
	
	private void waitForAWhile(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
