package dc.yandex.kanban;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import dc.yandex.kanban.service.FileBackedTaskManager;
import dc.yandex.kanban.service.Managers;
import dc.yandex.kanban.service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("#############################");
        System.out.println("###    InMemoryManager    ###");
        System.out.println("#############################");
        inMemoryMain(args);

        System.out.println("#############################");
        System.out.println("###   FileBackedManager   ###");
        System.out.println("#############################");
        fileBackedMain(args);
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

    public static void inMemoryMain(String[] args) {
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

    public static void fileBackedMain(String[] args) {
        String testFilename = "manager_save.txt";
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFilename);

        Task task1 = taskManager.createNewTask("Почитать", "Прочитать главу из книги Дж. Оруэлла '1984'",
                LocalDateTime.of(2024, 7, 24, 11, 55),
                Duration.ofMinutes(30));
        Task task2 = taskManager.createNewTask("Позаниматься музыкой",
                "Выучить 'Cullen Bay'",
                LocalDateTime.of(2024, 7, 24, 11, 30),
                Duration.ofMinutes(30));
        taskManager.addTask(task1);
        try {
            taskManager.addTask(task2);
        } catch (RuntimeException e) {
            System.out.println("Поймано исключение:\n" + e.getMessage());
        }

        System.out.println(" ======== BY PRIORITY ========= ");
        System.out.println(taskManager.getPrioritizedTasks());

        Epic epic1 = taskManager.createNewEpic("Двухэтажный дом", "Нужен двухэтажный кирпичный");
        SubTask subTask1e1 = taskManager.createNewSubtask(epic1, "Основание", "Заложить фундамент",
                LocalDateTime.of(2024, 7, 24, 10, 30),
                Duration.ofMinutes(50));
        SubTask subTask2e1 = taskManager.createNewSubtask(epic1, "Стены", "Возвести стены",
                LocalDateTime.of(2024, 7, 30, 14, 23),
                Duration.ofMinutes(50));
        SubTask subTask3e1 = taskManager.createNewSubtask(epic1, "Кровля", "Уложить кровлю",
                LocalDateTime.of(2024, 7, 20, 10, 30),
                Duration.ofMinutes(40));
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1e1);
        taskManager.addSubTask(subTask2e1);
        taskManager.addSubTask(subTask3e1);
        subTask2e1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2e1);

        System.out.println(" ======== BY PRIORITY ========= ");
        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println(" ======== EPIC 1 times  ========= ");
        System.out.println(" start = " + epic1.getStartTime());
        System.out.println(" end = " + epic1.getEndTime());

        Epic epic2 = taskManager.createNewEpic("Запастить продуктами", "В доме нужна еда");
        taskManager.addEpic(epic2);

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(new File(testFilename));

        System.out.println("Задачи основного менеджера:");
        System.out.println(taskManager.getTasks());
        System.out.println("Задачи восстановленного менеджера:");
        System.out.println(taskManager2.getTasks());

        System.out.println("Эпики основного менеджера:");
        System.out.println(taskManager.getEpics());
        System.out.println("Эпики восстановленного менеджера:");
        System.out.println(taskManager2.getEpics());

        System.out.println("Подзадачи основного менеджера:");
        System.out.println(taskManager.getSubTasks());
        System.out.println("Подзадачи восстановленного менеджера:");
        System.out.println(taskManager2.getSubTasks());
    }

}
