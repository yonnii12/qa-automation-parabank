package pages;

import com.microsoft.playwright.Page;
import utils.ConfigManager;

public class BasePage {
    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void navigate() {
        String baseUrl = ConfigManager.getProperty("base.url");
        page.navigate(baseUrl);
    }
}