package dc.yandex.kanban;

import dc.yandex.kanban.model.*;
import dc.yandex.kanban.service.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createNewTask("Почитать",
                "Прочитать главу из книги Дж. Оруэлла '1984'");
        taskManager.addTask(task1);
        Task task2 = taskManager.createNewTask("Позаниматься музыкой",
                "Выучить 'Cullen Bay'");
        taskManager.addTask(task2);

        Epic epic1 = taskManager.createNewEpic("Двухэтажный дом", "Нужен двухэтажный кирпичный");
        taskManager.addEpic(epic1);
        SubTask subTask1e1 = taskManager.createNewSubtask(epic1, "Основание", "Заложить фундамент");
        taskManager.addSubTask(subTask1e1);
        SubTask subTask2e1 = taskManager.createNewSubtask(epic1, "Стены", "Возвести стены");
        taskManager.addSubTask(subTask2e1);

        Epic epic2 = taskManager.createNewEpic("Запастить продуктами", "В доме нужна еда");
        taskManager.addEpic(epic2);
        SubTask subTask1e2 = taskManager.createNewSubtask(epic2, "Купить сыр", "Упаковку моцареллы и кусок чеддера");
        taskManager.addSubTask(subTask1e2);
        printAllTasks(taskManager, " Созданы новые задачи, эпики и подзадачи");

        System.out.println("=== Получение списка подзадач эпика 2 по id:");
        System.out.println(taskManager.getEpicSubTasksById(epic2.getId()));

        System.out.println("=== Получение задачи по идентификатору :");
        System.out.println(taskManager.getTaskById(epic1.getId()));

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        subTask1e1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1e1);
        subTask1e2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1e2);
        epic2.setName("Запастись продуктами к празднику");
        epic2.setDescription("Будет много гостей, в доме нужна еда.");
        taskManager.updateEpic(epic2);
        printAllTasks(taskManager, "Изменены статусы задачи и подзадач эпиков, имя и описание эпика 2");

        taskManager.deleteTaskById(subTask1e2.getId());
        printAllTasks(taskManager, "Удалена единственная подзадача эпика 2");

        taskManager.deleteTaskById(epic1.getId());
        printAllTasks(taskManager, "Удален эпик 1");

        taskManager.deleteAllTasks();
        printAllTasks(taskManager, "Удалены все задачи");
        taskManager.deleteAllSubTasks();
        printAllTasks(taskManager, "Удалены все подзадачи");
        taskManager.deleteAllEpics();
        printAllTasks(taskManager, "Удалены все эпики");

    }

    public static void printAllTasks (TaskManager taskManager, String header) {
        System.out.println("=== " + header + " ==============");
        System.out.println("Задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи:");
        System.out.println(taskManager.getSubTasks());
    }

}
