package hooks;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.ConfigManager;
import utils.TestContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Hooks {

    private static boolean isVideoDirCleaned = false;

    @Before
    public void setScenario(Scenario scenario) {
        TestContext.setScenario(scenario);
    }

    @Before("@ui")
    public void setUpUi() {
        cleanVideoDirectory();

        Playwright playwright = Playwright.create();

        boolean headless = Boolean.parseBoolean(ConfigManager.getProperty("headless"));
        
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );
        
        String videoDir = ConfigManager.getProperty("video.dir");
        if (videoDir == null) videoDir = "target/videos/"; 

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get(videoDir));
                
        BrowserContext context = browser.newContext(contextOptions);
        Page page = context.newPage();

        TestContext.setPlaywright(playwright);
        TestContext.setBrowser(browser);
        TestContext.setContext(context);
        TestContext.setPage(page);
    }

    @After("@ui")
    public void tearDownUi(Scenario scenario) {
        if (TestContext.getPage() != null) {
            if (scenario.isFailed()) {
                byte[] screenshot = TestContext.getPage().screenshot(
                        new Page.ScreenshotOptions().setFullPage(true)
                );
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            }
            
            Path videoPath = TestContext.getPage().video().path();
            TestContext.getContext().close(); 
            
            if (videoPath != null) {
                saveVideo(videoPath, scenario);
            }
        }
        
        if (TestContext.getBrowser() != null) {
            TestContext.getBrowser().close();
        }
        if (TestContext.getPlaywright() != null) {
            TestContext.getPlaywright().close();
        }
    }

    private void saveVideo(Path originalPath, Scenario scenario) {
        try {
            String scenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String status = scenario.isFailed() ? "FAILED" : "PASSED";
            String newFileName = scenarioName + "_" + status + ".webm";
            
            String videoDir = ConfigManager.getProperty("video.dir");
            if (videoDir == null) videoDir = "target/videos/";
            
            Path newPath = Paths.get(videoDir, newFileName);

            Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Video guardado: " + newPath.toString());
            
        } catch (Exception e) {
            System.err.println("Error guardando video: " + e.getMessage());
        }
    }

    private void cleanVideoDirectory() {
        if (!isVideoDirCleaned) {
            String videoDirPath = ConfigManager.getProperty("video.dir");
            if (videoDirPath == null) videoDirPath = "target/videos/";

            File videoDir = new File(videoDirPath);

            if (videoDir.exists() && videoDir.isDirectory()) {
                File[] files = videoDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                System.out.println("--- Directorio de videos limpio ---");
            }
            isVideoDirCleaned = true;
        }
    }
}