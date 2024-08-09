package dc.yandex.kanban.service.server;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.service.Managers;
import dc.yandex.kanban.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerRun {

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
