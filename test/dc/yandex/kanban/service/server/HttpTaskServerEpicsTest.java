package dc.yandex.kanban.service.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.service.InMemoryTaskManager;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.server.handlers.BaseHttpHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerEpicsTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson = BaseHttpHandler.getDefaultGson();

    String endPoint = HttpTaskServer.EPIC_END_POINT;

    public HttpTaskServerEpicsTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = manager.createNewEpic("Test Epic 1", "Testing Epic 1");

        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test Epic 1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testNonExistingEpic() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        Epic epic2 = manager.createNewEpic("Test Epic 2", "Testing Epic 2");

        manager.addEpic(epic);
        manager.addEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        String taskData = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(taskData);
        assertTrue(jsonElement.isJsonArray(), "Сервер не вернул массив эпиков");
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertEquals(2, jsonArray.size(), "Некорректное количество эпиков");
        }
    }

    @Test
    public void testGetEpicSubTasks() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task2 = manager.createNewSubtask(epic, "Test 3", "Testing task 3",
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));

        manager.addEpic(epic);
        manager.addSubTask(task);
        manager.addSubTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        String taskData = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(taskData);
        assertTrue(jsonElement.isJsonArray(), "Сервер не вернул массив подзадач");
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertEquals(2, jsonArray.size(), "Некорректное количество подзадач");
        }
    }

    @Test
    public void testGetOneEpic() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");

        manager.addEpic(epic);

        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        String taskData = new String(response.body().getBytes(StandardCharsets.UTF_8));
        JsonElement jsonElement = JsonParser.parseString(taskData);

        assertTrue(jsonElement.isJsonObject(), "Сервер не вернул правильный объект эпика");
        assertEquals(taskJson, taskData, "Несовпадение данных существующего эпика");
    }

    @Test
    public void testDeleteOneEpic() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
        SubTask task2 = manager.createNewSubtask(epic, "Test 3", "Testing task 3",
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));

        manager.addEpic(epic);
        manager.addSubTask(task);
        manager.addSubTask(task2);

        assertEquals(1, manager.getEpics().size(), "Неправильное количество эпиков в менеджере");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size(), "Эпик не удалился!");
        assertEquals(0, manager.getSubTasks().size(), "Подзадачи эпика не удалились!");
    }

}
