package courseplayw;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));


        Locator figures = page.locator(".figure");
        int count = figures.count();

        assertTrue(count > 0, "Должны быть найдены элементы с классом .figure");
        System.out.println("✅ Найдено элементов: " + count);

        for (int i = 0; i < count; i++) {
            System.out.println("🔍 Тестирование элемента #" + (i + 1));

            Locator figure = figures.nth(i);


            figure.hover();


            page.waitForTimeout(500); // Небольшая задержка для анимации


            Locator profileLink = figure.locator("text=View profile");
            assertTrue(profileLink.isVisible(),
                    "Ссылка 'View profile' должна быть видимой после наведения");

            System.out.println("✅ Ссылка 'View profile' появилась");


            profileLink.click();


            page.waitForURL("**/users/**");


            String currentUrl = page.url();
            assertTrue(currentUrl.contains("/users/"),
                    "URL должен содержать /users/, текущий URL: " + currentUrl);


            String[] urlParts = currentUrl.split("/users/");
            assertTrue(urlParts.length > 1, "URL должен содержать ID пользователя");
            assertTrue(urlParts[1].matches("\\d+"), "ID пользователя должен быть числом");

            System.out.println("✅ Переход на страницу пользователя: " + currentUrl);


            Locator pageHeader = page.locator("h1");
            assertTrue(pageHeader.isVisible(), "На странице должен быть заголовок");

            System.out.println("✅ Заголовок страницы: " + pageHeader.textContent());


            page.goBack();


            page.waitForURL("https://the-internet.herokuapp.com/hovers");

            System.out.println("✅ Успешно вернулись на страницу hovers");
        }
    }


    @AfterEach
    void teardown() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void teardownClass() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
