package courseplayw;
import com.microsoft.playwright.*;

import java.util.Collections;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatusCodeInterceptionTest extends BaseTest {

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


        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });
    }

    @Test
    void testMockedStatusCode() {

        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.click("text=404");

        String responseText = page.textContent("body");
        assertTrue(responseText.contains("Mocked Success Response"));
    }

}
