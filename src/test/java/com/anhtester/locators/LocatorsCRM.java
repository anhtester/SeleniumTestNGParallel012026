package com.anhtester.locators;

import org.openqa.selenium.By;

public class LocatorsCRM {
    public static By inputEmail = By.id("email");
    public static By inputPassword = By.id("password");
    public static By buttonLogin = By.xpath("//button[normalize-space()='Login']");
    public static By menuDashboard = By.xpath("//span[normalize-space()='Dashboard']");
    public static By alertErrorMessage = By.xpath("//div[contains(@class,'alert-danger')]");
    public static By alertEmailRequiredMessage = By.xpath("//div[normalize-space()='The Email Address field is required.']");
    public static By alertPasswordRequiredMessage = By.xpath("//div[normalize-space()='The Password field is required.']");

}
