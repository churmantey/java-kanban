package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Epic;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskList; // Список всех обычных задач
    private final HashMap<Integer, Epic> epicList; // Список всех эпиков
    private final HashMap<Integer, SubTask> subTaskList; // Список всех подзадач
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
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    // Получает список всех эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    // Получает список всех подзадач
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskList.values());
    }

    // Получает список всех подзадач эпика по id эпика
    @Override
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
    @Override
    public Task getTaskById(int taskId) {
        Task foundTask = null;
        if (taskList.containsKey(taskId)) {
            foundTask = taskList.get(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            foundTask =  subTaskList.get(taskId);
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
    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    // Удаляет все подзадачи
    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubTasks();
        }
        subTaskList.clear();
    }

    // Удаляет все эпики (вместе с подзадачами)
    @Override
    public void deleteAllEpics() {
        epicList.clear();
        subTaskList.clear();
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
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

}
