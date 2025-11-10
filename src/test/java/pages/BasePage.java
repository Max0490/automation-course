package pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected Page page;
    protected String baseUrl = "https://the-internet.herokuapp.com";

    public BasePage(Page page) {
        this.page = page;
    }

    public BasePage navigateTo(String url) {
        page.navigate(baseUrl + url);
        return this;
    }

    public String getTitle() {
        return page.title();
    }

    protected Page getPage() {
        return page;
    }
}