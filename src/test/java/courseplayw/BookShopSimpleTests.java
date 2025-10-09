package courseplayw;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookShopSimpleTests extends BaseTest {

    private final String searchField = "input[placeholder='Filter books..']";
    private final String Field = "class[placeholder='Filter books..']";

    @BeforeEach
    void openSite() {
        page.navigate("https://automationbookstore.dev/");
    }

    @Test
    void checkTitlePage() {
        assertEquals("Automation Bookstore", page.title());
    }

    @Test
    void searcBookTest() {


        page.fill(searchField, "How Google");
        String bookTitle = page.textContent("#pid4_title");
        assertEquals("How Google Tests Software", bookTitle);
    }

    @Test
    void priceSearchTest() {

        page.fill(searchField, "Agile Testing");
        String bookPrice = page.textContent("#pid3_price");
        assertEquals("$49.07", bookPrice);

    }
}
