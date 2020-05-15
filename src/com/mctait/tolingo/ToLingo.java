package com.mctait.tolingo;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ToLingo {
	
	private static String chromeDriverPath = "C:\\kmt\\workspace\\ToLingo\\libs\\chromedriver.exe";
	private static String toLingoUrl = "https://jobs.tolingo.com/users/sign_in";
	private static String loginEmail = "kevin.mctait@gmail.com";
	private static String password = "";
	private static String accountName = "Kevin McTait";
	private static String successMsg = "You have a ToLingo job!";
	private static String failureMsg = "There is no work";
	private static String noWorkIndicator = "We do not have any jobs for you at the moment";
	private static String alertHeader = "ToLingo";
	private static String alertMessage = "You have a new job!";
	private static String alertToolTip = "ToLingo: you have a new job!";
	private static String pathToAlertIcon = "images/tolingo.png";
	
	private static long	repeatDelay = 5 * 60 * 1000l;
	
	public static void main(String[] args) {
		
		ToLingo toling = new ToLingo();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				toling.testToLingo();
			}
		}, 0, repeatDelay);
	}

	private void testToLingo() {

		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		WebDriver webDriver = new ChromeDriver();
		
		webDriver.get(toLingoUrl);
		try {
			Thread.sleep(2000);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		WebElement login = webDriver.findElement(By.id("user_email"));
		login.sendKeys(loginEmail);
		WebElement password = webDriver.findElement(By.id("user_password"));
		password.sendKeys(ToLingo.password);
		WebElement submitButton = webDriver.findElement(By.name("commit"));
		submitButton.submit();
		
		if(thereIsWork(webDriver)) {
			System.out.println(successMsg);
			try {
				createWindowsAlert();				
			} catch(AWTException e) {
				System.err.println(e.getStackTrace());
			}

		} else {
			System.err.println(failureMsg);
			WebElement logoutList = webDriver.findElement(By.partialLinkText(accountName));
			logoutList.click();
			WebElement logout = webDriver.findElement(By.partialLinkText("Log out"));
			logout.click();
			try {
				Thread.sleep(2000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		webDriver.quit();
		return;
	}
	
	private boolean thereIsWork(WebDriver webDriver) {
		List<WebElement> divs = webDriver.findElements(By.tagName("div"));
		Iterator<WebElement> iterator = divs.iterator();
		
		while (iterator.hasNext()) {
			WebElement div = iterator.next();
			if(div.getText().contains(noWorkIndicator)) {
				return false; 				
			}
		}
		return true;
	}
	
	private void createWindowsAlert() throws AWTException {
		SystemTray tray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(pathToAlertIcon);
		TrayIcon trayIcon = new TrayIcon(image, alertToolTip);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip(alertToolTip);
		tray.add(trayIcon);
		trayIcon.displayMessage(alertHeader, alertMessage, MessageType.INFO);
	}
}
