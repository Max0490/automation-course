package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GitHubSearchTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        context.route("**/search**", route -> {
            String originalUrl = route.request().url();
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";
            route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
        });
    }

    @Test
    void testSearchModification() {
        page.navigate("https://github.com/search?q=java");

        page.waitForSelector("[data-testid='results-list']");

        Locator repoStars = page.locator("[href*='stargazers']");
        String starsText = repoStars.first().textContent();

        starsText = starsText.replace("k", "000").replace(".", "");
        int stars = Integer.parseInt(starsText.replace(",", ""));
        assertTrue(stars > 10000);
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
