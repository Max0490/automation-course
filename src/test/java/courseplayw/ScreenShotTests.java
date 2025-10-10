package courseplayw;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
    void testHomePageVisual() throws IOException {
        page.navigate("https://the-internet.herokuapp.com");


        Path actual = Paths.get("actual.png");
        page.screenshot(new Page.ScreenshotOptions().setPath(actual));

        Path expected = Paths.get("expected.png");

        if (!Files.exists(expected)) {
            Files.copy(actual, expected);
            System.out.println("Создал эталонный скрин");
            return;
        }
        long result = Files.mismatch(actual, expected);
        assertEquals(-1, result);
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
    void attachScreenshotOnFailure(TestInfo testInfo) {

        byte[] screenshot = page.screenshot();
        Allure.addAttachment(
                "Screenshot - " + testInfo.getDisplayName(),
                "image/png",
                new ByteArrayInputStream(screenshot),
                ".png"
        );


        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
