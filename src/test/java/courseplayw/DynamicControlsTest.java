package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.DynamicControlsPage;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;
    DynamicControlsPage controlsPage;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        controlsPage = new DynamicControlsPage(page);

        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Test
    void testCheckboxRemoval() {
        // Проверяем что чекбокс есть в начале
        assertTrue(controlsPage.isCheckboxVisible());

        // Нажимаем кнопку Remove
        controlsPage.clickRemoveButton();

        // Ждем
        controlsPage.waitForCheckboxToDisappear();

        // Проверяем что чекбокс исчез
        assertFalse(controlsPage.isCheckboxVisible());
    }

    @AfterEach
    void teardown() {
        browser.close();
        playwright.close();
    }
}