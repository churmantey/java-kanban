package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;

import java.util.List;

public interface TaskManager {

    // Получает список всех обычных задач
    List<Task> getTasks();

    // Получает список всех эпиков
    List<Epic> getEpics();

    // Получает список всех подзадач
    List<SubTask> getSubTasks();

    // Список задач/подзадач, отсортированный по времени начала
    List<Task> getPrioritizedTasks();

    // Получает список всех подзадач эпика по id эпика
    List<SubTask> getEpicSubTasksById(int epicId);

    // Получает задачу, эпик или подзадачу по переданному id
    Task getTaskById(int taskId);

    // Удаляет задачу, эпик или подзадачу по переданному id
    void deleteTaskById(int taskId);

    // Удаляет все задачи
    void deleteAllTasks();

    // Удаляет все подзадачи
    void deleteAllSubTasks();

    // Удаляет все эпики (вместе с подзадачами)
    void deleteAllEpics();

    // Создает новый объект Task
    Task createNewTask(String name, String description);

    // Создает новый объект Epic
    Epic createNewEpic(String name, String description);

    // Создает новый объект SubTask
    SubTask createNewSubtask(Epic epic, String name, String description);

    // Добавляет задачу в список
    void addTask(Task task);

    // Добавляет эпик в список
    void addEpic(Epic epic);

    // Добавляет подзадачу в список
    void addSubTask(SubTask subTask);

    // Обновляет задачу
    void updateTask(Task task);

    // Обновляет эпик
    void updateEpic(Epic epic);

    // Обновляет подзадачу
    void updateSubTask(SubTask subTask);

    List<Task> getHistory();

}
