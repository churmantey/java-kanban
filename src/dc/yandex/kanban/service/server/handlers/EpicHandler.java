package dc.yandex.kanban.service.server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskType;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.exceptions.TaskNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {  // выводим список эпиков
            List<Epic> taskList = manager.getEpics();
            String jsonData = gson.toJson(taskList);
            sendText(exchange, jsonData);
        } else if (pathParts.length == 3) { // выводим один эпик по id
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = manager.getTaskById(taskId);
                if (!task.getType().equals(TaskType.EPIC)) {
                    sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден");
                    return;
                }
                String jsonData = gson.toJson(task);
                sendText(exchange, jsonData);
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден. Не удалось преобразовать id.");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден.");
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) { // выводим подзадачи эпика по id
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = manager.getTaskById(taskId);
                if (!task.getType().equals(TaskType.EPIC)) {
                    sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден");
                    return;
                }
                List<SubTask> taskList = manager.getEpicSubTasksById(taskId);
                String jsonData = gson.toJson(taskList);
                sendText(exchange, jsonData);
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден. Не удалось преобразовать id.");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден.");
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } else { // неизвестный путь запроса
            sendNotFound(exchange, "Такой функционал отсутствует");
        }
    }

    @Override
    public void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {  // создаем новый эпик
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
                if (jsonObject.get("name") == null || jsonObject.get("description") == null) {
                    sendNotFound(exchange, "Передана некорректная структура");
                    return;
                }
                Epic task = manager.createNewEpic(
                        jsonObject.get("name").getAsString(),
                        jsonObject.get("description").getAsString());
                try {
                    manager.addEpic(task);
                    sendNoTextOk(exchange);
                } catch (Exception e) {
                    sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
                }
            } else {
                sendNotFound(exchange, "Передана некорректная структура эпика");
            }
        } else { // неизвестный путь запроса
            sendNotFound(exchange, "Такой функционал отсутствует");
        }
    }

    @Override
    public void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) { // удаляем один эпик по id
            String taskIdStr = pathParts[2];
            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = manager.getTaskById(taskId);
                if (!task.getType().equals(TaskType.EPIC)) {
                    sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найден.");
                    return;
                }
                manager.deleteTaskById(task.getId());
                sendText(exchange, "");
            } catch (NumberFormatException e) { // в строке не числовой id
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найдена. Не удалось преобразовать id.");
            } catch (TaskNotFoundException e) { // задачу по id не нашли
                sendNotFound(exchange, "Эпик с id " + taskIdStr + " не найдена");
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } else {
            sendNotFound(exchange, "Некорректный путь запроса");
        }
    }
}
