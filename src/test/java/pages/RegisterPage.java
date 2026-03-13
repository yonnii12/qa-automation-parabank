package pages;

import com.microsoft.playwright.Page;
import models.User;

public class RegisterPage extends BasePage {

    public RegisterPage(Page page) {
        super(page);
    }

    public void openRegisterForm() {
        page.locator("a[href*='register.htm']").click();
    }

    public void fillForm(User user) {
        page.locator("#customer\\.firstName").fill(user.getFirstName());
        page.locator("#customer\\.lastName").fill(user.getLastName());
        page.locator("#customer\\.address\\.street").fill(user.getStreet());
        page.locator("#customer\\.address\\.city").fill(user.getCity());
        page.locator("#customer\\.address\\.state").fill(user.getState());
        page.locator("#customer\\.address\\.zipCode").fill(user.getZipCode());
        page.locator("#customer\\.phoneNumber").fill(user.getPhone());
        page.locator("#customer\\.ssn").fill(user.getSsn());
        page.locator("#customer\\.username").fill(user.getUsername());
        page.locator("#customer\\.password").fill(user.getPassword());
        page.locator("#repeatedPassword").fill(user.getPassword());
    }

    public void submit() {
        page.locator("input[value='Register']").click();
    }
}