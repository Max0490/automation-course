package courseplayw;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Тесты для the-internet.herokuapp.com")
@Feature("Работа с JavaScript-алертами")
public class AllureTest {
    private static ExtentReports extent;
    private Browser browser;
    private Playwright playwright;
    private Page page;
    private ExtentTest test;

    @BeforeAll
    static void setupExtent() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("allure-results/extent-report.html");
        reporter.config().setDocumentTitle("Playwright Extent Report");
        reporter.config().setTheme(Theme.DARK);
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();

        // Создаем тест в ExtentReports
        test = extent.createTest(testInfo.getDisplayName(),
                testInfo.getTestMethod().get().getName());
    }

    @Test
    @Story("Проверка JS Alert")
    @Description("Тест взаимодействия с JS Alert и проверка результата")
    @Severity(SeverityLevel.NORMAL)
    void testJavaScriptAlert() {
        try {
            navigateToAlertsPage();
            String alertMessage = handleJsAlert();
            verifyResultText();
            captureSuccessScreenshot();

            logExtent(Status.PASS, "Тест успешно завершен с сообщением: " + alertMessage);

        } catch (Exception e) {
            handleTestFailure(e);
            throw e;
        }
    }

    @Step("Открыть страницу с алертами")
    private void navigateToAlertsPage() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        assertEquals("JavaScript Alerts", page.locator("h3").textContent(),
                "Страница должна содержать заголовок 'JavaScript Alerts'");
        logExtent(Status.INFO, "Страница с алертами загружена");
    }

    @Step("Обработать JS Alert")
    private String handleJsAlert() {
        CompletableFuture<String> alertMessageFuture = new CompletableFuture<>();

        page.onDialog(dialog -> {
            String message = dialog.message();
            alertMessageFuture.complete(message);
            dialog.accept();
        });

        page.click("button[onclick='jsAlert()']");
        logExtent(Status.INFO, "Клик по кнопке JS Alert выполнен");

        try {
            String alertMessage = alertMessageFuture.get(5, TimeUnit.SECONDS);
            logExtent(Status.INFO, "Alert обработан: " + alertMessage);
            return alertMessage;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обработать alert", e);
        }
    }

    @Step("Проверить текст результата")
    private void verifyResultText() {
        page.waitForCondition(() ->
                page.locator("#result").textContent().contains("successfully"));

        String resultText = page.locator("#result").textContent();
        assertEquals("You successfully clicked an alert", resultText,
                "Текст результата должен соответствовать ожидаемому");
        logExtent(Status.INFO, "Результирующий текст проверен: " + resultText);
    }

    @Step("Сделать скриншот при успешном выполнении")
    private void captureSuccessScreenshot() {
        String screenshotName = "success-screenshot.png";
        Path screenshotPath = Paths.get("allure-results", screenshotName);

        try {
            byte[] screenshot = page.screenshot();
            Files.createDirectories(Paths.get("allure-results"));
            Files.write(screenshotPath, screenshot);


            try (InputStream screenshotStream = new ByteArrayInputStream(screenshot)) {
                Allure.addAttachment("Успешное выполнение", "image/png", screenshotStream, ".png");
            }


            test.pass("Скриншот успешного выполнения",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath.toString()).build());

        } catch (Exception e) {
            logExtent(Status.WARNING, "Не удалось сделать скриншот: " + e.getMessage());
        }
    }

    private void logExtent(Status status, String message) {
        test.log(status, message);
    }

    private void handleTestFailure(Exception e) {
        byte[] failureScreenshot = page.screenshot();

        try (InputStream failureStream = new ByteArrayInputStream(failureScreenshot)) {
            Allure.addAttachment("Ошибка теста", "image/png", failureStream, ".png");
        } catch (Exception ex) {
            logExtent(Status.WARNING, "Не удалось добавить скриншот ошибки в Allure: " + ex.getMessage());
        }

        String screenshotName = "error-screenshot.png";
        Path screenshotPath = Paths.get("allure-results", screenshotName);

        try {
            Files.write(screenshotPath, failureScreenshot);
            test.fail("Ошибка теста: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath.toString()).build());
        } catch (Exception ex) {
            test.fail("Ошибка теста: " + e.getMessage());
        }
    }

    @Test
    @Story("Проверка JS Confirm")
    @Description("Тест взаимодействия с JS Confirm и проверка результата")
    @Severity(SeverityLevel.NORMAL)
    void testJavaScriptConfirm() {
        try {
            navigateToAlertsPage();
            String confirmMessage = handleJsConfirm();
            verifyConfirmResultText();
            captureSuccessScreenshot();

            logExtent(Status.PASS, "Тест Confirm успешно завершен с сообщением: " + confirmMessage);

        } catch (Exception e) {
            handleTestFailure(e);
            throw e;
        }
    }

    @Step("Обработать JS Confirm")
    private String handleJsConfirm() {
        CompletableFuture<String> confirmMessageFuture = new CompletableFuture<>();

        page.onDialog(dialog -> {
            String message = dialog.message();
            confirmMessageFuture.complete(message);
            dialog.accept(); // Нажимаем OK
        });

        page.click("button[onclick='jsConfirm()']");
        logExtent(Status.INFO, "Клик по кнопке JS Confirm выполнен");

        try {
            String confirmMessage = confirmMessageFuture.get(5, TimeUnit.SECONDS);
            logExtent(Status.INFO, "Confirm обработан: " + confirmMessage);
            return confirmMessage;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обработать confirm", e);
        }
    }

    @Step("Проверить текст результата для Confirm")
    private void verifyConfirmResultText() {
        page.waitForCondition(() ->
                page.locator("#result").textContent().contains("Ok"));

        String resultText = page.locator("#result").textContent();
        assertEquals("You clicked: Ok", resultText,
                "Текст результата должен соответствовать ожидаемому для Confirm");
        logExtent(Status.INFO, "Результирующий текст Confirm проверен: " + resultText);
    }

    @AfterEach
    void tearDownEach() {
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

    @AfterAll
    static void tearDown() {
        if (extent != null) {
            extent.flush();
        }
    }
}