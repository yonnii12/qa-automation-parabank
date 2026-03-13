package utils;

import com.microsoft.playwright.*;
import io.cucumber.java.Scenario;

public class TestContext {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static Scenario scenario;

    public static Playwright getPlaywright() { return playwright; }
    public static void setPlaywright(Playwright playwright) { TestContext.playwright = playwright; }

    public static Browser getBrowser() { return browser; }
    public static void setBrowser(Browser browser) { TestContext.browser = browser; }

    public static BrowserContext getContext() { return context; }
    public static void setContext(BrowserContext context) { TestContext.context = context; }

    public static Page getPage() { return page; }
    public static void setPage(Page page) { TestContext.page = page; }

    public static Scenario getScenario() { return scenario; }
    public static void setScenario(Scenario scenario) { TestContext.scenario = scenario; }
}