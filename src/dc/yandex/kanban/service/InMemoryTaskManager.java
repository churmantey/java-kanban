package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStartTimeComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> taskList; // Список всех обычных задач
    private final Map<Integer, Epic> epicList; // Список всех эпиков
    private final Map<Integer, SubTask> subTaskList; // Список всех подзадач
    private int taskCounter; // Счетчик для id задач,эпиков и подзадач
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTaskList;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
        taskCounter = 0;
        historyManager = Managers.getDefaultHistory();

        prioritizedTaskList = new TreeSet<>(new TaskStartTimeComparator());
    }

    // Добавляет задачу в приоритизированный список
    public void prioritize(Task task) {
        if (task != null && task.getStartTime() != null && task.getStartTime().isAfter(Task.emptyDate)) {
            prioritizedTaskList.add(task);
        }
    }

    // Проверяет пересечение по времени с другими задачами
    public boolean tasksInterfere(Task t1, Task t2) {
        if (t1.equals(t2)) return false;
        LocalDateTime t1Start = t1.getStartTime();
        LocalDateTime t1End = t1.getEndTime();
        LocalDateTime t2Start = t2.getStartTime();
        LocalDateTime t2End = t2.getEndTime();
        return (!t1Start.isBefore(t2Start) && !t1Start.isAfter(t2End))
                || (!t1End.isBefore(t2Start) && !t1End.isAfter(t2End));
    }

    // Устанавливает счетчик для id задач
    public void setTaskCounter(int taskCounter) {
        this.taskCounter = taskCounter;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskList);
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
            Task deletedTask = taskList.remove(taskId);
            prioritizedTaskList.remove(deletedTask);
            deletedTask.clearData();
        } else if (subTaskList.containsKey(taskId)) {
            // Получаем подзадачу, eё эпик, удаляем подзадачу в эпике, затем в списке.
            SubTask subTask = subTaskList.get(taskId);
            Epic epic = subTask.getParentTask();
            epic.deleteSubTask(subTask);
            historyManager.remove(taskId);
            subTaskList.remove(taskId).clearData();
            prioritizedTaskList.remove(subTask);
        } else if (epicList.containsKey(taskId)) {
            // Получаем эпик, удаляем все подзадачи эпика в списке, затем удаляем подзадачи в эпике.
            Epic epic = epicList.get(taskId);

            epic.getSubTasks().forEach(subTask -> {
                historyManager.remove(subTask.getId());
                prioritizedTaskList.remove(subTask);
                subTaskList.remove(subTask.getId()).clearData();
            });

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
        taskList.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTaskList.remove(task);
            task.clearData();
        });
        taskList.clear();
    }

    // Удаляет все подзадачи
    @Override
    public void deleteAllSubTasks() {
        epicList.values().forEach(Epic::deleteAllSubTasks);
        subTaskList.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            prioritizedTaskList.remove(subTask);
            subTask.clearData();
        });
        subTaskList.clear();
    }

    // Удаляет все эпики (вместе с подзадачами)
    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        epicList.values().forEach(epic -> {
            historyManager.remove(epic.getId());
            epic.clearData();
        });
        epicList.clear();
    }

    // Создает новый объект Task
    @Override
    public Task createNewTask(String name, String description) {
        taskCounter++;
        return new Task(taskCounter, name, description);
    }

    public Task createNewTask(String name, String description, LocalDateTime startTime, Duration duration) {
        taskCounter++;
        return new Task(taskCounter, name, description, startTime, duration);
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

    public SubTask createNewSubtask(Epic epic, String name, String description, LocalDateTime startTime, Duration duration) {
        taskCounter++;
        return new SubTask(epic, taskCounter, name, description, startTime, duration);
    }

    // Проверяет пересечение задачи по времени выполнения с другими задачами из приоритизированного списка
    private void checkTimeInterference(Task task) {
        if (prioritizedTaskList.stream().anyMatch(existingTask -> tasksInterfere(task, existingTask))) {
            throw new RuntimeException("Есть пересечения по времени с другой задачей");
        }
    }

    // Добавляет задачу в список
    @Override
    public void addTask(Task task) {
        if (task != null && task.getClass().equals(Task.class)) {
            checkTimeInterference(task); // проверка пересечения по времени с другими задачами
            taskList.put(task.getId(), task);
            prioritize(task);
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
            checkTimeInterference(subTask); // проверка пересечения по времени с другими задачами
            subTaskList.put(subTask.getId(), subTask);
            subTask.getParentTask().addSubTask(subTask);
            prioritize(subTask);
        } else {
            System.out.println("Попытка добавить подзадачу null");
        }
    }

    // Обновляет задачу
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            taskList.put(task.getId(), task);
            prioritize(task);
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
            prioritize(subTask);
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
