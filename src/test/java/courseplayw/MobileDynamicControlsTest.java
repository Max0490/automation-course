package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        Locator inputField = page.locator("input[type='text']");
        assertTrue(inputField.isDisabled(), "Поле ввода должно быть неактивным изначально");


        Locator enableButton = page.locator("button:has-text('Enable')");
        enableButton.click();

        page.waitForSelector("p:has-text('Enabled')", new Page.WaitForSelectorOptions().setTimeout(10000));

        assertFalse(inputField.isDisabled(), "Поле ввода должно стать активным после клика");

        inputField.fill("Test input on iPad");
        assertEquals("Test input on iPad", inputField.inputValue(),
                "В поле должен быть введен текст");

        System.out.println("✅ Тест завершен успешно! Поле ввода активировано на iPad Pro 11");
    }

    @AfterEach
    void tearDown() {
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
