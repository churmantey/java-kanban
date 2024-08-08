package dc.yandex.kanban.service.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
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

public class HttpTaskServerSubTasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson = BaseHttpHandler.getDefaultGson();

    String endPoint = HttpTaskServer.SUBTASK_END_POINT;

    public HttpTaskServerSubTasksTest() {
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
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addEpic(epic);

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
        List<SubTask> tasksFromManager = manager.getSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addEpic(epic);
        manager.addSubTask(task);

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

        List<SubTask> tasksFromManager = manager.getSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test Updated 2", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
        assertEquals("Testing Updated 2", tasksFromManager.get(0).getDescription(),
                "Некорректное описание подзадачи");
        assertEquals(TaskStatus.IN_PROGRESS, tasksFromManager.get(0).getStatus(),
                "Некорректный статус подзадачи");
    }

    @Test
    public void testTimeConflictingSubTask() throws IOException, InterruptedException {
        Task task1 = manager.createNewTask("Test 1", "Testing task 1",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addTask(task1);

        // создаём задачу
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addEpic(epic);

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
        assertEquals(406, response.statusCode());

        List<SubTask> tasksFromManager = manager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testNonExistingSubTask() throws IOException, InterruptedException {
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
    public void testGetSubTasks() throws IOException, InterruptedException {
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
        assertTrue(jsonElement.isJsonArray(), "Сервер не вернул массив подзадач");
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertEquals(2, jsonArray.size(), "Некорректное количество подзадач");
        }
    }

    @Test
    public void testGetOneSubTask() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addEpic(epic);
        manager.addSubTask(task);

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

        assertTrue(jsonElement.isJsonObject(), "Сервер не вернул правильный объект подзадачи");
        assertEquals(taskJson, taskData, "Несовпадение данных существующей подзадачи");
    }

    @Test
    public void testDeleteOneSubTask() throws IOException, InterruptedException {
        Epic epic = manager.createNewEpic("Test Epic 1", "Testing Epic 1");
        SubTask task = manager.createNewSubtask(epic, "Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));

        manager.addEpic(epic);
        manager.addSubTask(task);

        assertEquals(1, manager.getSubTasks().size(), "Неправильное количество подзадач в менеджере");

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
        assertEquals(0, manager.getSubTasks().size(), "Подзадача не удалилась!");
    }

}
