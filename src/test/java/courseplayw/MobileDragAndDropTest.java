package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MobileDragAndDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Ручная настройка параметров Samsung Galaxy S22 Ultra
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
                .setViewportSize(384, 873)  // Разрешение экрана
                .setDeviceScaleFactor(3.5)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testDragAndDropMobile() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator columnA = page.locator("#column-a");
        Locator columnB = page.locator("#column-b");


        assertEquals("A", columnA.locator("header").textContent(), "Изначально в колонке A должен быть текст 'A'");
        assertEquals("B", columnB.locator("header").textContent(), "Изначально в колонке B должен быть текст 'B'");


        page.evaluate("() => {\n" +
                "  const dataTransfer = new DataTransfer();\n" +
                "  \n" +
                "  // Создаем событие dragstart для элемента A\n" +
                "  const dragStartEvent = new DragEvent('dragstart', { dataTransfer: dataTransfer });\n" +
                "  document.getElementById('column-a').dispatchEvent(dragStartEvent);\n" +
                "  \n" +
                "  // Создаем событие dragover для элемента B\n" +
                "  const dragOverEvent = new DragEvent('dragover', { dataTransfer: dataTransfer });\n" +
                "  document.getElementById('column-b').dispatchEvent(dragOverEvent);\n" +
                "  \n" +
                "  // Создаем событие drop для элемента B\n" +
                "  const dropEvent = new DragEvent('drop', { dataTransfer: dataTransfer });\n" +
                "  document.getElementById('column-b').dispatchEvent(dropEvent);\n" +
                "  \n" +
                "  // Создаем событие dragend для элемента A\n" +
                "  const dragEndEvent = new DragEvent('dragend', { dataTransfer: dataTransfer });\n" +
                "  document.getElementById('column-a').dispatchEvent(dragEndEvent);\n" +
                "}");



        page.waitForTimeout(1000); // Даем время для обновления DOM


        assertEquals("B", columnA.locator("header").textContent(), "После перетаскивания в колонке A должен быть текст 'B'");
        assertEquals("A", columnB.locator("header").textContent(), "После перетаскивания в колонке B должен быть текст 'A'");

        System.out.println("✅ Drag and Drop тест успешно завершен на Samsung Galaxy S22 Ultra!");
        System.out.println("Колонка A: " + columnA.locator("header").textContent());
        System.out.println("Колонка B: " + columnB.locator("header").textContent());
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
