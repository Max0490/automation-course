package courseplayw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodosApi() throws Exception {

        APIResponse response = requestContext.get("/todos");


        assertEquals(200, response.status());
        System.out.println("✅ Статус код: " + response.status());


        String responseBody = response.text();
        List<Map<String, Object>> todos = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );


        assertFalse(todos.isEmpty(), "Список todos не должен быть пустым");


        Map<String, Object> firstTodo = todos.get(0);


        assertTrue(firstTodo.containsKey("userId"), "Должно содержать поле userId");
        assertTrue(firstTodo.containsKey("id"), "Должно содержать поле id");
        assertTrue(firstTodo.containsKey("title"), "Должно содержать поле title");
        assertTrue(firstTodo.containsKey("completed"), "Должно содержать поле completed");


        assertInstanceOf(Integer.class, firstTodo.get("userId"), "userId должен быть числом");
        assertInstanceOf(Integer.class, firstTodo.get("id"), "id должен быть числом");
        assertInstanceOf(String.class, firstTodo.get("title"), "title должен быть строкой");
        assertInstanceOf(Boolean.class, firstTodo.get("completed"), "completed должен быть boolean");

        System.out.println("✅ Структура JSON валидна");
        System.out.println("📊 Всего элементов: " + todos.size());
        System.out.println("📝 Первый todo: " + firstTodo.get("title"));
    }

    @Test
    void testSingleTodo() throws Exception {

        APIResponse response = requestContext.get("/todos/1");


        assertEquals(200, response.status());


        String responseBody = response.text();
        Map<String, Object> todo = objectMapper.readValue(responseBody, Map.class);


        assertEquals(1, todo.get("id"), "ID должен быть 1");
        assertEquals(1, todo.get("userId"), "User ID должен быть 1");
        assertNotNull(todo.get("title"), "Title не должен быть null");
        assertNotNull(todo.get("completed"), "Completed не должен быть null");

        System.out.println("✅ Одиночный todo проверен: " + todo.get("title"));
    }

    @Test
    void testPostsApi() throws Exception {

        APIResponse response = requestContext.get("/posts");


        assertEquals(200, response.status());


        String responseBody = response.text();
        List<Map<String, Object>> posts = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );


        assertFalse(posts.isEmpty());

        Map<String, Object> firstPost = posts.get(0);
        assertTrue(firstPost.containsKey("userId"));
        assertTrue(firstPost.containsKey("id"));
        assertTrue(firstPost.containsKey("title"));
        assertTrue(firstPost.containsKey("body"));

        System.out.println("✅ Posts API проверен, всего постов: " + posts.size());
    }

    @Test
    void testUsersApi() throws Exception {

        APIResponse response = requestContext.get("/users");

        assertEquals(200, response.status());

        String responseBody = response.text();
        List<Map<String, Object>> users = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );


        Map<String, Object> firstUser = users.get(0);


        assertTrue(firstUser.containsKey("id"));
        assertTrue(firstUser.containsKey("name"));
        assertTrue(firstUser.containsKey("username"));
        assertTrue(firstUser.containsKey("email"));


        assertTrue(firstUser.containsKey("address"));
        assertTrue(firstUser.containsKey("company"));

        Map<String, Object> address = (Map<String, Object>) firstUser.get("address");
        assertTrue(address.containsKey("street"));
        assertTrue(address.containsKey("city"));

        System.out.println("✅ Users API проверен, первый пользователь: " + firstUser.get("name"));
    }

    @Test
    void testErrorHandling() {

        APIResponse response = requestContext.get("/nonexistent");

        assertEquals(404, response.status());
        System.out.println("✅ Обработка 404 ошибки работает корректно");
    }

    @Test
    void testResponseHeaders() {

        APIResponse response = requestContext.get("/todos/1");

        assertEquals(200, response.status());
        assertTrue(response.headers().containsKey("content-type"));
        assertTrue(response.headers().get("content-type").contains("application/json"));

        System.out.println("✅ Заголовки ответа корректны: " + response.headers().get("content-type"));
    }

    @AfterEach
    void tearDown() {
        if (requestContext != null) {
            requestContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}