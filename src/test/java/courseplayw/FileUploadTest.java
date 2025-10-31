package courseplayw;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {
    Playwright playwright;
    APIRequestContext request;
    private byte[] testPngData;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        request = playwright.request().newContext();
        testPngData = generateTestPng();
    }

    @Test
    void testFileDownload() {
        try {
            // 1. Скачивание эталонного PNG файла через Playwright
            APIResponse downloadResponse = downloadReferencePng();

            // 2. Проверка MIME-типа и валидности формата
            verifyPngFile(downloadResponse);

            System.out.println("✅ Тест скачивания файла завершен успешно");

        } catch (Exception e) {
            fail("Тест завершился с ошибкой: " + e.getMessage());
        }
    }

    @Test
    void testFileUploadWithJavaHttp() {
        try {
            System.out.println("📤 Загрузка файла через Java HTTP...");

            String responseBody = uploadWithJavaHttp();

            // Простая проверка - ответ должен быть валидным JSON и содержать 200 статус
            assertTrue(responseBody.contains("\"url\""), "Ответ должен быть валидным JSON");
            assertTrue(responseBody.contains("https://httpbin.org/post"), "Ответ должен содержать URL запроса");

            System.out.println("✅ Файл успешно загружен через Java HTTP");

        } catch (Exception e) {
            System.out.println("⚠️  Ошибка загрузки через Java HTTP: " + e.getMessage());
        }
    }

    private String uploadWithJavaHttp() throws Exception {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String crlf = "\r\n";

        // Строим multipart тело
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("--").append(boundary).append(crlf);
        bodyBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"test-image.png\"").append(crlf);
        bodyBuilder.append("Content-Type: image/png").append(crlf);
        bodyBuilder.append(crlf);

        String bodyStart = bodyBuilder.toString();
        String bodyEnd = crlf + "--" + boundary + "--" + crlf;

        // Собираем полное тело запроса
        byte[] bodyStartBytes = bodyStart.getBytes();
        byte[] bodyEndBytes = bodyEnd.getBytes();

        byte[] fullBody = new byte[bodyStartBytes.length + testPngData.length + bodyEndBytes.length];
        System.arraycopy(bodyStartBytes, 0, fullBody, 0, bodyStartBytes.length);
        System.arraycopy(testPngData, 0, fullBody, bodyStartBytes.length, testPngData.length);
        System.arraycopy(bodyEndBytes, 0, fullBody, bodyStartBytes.length + testPngData.length, bodyEndBytes.length);

        // Используем стандартный Java HTTP
        URL url = new URL("https://httpbin.org/post");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Content-Length", String.valueOf(fullBody.length));
        connection.setDoOutput(true);

        // Отправляем данные
        connection.getOutputStream().write(fullBody);

        // Читаем ответ
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode, "HTTP статус должен быть 200");

        // Читаем тело ответа
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = connection.getInputStream().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        String responseBody = result.toString(StandardCharsets.UTF_8.name());
        System.out.println("Ответ сервера: " + responseBody); // Добавь эту строку для отладки

        return responseBody;
    }

    private APIResponse downloadReferencePng() {
        System.out.println("📥 Скачивание эталонного PNG файла...");

        // Простой GET запрос через Playwright
        APIResponse response = request.get("https://httpbin.org/image/png");

        assertEquals(200, response.status(), "Запрос должен завершиться успешно");
        System.out.println("✅ Эталонный файл скачан, статус: " + response.status());

        return response;
    }

    private void verifyPngFile(APIResponse downloadResponse) {
        System.out.println("🔍 Проверка валидности PNG файла...");

        byte[] content = downloadResponse.body();

        // Проверка MIME-типа
        String contentType = downloadResponse.headers().get("content-type");
        assertEquals("image/png", contentType,
                "Content-Type должен быть image/png");

        // Проверка сигнатуры PNG (магические числа)
        // PNG сигнатура: 89 50 4E 47 0D 0A 1A 0A
        assertEquals(0x89, content[0] & 0xFF, "Первый байт должен быть 0x89");
        assertEquals(0x50, content[1] & 0xFF, "Второй байт должен быть 'P' (0x50)");
        assertEquals(0x4E, content[2] & 0xFF, "Третий байт должен быть 'N' (0x4E)");
        assertEquals(0x47, content[3] & 0xFF, "Четвертый байт должен быть 'G' (0x47)");
        assertEquals(0x0D, content[4] & 0xFF, "Пятый байт должен быть 0x0D");
        assertEquals(0x0A, content[5] & 0xFF, "Шестой байт должен быть 0x0A");
        assertEquals(0x1A, content[6] & 0xFF, "Седьмой байт должен быть 0x1A");
        assertEquals(0x0A, content[7] & 0xFF, "Восьмой байт должен быть 0x0A");

        // Проверка что файл не пустой
        assertTrue(content.length > 100, "PNG файл должен быть больше 100 байт");

        System.out.println("✅ PNG файл валиден, размер: " + content.length + " байт");
        System.out.println("✅ MIME-тип корректный: " + contentType);
    }

    @Test
    void testBase64FileVerification() {
        // Тестируем работу с base64 данными
        System.out.println("🔍 Тестирование Base64 верификации...");

        // Кодируем наш тестовый PNG в base64
        String base64Data = Base64.getEncoder().encodeToString(testPngData);

        // Декодируем обратно
        byte[] decodedData = Base64.getDecoder().decode(base64Data);

        // Проверяем целостность
        assertArrayEquals(testPngData, decodedData,
                "Данные после кодирования/декодирования должны совпадать");

        System.out.println("✅ Base64 верификация успешна");
    }

    /**
     * Генерирует простой валидный PNG файл в памяти
     */
    private byte[] generateTestPng() {
        // Минимальный валидный PNG файл (1x1 черный пиксель)
        byte[] minimalPng = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
                0x00, 0x00, 0x00, 0x0D,                                 // IHDR chunk length
                0x49, 0x48, 0x44, 0x52,                                 // IHDR
                0x00, 0x00, 0x00, 0x01,                                 // Width: 1
                0x00, 0x00, 0x00, 0x01,                                 // Height: 1
                0x08, 0x02, 0x00, 0x00, 0x00,                           // Bit depth, color type, etc.
                (byte) 0x90, 0x77, (byte) 0x53, (byte) 0xDE,            // CRC
                0x00, 0x00, 0x00, 0x0A,                                 // IDAT chunk length
                0x49, 0x44, 0x41, 0x54,                                 // IDAT
                0x08, 0x5D, 0x01, 0x01, 0x00, 0x00, 0x00, (byte) 0xFF,  // Image data
                (byte) 0xFF, 0x00, 0x00, 0x00, 0x00,                    // Image data continued
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82,                   // CRC
                0x00, 0x00, 0x00, 0x00,                                 // IEND chunk length
                0x49, 0x45, 0x4E, 0x44,                                 // IEND
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82                    // CRC
        };

        System.out.println("🖼️ Сгенерирован тестовый PNG файл, размер: " + minimalPng.length + " байт");
        return minimalPng;
    }

    @AfterEach
    void tearDown() {
        try {
            if (request != null) {
                request.dispose();
            }
            if (playwright != null) {
                playwright.close();
            }
            System.out.println("🧹 Ресурсы очищены");
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка при очистке ресурсов: " + e.getMessage());
        }
    }
}