package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;


public class DynamicLoadingTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;


    @Test
    void testDynamicLoading() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext()) {

            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
            );

            Page page = context.newPage();

            page.onResponse(response -> {
                if (response.url().contains("/dynamic_loading") && response.status() == 200) {
                    System.out.println("✅ Сетевой запрос успешен: " + response.status());
                }
            });

            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
            page.click("button");

            Locator result = page.locator("#finish");
            result.waitFor();

            Assertions.assertEquals("Hello World!", result.textContent().trim());

            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-alternative.zip")));

            System.out.println("🎯 Тест завершен, трассировка сохранена");
        }
    }

    @AfterEach
    void tearDown() {
        if (page != null) {
            page.close();
        }
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