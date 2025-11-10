package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;

public class DragDropArea {
    private final Page page;
    private Locator elementA;
    private Locator elementB;
    private Locator dropZone;

    public DragDropArea(Page page) {
        this.page = page;
    }

    // Ленивая инициализация элементов
    private Locator getElementA() {
        if (elementA == null) {
            elementA = page.locator("#column-a");
        }
        return elementA;
    }

    private Locator getElementB() {
        if (elementB == null) {
            elementB = page.locator("#column-b");
        }
        return elementB;
    }

    private Locator getDropZone() {
        if (dropZone == null) {
            dropZone = page.locator("#column-b");
        }
        return dropZone;
    }

    public DragDropArea dragAToB() {
        Locator source = getElementA();
        Locator target = getElementB();

        source.dragTo(target);
        return this;
    }


    public DragDropArea dragAToBWithCoordinates() {
        Locator source = getElementA();
        Locator target = getElementB();

        BoundingBox targetBox = target.boundingBox();
        source.hover();
        page.mouse().down();
        page.mouse().move(targetBox.x + targetBox.width / 2,
                targetBox.y + targetBox.height / 2);
        page.mouse().up();

        return this;
    }


    public String getTextA() {
        return getElementA().textContent().trim();
    }

    public String getTextB() {
        return getElementB().textContent().trim();
    }


    public boolean isTextInBEqualTo(String expectedText) {
        return getTextB().equals(expectedText);
    }

    public boolean isTextInAEqualTo(String expectedText) {
        return getTextA().equals(expectedText);
    }


    public DragDropArea verifyTextInB(String expectedText) {
        if (!isTextInBEqualTo(expectedText)) {
            throw new AssertionError("Expected text in B: " + expectedText +
                    ", but got: " + getTextB());
        }
        return this;
    }

    public DragDropArea verifyTextInA(String expectedText) {
        if (!isTextInAEqualTo(expectedText)) {
            throw new AssertionError("Expected text in A: " + expectedText +
                    ", but got: " + getTextA());
        }
        return this;
    }
}