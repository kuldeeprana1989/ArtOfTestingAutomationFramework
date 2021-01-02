package com.artoftesting.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SauceDemoInventoryPage {

	WebDriver driver;
	
	@FindBy(css="div.product_label")
	WebElement productLabel;

	
	public SauceDemoInventoryPage(WebDriver driver) {
		this.driver = driver;
        PageFactory.initElements(driver, this);
	}
	
	
	public String getProductLabel() {
		return productLabel.getText();
	}
	
	
}
