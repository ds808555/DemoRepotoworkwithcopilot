package com.anhtester.projects.cms.users.pages.products;

import com.anhtester.driver.DriverManager;
import com.anhtester.projects.cms.CommonPageCMS;
import com.anhtester.keywords.WebUI;
import com.anhtester.projects.cms.users.pages.dashboard.DashboardPage;
import org.openqa.selenium.By;

import java.util.ArrayList;

public class ProductInfoPageCMS extends CommonPageCMS {
    private By productName = By.xpath("//h1");
    public static By productPrice = By.xpath("(//div[text()='Discount Price:']/parent::div)/following-sibling::div//strong");
    private By productPriceAlt = By.xpath("//*[contains(normalize-space(.),'Price')]/following::strong[1]");
    private By productUnit = By.xpath("//span[@class='opacity-70']");
    private By productUnitAlt = By.xpath("//*[contains(@class,'opacity-70') or contains(normalize-space(.),'/')][1]");
    private By productDescription = By.xpath("//div[@class = 'mw-100 overflow-auto text-left aiz-editor-data']/p");
    private By productDescriptionAlt = By.xpath("//div[contains(@class,'aiz-editor-data')]");
    private By selectProductName = By.xpath("(//div[contains(@class,'product-name')])[1]");

    private String getTextOrEmpty(By... locators) {
        for (By locator : locators) {
            if (!DriverManager.getDriver().findElements(locator).isEmpty()) {
                String text = WebUI.getTextElement(locator);
                if (text != null && !text.trim().isEmpty()) {
                    return text.trim();
                }
            }
        }
        return "";
    }

    public ArrayList<String> productInfo(String product) {
        WebUI.waitForPageLoaded();
        WebUI.setText(DashboardPage.inputSearchProduct, product);
        WebUI.waitForJQueryLoad();
        WebUI.clickElement(selectProductName);
        WebUI.waitForPageLoaded();
        WebUI.sleep(2);
        String name = getTextOrEmpty(productName);
        String price = getTextOrEmpty(productPrice, productPriceAlt);
        String unit = getTextOrEmpty(productUnit, productUnitAlt);
        String unitProduct = unit.isEmpty() ? "" : (unit.length() > 1 ? unit.substring(1) : unit);
        String description = getTextOrEmpty(productDescription, productDescriptionAlt);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(name);
        arrayList.add(price);
        arrayList.add(unitProduct);
        arrayList.add(description);
        System.out.println("Array" + arrayList);
        System.out.println("Array" + arrayList.get(0));
        return arrayList;
    }
}
