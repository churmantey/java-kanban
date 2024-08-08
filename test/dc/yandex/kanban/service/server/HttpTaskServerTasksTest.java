package dc.yandex.kanban.service.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
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

public class HttpTaskServerTasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson = BaseHttpHandler.getDefaultGson();

    String endPoint = HttpTaskServer.TASK_END_POINT;

    public HttpTaskServerTasksTest() {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
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
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTimeConflictingTask() throws IOException, InterruptedException {
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addTask(task);

        Task conflictingTask = manager.createNewTask("Test 3", "Testing task 3",
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(conflictingTask);

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
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testNonExistingTask() throws IOException, InterruptedException {
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
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addTask(task);

        task.setName("Test Updated 2");
        task.setDescription("Testing Updated 2");
        task.setStatus(TaskStatus.IN_PROGRESS);

        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Updated 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Testing Updated 2", tasksFromManager.get(0).getDescription(),
                "Некорректное описание задачи");
        assertEquals(TaskStatus.IN_PROGRESS, tasksFromManager.get(0).getStatus(),
                "Некорректный статус задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = manager.createNewTask("Test 3", "Testing task 3",
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));

        manager.addTask(task);
        manager.addTask(task2);

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
        assertTrue(jsonElement.isJsonArray(), "Сервер не вернул массив задач");
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertEquals(2, jsonArray.size(), "Некорректное количество задач");
        }
    }

    @Test
    public void testGetOneTask() throws IOException, InterruptedException {
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addTask(task);

        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + task.getId());
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

        assertTrue(jsonElement.isJsonObject(), "Сервер не вернул правильный объект задачи");
        assertEquals(taskJson, taskData, "Несовпадение данных существующей задачи");
    }

    @Test
    public void testDeleteOneTask() throws IOException, InterruptedException {
        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addTask(task);

        assertEquals(1, manager.getTasks().size(), "Неправильное количество задач в менеджере");

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080" + endPoint + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size(), "Задача не удалилась!");
    }

}
