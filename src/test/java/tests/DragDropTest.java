package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.*;

class DragDropTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    DragDropPage dragDropPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)); // Установите true для CI/CD
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080));
        page = context.newPage();
        dragDropPage = new DragDropPage(page);
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void testDragAToB() {
        dragDropPage.navigateToDragDropPage()
                .performDragAToB();

        assertEquals("A", dragDropPage.getColumnBText());
        assertEquals("B", dragDropPage.getColumnAText());
    }

    @Test
    void testDragAndDropWithComponentChain() {
        dragDropPage.navigateToDragDropPage()
                .dragDropArea()
                .dragAToB()
                .verifyTextInB("A")
                .verifyTextInA("B");
    }

    @Test
    void testCompleteDragAndDropFlow() {
        // Использование комплексного метода
        dragDropPage.completeDragAndDropTest();

        // Дополнительные проверки
        assertTrue(dragDropPage.dragDropArea().isTextInBEqualTo("A"));
        assertTrue(dragDropPage.dragDropArea().isTextInAEqualTo("B"));
    }

    @Test
    void testDragAndDropWithCoordinates() {
        dragDropPage.navigateToDragDropPage()
                .dragDropArea()
                .dragAToBWithCoordinates()
                .verifyTextInB("A");
    }

    @Test
    void testInitialState() {
        dragDropPage.navigateToDragDropPage();

        assertEquals("A", dragDropPage.getColumnAText());
        assertEquals("B", dragDropPage.getColumnBText());
    }
}