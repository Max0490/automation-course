package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ScreenShotTests {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/")));
        page = context.newPage();
    }

    @Test
    void cartTests() {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        page.click("button[onclick='addElement()']");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(getTimestampPath("after_add.png")));

        page.click(".added-manually");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(getTimestampPath("after_remove.png")));

    }

    private Path getTimestampPath(String filename) {
        return Paths.get(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/" + filename);
    }

    @AfterEach
    void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
