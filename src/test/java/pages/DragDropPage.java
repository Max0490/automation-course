package pages;

import com.microsoft.playwright.Page;
import components.DragDropArea;

public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;
    private final String pageUrl = "/drag_and_drop";

    public DragDropPage(Page page) {
        super(page);
    }

    // Ленивая инициализация компонента
    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }

    // Навигация с цепочкой вызовов
    public DragDropPage navigateToDragDropPage() {
        navigateTo(pageUrl);
        return this;
    }

    // Вспомогательные методы для быстрого доступа
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

    // Валидационные методы с цепочками вызовов
    public DragDropPage verifyColumnBContainsA() {
        dragDropArea().verifyTextInB("A");
        return this;
    }

    public DragDropPage verifyColumnAContainsB() {
        dragDropArea().verifyTextInA("B");
        return this;
    }

    // Комплексный метод для полного теста
    public DragDropPage completeDragAndDropTest() {
        return navigateToDragDropPage()
                .performDragAToB()
                .verifyColumnBContainsA()
                .verifyColumnAContainsB();
    }
}