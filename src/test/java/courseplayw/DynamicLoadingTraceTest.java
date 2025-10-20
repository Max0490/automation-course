package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @Test
    void testDynamicLoadingWithTrace() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();

        // Настройка трассировки
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
                .setTitle("Dynamic Loading Test")  // Устанавливаем название здесь
        );

        page = context.newPage();

        try {
            // Шаг 1: Переход на страницу
            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
            System.out.println("Страница загружена");

            // Шаг 2: Проверка начального состояния
            Locator hiddenElement = page.locator("#finish");
            Assertions.assertTrue(hiddenElement.isHidden(),
                    "Элемент должен быть скрыт в начале теста");

            // Шаг 3: Клик на кнопку "Start"
            page.click("button");
            System.out.println("Клик на кнопку Start выполнен");

            // Шаг 4: Ожидание появления текста
            hiddenElement.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            Assertions.assertTrue(hiddenElement.isVisible(),
                    "Элемент должен стать видимым после загрузки");

            // Шаг 5: Проверка текста
            String finishText = hiddenElement.textContent();
            Assertions.assertEquals("Hello World!", finishText.trim(),
                    "Текст должен быть 'Hello World!'");

            System.out.println("Текст успешно загружен: " + finishText);

        } finally {
            // Сохранение трассировки в любом случае
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-dynamic-loading.zip"))
            );
        }
    }

    @Test
    void testDynamicLoadingWithScreenshots() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
                .setTitle("Dynamic Loading with Screenshots")
        );

        page = context.newPage();

        try {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("before-loading.png")));

            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

            Locator startButton = page.locator("button");
            Locator loadingIndicator = page.locator("#loading");
            Locator finishElement = page.locator("#finish");

            Assertions.assertTrue(startButton.isVisible(), "Кнопка Start должна быть видима");
            Assertions.assertTrue(loadingIndicator.isHidden(), "Индикатор загрузки должен быть скрыт");
            Assertions.assertTrue(finishElement.isHidden(), "Финальный текст должен быть скрыт");

            startButton.click();

            loadingIndicator.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("during-loading.png")));

            finishElement.waitFor(new Locator.WaitForOptions().setTimeout(10000));

            String resultText = finishElement.textContent();
            Assertions.assertEquals("Hello World!", resultText.trim());

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("after-loading.png")));

            System.out.println("✅ Тест пройден. Текст: " + resultText);

        } finally {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-with-screenshots.zip"))
            );
        }
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