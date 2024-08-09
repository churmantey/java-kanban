package dc.yandex.kanban.service.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.server.adapters.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getDefaultGson();
    }

    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        System.out.println(method + " " + path);
        String[] parts = path.split("/");
        switch (method) {
            case "GET":
                handleGet(exchange, parts);
                break;
            case "POST":
                handlePost(exchange, parts);
                break;
            case "DELETE":
                handleDelete(exchange, parts);
                break;
            default:
                // нереализованный метод
                sendNotFound(exchange, "HTTP-метод не поддерживается реализацией");
        }
    }

    public abstract void handleGet(HttpExchange exchange, String[] pathParts) throws IOException;

    public abstract void handlePost(HttpExchange exchange, String[] pathParts) throws IOException;

    public abstract void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException;

    protected void sendText(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке ответа:\n" + e.getMessage());
        }
    }

    protected void sendNoTextOk(HttpExchange h) {
        try {
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(201, 0);
            h.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке ответа:\n" + e.getMessage());
        }
    }

    protected void sendNotFound(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            h.sendResponseHeaders(404, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке ответа:\n" + e.getMessage());
        }
    }

    protected void sendHasInteraction(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            h.sendResponseHeaders(406, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке ответа:\n" + e.getMessage());
        }
    }

    protected void sendServerError(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            h.sendResponseHeaders(500, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке ответа:\n" + e.getMessage());
        }
    }

}
