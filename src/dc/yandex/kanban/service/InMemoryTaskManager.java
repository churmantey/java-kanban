package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> taskList; // Список всех обычных задач
    private final Map<Integer, Epic> epicList; // Список всех эпиков
    private final Map<Integer, SubTask> subTaskList; // Список всех подзадач
    private int taskCounter; // Счетчик для id задач,эпиков и подзадач

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
        taskCounter = 0;
        historyManager = Managers.getDefaultHistory();
    }

    // Получает список всех обычных задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    // Получает список всех эпиков
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    // Получает список всех подзадач
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskList.values());
    }

    // Получает список всех подзадач эпика по id эпика
    @Override
    public List<SubTask> getEpicSubTasksById(int epicId) {
        if (epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            return epic.getSubTasks();
        } else {
            System.out.println("Эпик с id " + epicId + " не существует");
            return null;
        }
    }

    // Получает задачу, эпик или подзадачу по переданному id
    @Override
    public Task getTaskById(int taskId) {
        Task foundTask = null;
        if (taskList.containsKey(taskId)) {
            foundTask = taskList.get(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            foundTask = subTaskList.get(taskId);
        } else if (epicList.containsKey(taskId)) {
            foundTask = epicList.get(taskId);
        } else {
            System.out.println("Задачи с id " + taskId + " не найдено");
        }
        if (foundTask != null) {
            historyManager.add(foundTask);
        }
        return foundTask;
    }

    // Удаляет задачу, эпик или подзадачу по переданному id
    @Override
    public void deleteTaskById(int taskId) {
        if (taskList.containsKey(taskId)) {
            historyManager.remove(taskId);
            taskList.remove(taskId).clearData();
        } else if (subTaskList.containsKey(taskId)) {
            // Получаем подзадачу, eё эпик, удаляем подзадачу в эпике, затем в списке.
            SubTask subTask = subTaskList.get(taskId);
            Epic epic = subTask.getParentTask();
            epic.deleteSubTask(subTask);
            historyManager.remove(taskId);
            subTaskList.remove(taskId).clearData();
        } else if (epicList.containsKey(taskId)) {
            // Получаем эпик, удаляем все подзадачи эпика в списке, затем удаляем подзадачи в эпике.
            Epic epic = epicList.get(taskId);
            ArrayList<SubTask> epicSubTasks = epic.getSubTasks();
            for (SubTask subTask : epicSubTasks) {
                historyManager.remove(subTask.getId());
                subTaskList.remove(subTask.getId()).clearData();
            }
            epic.deleteAllSubTasks();
            historyManager.remove(taskId);
            epicList.remove(taskId).clearData();
        } else {
            System.out.println("Попытка удаления несуществующей задачи с id " + taskId);
        }
    }

    // Удаляет все задачи
    @Override
    public void deleteAllTasks() {
        for (Task task : taskList.values()) {
            historyManager.remove(task.getId());
            task.clearData();
        }
        taskList.clear();
    }

    // Удаляет все подзадачи
    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubTasks();
        }
        for (SubTask subTask : subTaskList.values()) {
            historyManager.remove(subTask.getId());
            subTask.clearData();
        }
        subTaskList.clear();
    }

    // Удаляет все эпики (вместе с подзадачами)
    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        for (Epic epic : epicList.values()) {
            historyManager.remove(epic.getId());
            epic.clearData();
        }
        epicList.clear();
    }

    // Создает новый объект Task
    @Override
    public Task createNewTask(String name, String description) {
        taskCounter++;
        return new Task(taskCounter, name, description);
    }

    // Создает новый объект Epic
    @Override
    public Epic createNewEpic(String name, String description) {
        taskCounter++;
        return new Epic(taskCounter, name, description);
    }

    // Создает новый объект SubTask
    @Override
    public SubTask createNewSubtask(Epic epic, String name, String description) {
        taskCounter++;
        return new SubTask(epic, taskCounter, name, description);
    }

    // Добавляет задачу в список
    @Override
    public void addTask(Task task) {
        if (task != null && task.getClass().equals(Task.class)) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Попытка добавить null-задачу или задачу неподходящего типа.");
        }
    }

    // Добавляет эпик в список
    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Попытка добавить null в качестве эпика.");
        }
    }

    // Добавляет подзадачу в список
    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTaskList.put(subTask.getId(), subTask);
            subTask.getParentTask().addSubTask(subTask);
        } else {
            System.out.println("Попытка добавить подзадачу null");
        }
    }

    // Обновляет задачу
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Попытка добавить задачу null");
        }

    }

    // Обновляет эпик
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Попытка добавить эпик null");
        }
    }

    // Обновляет подзадачу
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            subTaskList.put(subTask.getId(), subTask);
            subTask.getParentTask().addSubTask(subTask);
        } else {
            System.out.println("Попытка добавить подзадачу null");
        }
    }

    // Возвращает список истории просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
