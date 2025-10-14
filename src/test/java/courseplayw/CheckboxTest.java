package courseplayw;

import com.microsoft.playwright.*;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора/снятия чекбоксов")
    void testCheckboxes() {

        page.navigate("https://the-internet.herokuapp.com/checkboxes");

        Locator firstCheckbox = page.locator("input[type='checkbox']").first();
        Locator secondCheckbox = page.locator("input[type='checkbox']").nth(1);

        if (firstCheckbox.isChecked()) {
            firstCheckbox.uncheck();
        }
        assertFalse(firstCheckbox.isChecked());


        if (!secondCheckbox.isChecked()) {
            secondCheckbox.check();
        }
        assertTrue(secondCheckbox.isChecked());
}}
