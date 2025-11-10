package pages;

import com.microsoft.playwright.Page;
import components.DragDropArea;

public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;
    private final String pageUrl = "/drag_and_drop";

    public DragDropPage(Page page) {
        super(page);
    }


    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }


    public DragDropPage navigateToDragDropPage() {
        navigateTo(pageUrl);
        return this;
    }


    public DragDropPage performDragAToB() {
        dragDropArea().dragAToB();
        return this;
    }

    public String getColumnBText() {
        return dragDropArea().getTextB();
    }

    public String getColumnAText() {
        return dragDropArea().getTextA();
    }


    public DragDropPage verifyColumnBContainsA() {
        dragDropArea().verifyTextInB("A");
        return this;
    }

    public DragDropPage verifyColumnAContainsB() {
        dragDropArea().verifyTextInA("B");
        return this;
    }


    public DragDropPage completeDragAndDropTest() {
        return navigateToDragDropPage()
                .performDragAToB()
                .verifyColumnBContainsA()
                .verifyColumnAContainsB();
    }
}