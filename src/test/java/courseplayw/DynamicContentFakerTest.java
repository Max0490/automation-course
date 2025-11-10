package courseplayw;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DynamicContentFakerTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Faker faker;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        faker = new Faker();
    }

    @AfterEach
    void teardown() {
        browser.close();
        playwright.close();
    }

    @Test
    void testDynamicContentWithFaker() {

        String fakeName = faker.name().fullName();
        System.out.println("Сгенерированное имя: " + fakeName);


        page.route("**/dynamic_content", route -> {
            // Простой HTML с нашим именем
            String fakeHtml = "<html><body>" +
                    "<div class='row'>" +
                    "<div class='large-10 columns'>" + fakeName + "</div>" +
                    "</div>" +
                    "</body></html>";

            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setBody(fakeHtml)
            );
        });


        page.navigate("https://the-internet.herokuapp.com/dynamic_content");


        String pageText = page.textContent("body");
        assertTrue(pageText.contains(fakeName), "Страница должна содержать сгенерированное имя");

        System.out.println("Тест пройден! Имя '" + fakeName + "' найдено на странице");
    }
}