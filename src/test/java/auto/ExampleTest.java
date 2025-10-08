package auto;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest extends BaseTest {


    @Test
    void dummyTest() {
        assertTrue(true);
    }

    @Test
    void testNestedFrames() {
        page.navigate("https://the-internet.herokuapp.com/nested_frames");

        // Переключение на фрейм left
        Frame leftFrame = page.frame("frame-left");
        String leftText = leftFrame.locator("body").textContent();
        Assertions.assertTrue(leftText.contains("LEFT"));

        // Переключение на фрейм middle
        Frame middleFrame = page.frame("frame-middle");
        String middleText = middleFrame.locator("body").textContent();
        Assertions.assertTrue(middleText.contains("MIDDLE"));

        // Открытие новой вкладки
        Page newPage = page.context().newPage();
        newPage.navigate("https://the-internet.herokuapp.com");

        // Закрытие новой вкладки
        newPage.close();
    }
}