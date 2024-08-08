package dc.yandex.kanban.service.server;

import com.sun.net.httpserver.HttpServer;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.service.Managers;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public static final String TASK_END_POINT = "/tasks";
    public static final String SUBTASK_END_POINT = "/subtasks";
    public static final String EPIC_END_POINT = "/epics";
    public static final String HISTORY_END_POINT = "/history";
    public static final String PRIORITIZED_END_POINT = "/prioritized";
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {

        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.httpServer.createContext(TASK_END_POINT, new TaskHandler(manager));
        this.httpServer.createContext(SUBTASK_END_POINT, new SubTaskHandler(manager));
        this.httpServer.createContext(EPIC_END_POINT, new EpicHandler(manager));
        this.httpServer.createContext(HISTORY_END_POINT, new HistoryHandler(manager));
        this.httpServer.createContext(PRIORITIZED_END_POINT, new PrioritizedHandler(manager));
    }

    public void start() {
        this.httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        this.httpServer.stop(0);
        System.out.println("HTTP-сервер останавливается!");
    }

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();

        Task task = taskManager.createNewTask("задача 1", "Описание 1",
                LocalDateTime.of(2024, 8, 4, 11, 30),
                Duration.ofMinutes(50));
        taskManager.addTask(task);
        Epic epic = taskManager.createNewEpic("Эпик 2", "Описание Эпика 2");
        taskManager.addEpic(epic);
        SubTask subTask = taskManager.createNewSubtask(epic, "Подзадача 3", "Описание подзадачи 3",
                LocalDateTime.of(2024, 8, 4, 15, 5),
                Duration.ofMinutes(50));
        taskManager.addSubTask(subTask);
        SubTask subTask2 = taskManager.createNewSubtask(epic, "Подзадача 4", "Описание подзадачи 4",
                LocalDateTime.of(2024, 8, 4, 16, 5),
                Duration.ofMinutes(50));
        taskManager.addSubTask(subTask2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}