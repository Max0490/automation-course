package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    static Playwright playwright;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
    }

    @ParameterizedTest
    @ValueSource(strings = {"chromium", "firefox"})
    void testHomePage(String browserType) {
        Browser browser = getBrowser(browserType);
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/");
        assertThat(page).hasTitle(Pattern.compile(".*"));

        context.close();
        browser.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"chromium", "firefox"})
    void testLoginPage(String browserType) {
        Browser browser = getBrowser(browserType);
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/login");
        assertThat(page).hasTitle(Pattern.compile(".*"));

        context.close();
        browser.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"chromium", "firefox"})
    void testDropdownPage(String browserType) {
        Browser browser = getBrowser(browserType);
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/dropdown");
        assertThat(page).hasTitle(Pattern.compile(".*"));

        context.close();
        browser.close();
    }

    private Browser getBrowser(String browserType) {
        switch (browserType) {
            case "chromium":
                return playwright.chromium().launch();
            case "firefox":
                return playwright.firefox().launch();
            default:
                throw new IllegalArgumentException("Неизвестный браузер: " + browserType);
        }
    }

    @AfterAll
    static void tearDown() {
        playwright.close();
    }

}
