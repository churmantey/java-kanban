package dc.yandex.kanban.service.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        List<Task> history = manager.getPrioritizedTasks();
        String jsonData = gson.toJson(history);
        sendText(exchange, jsonData);
    }

    @Override
    public void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        sendNotFound(exchange, "HTTP-метод не поддерживается реализацией");
    }

    @Override
    public void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        sendNotFound(exchange, "HTTP-метод не поддерживается реализацией");
    }
}
