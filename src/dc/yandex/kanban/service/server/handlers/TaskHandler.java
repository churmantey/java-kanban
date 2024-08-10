package dc.yandex.kanban.service.server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import dc.yandex.kanban.model.TaskType;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.exceptions.TaskNotFoundException;
import dc.yandex.kanban.service.exceptions.TaskTimeInterferenceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    // Обрабатывает GET-запросы
    @Override
    public void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {  // выводим список задач
            List<Task> taskList = manager.getTasks();
            String jsonData = gson.toJson(taskList);
            sendText(exchange, jsonData);
        } else if (pathParts.length == 3) { // выводим одну задачу по id
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = manager.getTaskById(taskId);
                if (!task.getType().equals(TaskType.TASK)) {
                    sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
                    return;
                }
                String jsonData = gson.toJson(task);
                sendText(exchange, jsonData);
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена. Не удалось преобразовать id.");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
            }
        } else { // неизвестный путь запроса
            sendNotFound(exchange, "Такой функционал отсутствует");
        }
    }

    // Обрабатывает POST-запросы
    @Override
    public void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {  // создаем новую задачу
            String taskData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement;
            try {
                jsonElement = JsonParser.parseString(taskData);
            } catch (Exception e) {
                sendNotFound(exchange, "Передана некорректная структура");
                return;
            }
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.get("name") == null
                        || jsonObject.get("description") == null
                        || jsonObject.get("startTime") == null
                        || jsonObject.get("duration") == null) {
                    sendNotFound(exchange, "Передана некорректная структура");
                    return;
                }

                Task task = manager.createNewTask(
                        jsonObject.get("name").getAsString(),
                        jsonObject.get("description").getAsString(),
                        gson.fromJson(jsonObject.get("startTime"), LocalDateTime.class),
                        gson.fromJson(jsonObject.get("duration"), Duration.class));
                try {
                    manager.addTask(task);
                    sendNoTextOk(exchange);
                } catch (TaskTimeInterferenceException e) {
                    sendHasInteraction(exchange, "Не удалось создать задачу\n" + e.getMessage());
                } catch (Exception e) {
                    sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
                }
            } else {
                sendNotFound(exchange, "Передана некорректная структура задачи");
            }
        } else if (pathParts.length == 3) { // обновляем существующую задачу
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task existingTask = manager.getTaskById(taskId);
                if (!existingTask.getType().equals(TaskType.TASK)) {
                    sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена.\n");
                    return;
                }
                String taskData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(taskData);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Task newTask = new Task(existingTask.getId(),
                            jsonObject.get("name").getAsString(),
                            jsonObject.get("description").getAsString(),
                            gson.fromJson(jsonObject.get("startTime"), LocalDateTime.class),
                            gson.fromJson(jsonObject.get("duration"), Duration.class));
                    newTask.setStatus(TaskStatus.valueOf(jsonObject.get("status").getAsString()));
                    manager.updateTask(newTask);
                    sendNoTextOk(exchange);
                } else {
                    sendNotFound(exchange, "Передана некорректная структура задачи");
                }
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена.\n");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
            } catch (TaskTimeInterferenceException e) { // пересечение по времени
                sendHasInteraction(exchange, "Не удалось создать задачу\n" + e.getMessage());
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } else { // неизвестный путь запроса
            sendNotFound(exchange, "Такой функционал отсутствует");
        }
    }

    // Обрабатывает DELETE-запросы
    @Override
    public void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) { // удаляем одну задачу по id
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = manager.getTaskById(taskId);
                if (!task.getType().equals(TaskType.TASK)) {
                    sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
                    return;
                }
                manager.deleteTaskById(task.getId());
                sendText(exchange, "");
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена. Не удалось преобразовать id.");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } else {
            sendNotFound(exchange, "Некорректный путь запроса");
        }
    }
}
