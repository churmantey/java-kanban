package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Epic;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private final HashMap<Integer, Task> taskList; // Список всех обычных задач
    private final HashMap<Integer, Epic> epicList; // Список всех эпиков
    private final HashMap<Integer, SubTask> subTaskList; // Список всех подзадач

    private int taskCounter; // Счетчик для id задач,эпиков и подзадач

    public TaskManager () {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
        taskCounter = 0;
    }

    // Получает список всех обычных задач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    // Получает список всех эпиков
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    // Получает список всех подзадач
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskList.values());
    }

    // Получает список всех подзадач эпика по id эпика
    public ArrayList<SubTask> getEpicSubTasksById(int epicId) {
        if (epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            return epic.getSubTasks();
        } else {
            System.out.println("Эпик с id " + epicId + " не существует");
            return null;
        }
    }

    // Получает задачу, эпик или подзадачу по переданному id
    public Task getTaskById (int taskId) {
        if (taskList.containsKey(taskId)) {
            return taskList.get(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            return subTaskList.get(taskId);
        } else if (epicList.containsKey(taskId)) {
            return epicList.get(taskId);
        } else {
            System.out.println("Задачи с id " + taskId + " не найдено");
        }
        return null;
    }

    // Удаляет задачу, эпик или подзадачу по переданному id
    public void deleteTaskById (int taskId) {
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            // Получаем подзадачу, eё эпик, удаляем подзадачу в эпике, затем в списке.
            SubTask subTask = subTaskList.get(taskId);
            Epic epic = subTask.getParentTask();
            epic.deleteSubTask(subTask);
            subTaskList.remove(taskId);
        } else if (epicList.containsKey(taskId)) {
            // Получаем эпик, удаляем все подзадачи эпика в списке, затем удаляем подзадачи в эпике.
            Epic epic = epicList.get(taskId);
            ArrayList<SubTask> epicSubTasks = epic.getSubTasks();
            for (SubTask subTask : epicSubTasks) {
                subTaskList.remove(subTask.getId());
            }
            epic.deleteAllSubTasks();
            epicList.remove(taskId);
        } else {
            System.out.println("Попытка удаления несуществующей задачи с id " + taskId);
        }
    }

    // Удаляет все задачи
    public void deleteAllTasks() {
        taskList.clear();
    }

    // Удаляет все подзадачи
    public void deleteAllSubTasks() {
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubTasks();
        }
        subTaskList.clear();
    }

    // Удаляет все эпики (вместе с подзадачами)
    public void deleteAllEpics() {
        epicList.clear();
        subTaskList.clear();
    }

    // Создает новый объект Task
    public Task createNewTask(String name, String description) {
        taskCounter++;
        return new Task(taskCounter, name, description);
    }

    // Создает новый объект Epic
    public Epic createNewEpic(String name, String description) {
        taskCounter++;
        return new Epic(taskCounter, name, description);
    }

    // Создает новый объект SubTask
    public SubTask createNewSubtask(Epic epic, String name, String description) {
        taskCounter++;
        return new SubTask(epic, taskCounter, name, description);
    }

    // Добавляет задачу в список
    public void addTask(Task task) {
        if (task != null) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Попытка добавить null в качестве задачи.");
        }
    }

    // Добавляет эпик в список
    public void addEpic(Epic epic) {
        if (epic != null) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Попытка добавить null в качестве эпика.");
        }
    }

    // Добавляет подзадачу в список
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTaskList.put(subTask.getId(), subTask);
            subTask.getParentTask().addSubTask(subTask);
        } else {
            System.out.println("Попытка добавить подзадачу null");
        }
    }

    // Обновляет задачу
    public void updateTask(Task task) {
        if (task != null) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Попытка добавить задачу null");
        }

    }

    // Обновляет эпик
    public void updateEpic(Epic epic) {
        if (epic != null) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Попытка добавить эпик null");
        }
    }

    // Обновляет подзадачу
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            subTaskList.put(subTask.getId(), subTask);
        } else {
            System.out.println("Попытка добавить подзадачу null");
        }
    }
}
