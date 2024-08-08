package dc.yandex.kanban.service.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HttpTaskServerPrioritizedTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson = BaseHttpHandler.getDefaultGson();

    String endPoint = HttpTaskServer.PRIORITIZED_END_POINT;

    public HttpTaskServerPrioritizedTest() {
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
    public void testGetEmptyProirity() throws IOException, InterruptedException {
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
            assertEquals(0, jsonArray.size(), "Некорректное количество задач");
        }
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {

        Task task = manager.createNewTask("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = manager.createNewTask("Test 3", "Testing task 3",
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));
        Epic epic = manager.createNewEpic("Epic 4", "Testing epic 4");
        SubTask subTask = manager.createNewSubtask(epic, "Test sub 5", "Testing subtask 5",
                LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(5));
        SubTask subTask2 = manager.createNewSubtask(epic, "Test sub 6", "Testing subtask 6",
                LocalDateTime.now().minusMinutes(60), Duration.ofMinutes(5));

        manager.addTask(task);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);

        List<Task> tasksFromManager = manager.getPrioritizedTasks();

        String jsonPrioritized = gson.toJson(tasksFromManager);

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

        assertEquals(jsonPrioritized, taskData, "Несовпадение исходного и полученного json");

        JsonElement jsonElement = JsonParser.parseString(taskData);
        assertTrue(jsonElement.isJsonArray(), "Сервер не вернул массив задач");
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertEquals(4, jsonArray.size(), "Некорректное количество задач в списке по приоритету");
        }
    }
}
