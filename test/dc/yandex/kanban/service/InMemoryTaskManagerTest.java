package dc.yandex.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddTaskAndCheckFields() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        manager.addTask(task);
        Task taskFromManager = manager.getTaskById(1);
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
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);
        Task epicFromManager = manager.getTaskById(1);
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
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask(epic, 2, "Подзадача 1 эпика 1", "Описание подзадачи 1");
        manager.addSubTask(subTask);
        Task subTaskFromManager = manager.getTaskById(2);
        List<SubTask> taskList = manager.getSubTasks();

        assertNotNull(subTaskFromManager, "Подзадача не найдена.");
        assertEquals(subTask.getId(), subTaskFromManager.getId());
        assertEquals(subTask.getName(), subTaskFromManager.getName());
        assertEquals(subTask.getDescription(), subTaskFromManager.getDescription());
        assertEquals(1, taskList.size(), "Подзадача не добавлена, список подзадач пуст!");
        assertEquals(subTask, taskList.get(0), "Добавленная подзадача не совпала с исходной!");
    }

    @Test
    public void shouldNotAddEpicAndSubtaskAsTask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask(epic, 2, "Подзадача 1 эпика 1", "Описание подзадачи 1");
        manager.addTask(epic);
        manager.addTask(subTask);
        List<Task> taskList = manager.getTasks();

        assertEquals(0, taskList.size(), "Эпик/сабтаск добавлен как задача!");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        manager.addTask(task);
        Task updatedTask = new Task(1, "Задача 1 Новое", "Описание 1 Новое");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updatedTask);
        Task taskFromManager = manager.getTaskById(1);

        assertEquals(updatedTask.getId(), taskFromManager.getId());
        assertEquals(updatedTask.getName(), taskFromManager.getName());
        assertEquals(updatedTask.getDescription(), taskFromManager.getDescription());
        assertEquals(updatedTask.getStatus(), taskFromManager.getStatus());
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);
        Epic updatedEpic = new Epic(1, "Эпик 1 Новое", "Описание эпика 1 Новое");
        manager.updateEpic(updatedEpic);
        Task epicFromManager = manager.getTaskById(1);

        assertEquals(updatedEpic.getId(), epicFromManager.getId());
        assertEquals(updatedEpic.getName(), epicFromManager.getName());
        assertEquals(updatedEpic.getDescription(), epicFromManager.getDescription());
    }
    @Test
    public void shouldUpdateSubTask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask(epic, 2, "Подзадача 1 эпика 1", "Описание подзадачи 1");
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask(epic, 2, "Подзадача 1 эпика 1 обновленная",
                "Описание подзадачи 1 обновленное");
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);
        epic.addSubTask(updatedSubTask);
        manager.updateSubTask(updatedSubTask);

        Task epicFromManager = manager.getTaskById(1);
        Task subTaskFromManager = manager.getTaskById(2);

        assertEquals(updatedSubTask.getId(), subTaskFromManager.getId());
        assertEquals(updatedSubTask.getName(), subTaskFromManager.getName());
        assertEquals(updatedSubTask.getDescription(), subTaskFromManager.getDescription());
        assertEquals(updatedSubTask.getStatus(), subTaskFromManager.getStatus());
        assertEquals(epicFromManager.getStatus(), subTaskFromManager.getStatus());
    }

    @Test
    public void shouldDeleteTask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask(epic, 2, "Подзадача 1 эпика 1", "Описание подзадачи 1");
        Task task = new Task(3, "Задача 1", "Описание 1");
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addTask(task);
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getTasks().size());
        manager.deleteTaskById(2);
        manager.deleteTaskById(1);
        manager.deleteTaskById(3);
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
        SubTask managerSubTask = manager.createNewSubtask( managerEpic,
                "Подзадача 3 создана менеджером", "Описание 3");
        SubTask manualSubTask = new SubTask(manualEpic, 3, "Ручная подзадача 3", "Описание ручной подзадачи 3");

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
}