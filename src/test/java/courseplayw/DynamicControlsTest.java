package courseplayw;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @Test
    void testDynamicCheckbox() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        Locator checkbox = page.locator("input[type='checkbox']");
        assertTrue(checkbox.isVisible());

        Locator removeButton = page.locator("button:has-text('Remove')");
        assertTrue(removeButton.isVisible());

        removeButton.click();

        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(10000));
        assertFalse(checkbox.isVisible());

        Locator message = page.locator("p:has-text('gone')");
        message.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        assertTrue(message.isVisible(), "Сообщение 'It's gone!' должно появиться");
        assertEquals("It's gone!", message.textContent().trim(), "Текст сообщения должен быть 'It's gone!'");

        Locator addButton = page.locator("button:has-text('Add')");
        assertTrue(addButton.isVisible());

        addButton.click();

        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        assertTrue(checkbox.isVisible());

        Locator addMessage = page.locator("p:has-text('back')");
        addMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        assertTrue(addMessage.isVisible());
        assertEquals("It's back!", addMessage.textContent().trim(), "Текст сообщения должен быть 'It's back!'");

    }


    @AfterEach
    void tearDown() {
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}