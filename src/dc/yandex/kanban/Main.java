package dc.yandex.kanban;

import dc.yandex.kanban.model.*;
import dc.yandex.kanban.service.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createNewTask("Почитать",
                "Прочитать главу из книги Дж. Оруэлла '1984'");
        Task task2 = taskManager.createNewTask("Позаниматься музыкой",
                "Выучить 'Cullen Bay'");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        printAllTasks(taskManager, "Добавлены 2 задачи");

        Epic epic1 = taskManager.createNewEpic("Двухэтажный дом", "Нужен двухэтажный кирпичный");
        SubTask subTask1e1 = taskManager.createNewSubtask(epic1, "Основание", "Заложить фундамент");
        SubTask subTask2e1 = taskManager.createNewSubtask(epic1, "Стены", "Возвести стены");
        SubTask subTask3e1 = taskManager.createNewSubtask(epic1, "Кровля", "Уложить кровлю");
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1e1);
        taskManager.addSubTask(subTask2e1);
        taskManager.addSubTask(subTask3e1);
        printAllTasks(taskManager, "Добавлен эпик с 3-мя подзадачами");

        Epic epic2 = taskManager.createNewEpic("Запастить продуктами", "В доме нужна еда");
        taskManager.addEpic(epic2);
        printAllTasks(taskManager, "Добавлен эпик без подзадач");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        printHistory(taskManager, "История после 3 уникальных попаданий");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(subTask2e1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(subTask3e1.getId());
        printHistory(taskManager, "История после еще 6 дублей и 3 уникальных запросов");

        taskManager.deleteTaskById(subTask3e1.getId());
        printAllTasks(taskManager, "Удалена одна подзадача эпика");
        printHistory(taskManager, "История после удаления одной подзадачи");

        taskManager.deleteTaskById(epic1.getId());
        printAllTasks(taskManager, "Удален эпик 1");
        printHistory(taskManager, "История после удаления эпика с подзадачами");

        taskManager.deleteAllTasks();
        printAllTasks(taskManager, "Удалены все задачи");
        taskManager.deleteAllSubTasks();
        printAllTasks(taskManager, "Удалены все подзадачи");
        taskManager.deleteAllEpics();
        printAllTasks(taskManager, "Удалены все эпики");
        printHistory(taskManager, "История после удаления всех эпиков/задач/подзадач");

    }

    public static void printAllTasks(TaskManager taskManager, String header) {
        System.out.println("==== " + header + " ====");
        System.out.println("Задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи:");
        System.out.println(taskManager.getSubTasks());
    }

    public static void printHistory(TaskManager taskManager, String header) {
        List<Task> history = taskManager.getHistory();
        System.out.println(".... " + header + " ....");
        System.out.println("Размер: " + history.size());
        System.out.println("История:\n" + history);
    }

}
