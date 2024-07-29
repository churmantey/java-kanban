package dc.yandex.kanban.service;


import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {

    protected TaskManager manager;
    protected Task task, updatedTask, taskFromManager, conflictingTask, nonconflictingTask;
    protected Epic epic, updatedEpic, epicFromManager;
    protected SubTask subTask, updatedSubTask, subTaskFromManager, subTask2, subTask3;

    @BeforeEach
    public void beforeEach() {
        task = new Task(1, "Задача 1", "Описание 1",
                LocalDateTime.of(2024, 7, 24, 12, 20),
                Duration.ofMinutes(30));
        updatedTask = new Task(1, "Задача 1 Новое", "Описание 1 Новое",
                LocalDateTime.of(2024, 7, 24, 12, 10),
                Duration.ofMinutes(35));
        epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        updatedEpic = new Epic(2, "Эпик 1 Новое", "Описание эпика 1 Новое");
        subTask = new SubTask(epic, 3, "Подзадача 1 эпика 1", "Описание подзадачи 1",
                LocalDateTime.of(2024, 7, 24, 13, 30),
                Duration.ofMinutes(30));
        updatedSubTask = new SubTask(epic, 3, "Подзадача 1 эпика 1 обновленная",
                "Описание подзадачи 1 обновленное");
        subTask2 = new SubTask(epic, 4, "Подзадача 2 эпика 1", "Описание подзадачи 2",
                LocalDateTime.of(2024, 7, 24, 10, 30),
                Duration.ofMinutes(30));
        subTask3 = new SubTask(epic, 5, "Подзадача 3 эпика 1", "Описание подзадачи 3",
                LocalDateTime.of(2024, 7, 24, 8, 30),
                Duration.ofMinutes(30));
        conflictingTask = new Task(6, "Задача 2 конфликтующая", "Описание 2 конфликтующее",
                LocalDateTime.of(2024, 7, 24, 12, 0),
                Duration.ofMinutes(30));
        nonconflictingTask = new Task(7, "Задача 3 неконфликтующая", "Описание 3 неконфликтующее",
                LocalDateTime.of(2024, 7, 29, 12, 0),
                Duration.ofMinutes(30));
    }

    @Test
    public void shouldAddTaskAndCheckFields() {
        manager.addTask(task);
        taskFromManager = manager.getTaskById(1);
        List<Task> taskList = manager.getTasks();

        assertNotNull(taskFromManager, "Задача не найдена.");
        assertEquals(task.getId(), taskFromManager.getId());
        assertEquals(task.getName(), taskFromManager.getName());
        assertEquals(task.getDescription(), taskFromManager.getDescription());
        assertEquals(1, taskList.size(), "Задача не добавлена, список задач пуст!");
        assertEquals(task, taskList.get(0), "Добавленная задача не совпала с исходной!");
    }

    @Test
    public void shouldAddEpicAndCheckFields() {
        manager.addEpic(epic);
        epicFromManager = (Epic) manager.getTaskById(2);
        List<Epic> taskList = manager.getEpics();

        assertNotNull(epicFromManager, "Эпик не найден.");
        assertEquals(epic.getId(), epicFromManager.getId());
        assertEquals(epic.getName(), epicFromManager.getName());
        assertEquals(epic.getDescription(), epicFromManager.getDescription());
        assertEquals(1, taskList.size(), "Эпик не добавлен, список эпиков пуст!");
        assertEquals(epic, taskList.get(0), "Добавленный эпик не совпал с исходным!");
    }

    @Test
    public void shouldAddSubtaskAndCheckFields() {
        manager.addSubTask(subTask);
        subTaskFromManager = (SubTask) manager.getTaskById(3);
        List<SubTask> taskList = manager.getSubTasks();

        assertNotNull(subTaskFromManager, "Подзадача не найдена.");
        assertEquals(subTask.getId(), subTaskFromManager.getId());
        assertEquals(subTask.getName(), subTaskFromManager.getName());
        assertEquals(subTask.getDescription(), subTaskFromManager.getDescription());
        assertEquals(1, taskList.size(), "Подзадача не добавлена, список подзадач пуст!");
        assertEquals(subTask, taskList.get(0), "Добавленная подзадача не совпала с исходной!");
    }

    @Test
    public void shouldHaveEpicIdInSubtask() {
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        assertEquals(epic.getId(), ((SubTask) manager.getTaskById(subTask.getId())).getParentTask().getId());
        assertEquals(epic.getId(), ((SubTask) manager.getTaskById(subTask2.getId())).getParentTask().getId());
        assertEquals(epic.getId(), ((SubTask) manager.getTaskById(subTask3.getId())).getParentTask().getId());
    }


    @Test
    public void shouldUpdateTask() {
        manager.addTask(task);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updatedTask);
        taskFromManager = manager.getTaskById(1);

        assertEquals(updatedTask.getId(), taskFromManager.getId());
        assertEquals(updatedTask.getName(), taskFromManager.getName());
        assertEquals(updatedTask.getDescription(), taskFromManager.getDescription());
        assertEquals(updatedTask.getStatus(), taskFromManager.getStatus());
    }

    @Test
    public void shouldUpdateEpic() {
        manager.addEpic(epic);
        manager.updateEpic(updatedEpic);
        epicFromManager = (Epic) manager.getTaskById(2);

        assertEquals(updatedEpic.getId(), epicFromManager.getId());
        assertEquals(updatedEpic.getName(), epicFromManager.getName());
        assertEquals(updatedEpic.getDescription(), epicFromManager.getDescription());
    }

    @Test
    public void shouldUpdateSubTask() {
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);
        epic.addSubTask(updatedSubTask);
        manager.updateSubTask(updatedSubTask);

        epicFromManager = (Epic) manager.getTaskById(2);
        subTaskFromManager = (SubTask) manager.getTaskById(3);

        assertEquals(updatedSubTask.getId(), subTaskFromManager.getId());
        assertEquals(updatedSubTask.getName(), subTaskFromManager.getName());
        assertEquals(updatedSubTask.getDescription(), subTaskFromManager.getDescription());
        assertEquals(updatedSubTask.getStatus(), subTaskFromManager.getStatus());
        assertEquals(epicFromManager.getStatus(), subTaskFromManager.getStatus());
    }

    @Test
    public void shouldDeleteTasks() {
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addTask(task);
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getTasks().size());
        manager.deleteTaskById(subTask.getId());
        manager.deleteTaskById(task.getId());
        manager.deleteTaskById(epic.getId());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldAddTasksWithSameId() {
        Task managerTask = manager.createNewTask("Задача 1 создана менеджером", "Описание 1");
        Task manualTask = new Task(1, "Ручная задача 1", "Описание ручной задачи 1");
        Epic managerEpic = manager.createNewEpic("Эпик 2 создан менеджером", "Описание 2");
        Epic manualEpic = new Epic(2, "Ручной эпик 2", "Описание ручного эпика 2");
        SubTask managerSubTask = manager.createNewSubtask(managerEpic,
                "Подзадача 3 создана менеджером", "Описание 3");
        SubTask manualSubTask = new SubTask(manualEpic, 3, "Ручная подзадача 3",
                "Описание ручной подзадачи 3");

        manager.addTask(managerTask);
        manager.addTask(manualTask);
        manager.addEpic(managerEpic);
        manager.addEpic(manualEpic);
        manager.addSubTask(managerSubTask);
        manager.addSubTask(manualSubTask);

        assertEquals(manualTask, manager.getTaskById(managerTask.getId()));
        assertEquals(manualEpic, manager.getTaskById(managerEpic.getId()));
        assertEquals(manualSubTask, manager.getTaskById(managerSubTask.getId()));
    }

    @Test
    public void shouldNotAddEpicAndSubtaskAsTask() {
        manager.addTask(epic);
        manager.addTask(subTask);
        List<Task> taskList = manager.getTasks();

        assertEquals(0, taskList.size(), "Эпик/сабтаск добавлен как задача!");
    }

    @Test
    public void shouldNotAddConflictingTask() {
        manager.addTask(task);
        assertThrows(RuntimeException.class, () -> manager.addTask(conflictingTask));
        assertDoesNotThrow(() -> manager.addTask(nonconflictingTask));
    }

    @Test
    public void shouldNotPrioritizeTaskWithEmptyDate() {
        task = new Task(1, "Задача 1 без даты", "Описание 1 без даты");
        manager.addTask(task);
        assertEquals(0, manager.getPrioritizedTasks().size());
    }

    @Test
    public void shouldPrioritizeTask() {
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(4, prioritizedTasks.size());

        assertEquals(subTask3, prioritizedTasks.get(0));
        assertEquals(subTask2, prioritizedTasks.get(1));
        assertEquals(task, prioritizedTasks.get(2));
        assertEquals(subTask, prioritizedTasks.get(3));
    }

    @Test
    public void shouldUpdateEpicStatus() {
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        assertEquals(TaskStatus.NEW, manager.getTaskById(epic.getId()).getStatus(),
                "Статус эпика отличается от NEW!");

        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(epic.getId()).getStatus(),
                "Статус эпика отличается от IN_PROGRESS!");

        subTask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(epic.getId()).getStatus(),
                "Статус эпика отличается от IN_PROGRESS!");

        subTask.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        subTask3.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        assertEquals(TaskStatus.DONE, manager.getTaskById(epic.getId()).getStatus(),
                "Статус эпика отличается от DONE!");
    }

    @Test
    public void shouldUpdateEpicTime() {
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        assertEquals(subTask3.getStartTime(), manager.getTaskById(epic.getId()).getStartTime());
        assertEquals(subTask.getEndTime(), manager.getTaskById(epic.getId()).getEndTime());
    }

}
