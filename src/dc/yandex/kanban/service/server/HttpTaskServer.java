package dc.yandex.kanban.service.server;

import com.sun.net.httpserver.HttpServer;
import dc.yandex.kanban.service.TaskManager;
import dc.yandex.kanban.service.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

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
}
