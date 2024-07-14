package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {

    private TaskManager manager;
    private File tmpFile;
    private Task task, taskFromManager, updatedTask;
    private Epic epic, epicFromManager, updatedEpic;
    private SubTask subTask, subTaskFromManager, updatedSubTask;

    @BeforeEach
    public void beforeEach() {
        try {
            tmpFile = File.createTempFile("test_", ".txt");
            manager = new FileBackedTaskManager(tmpFile.getAbsolutePath());
            System.out.println(tmpFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        task = new Task(1, "Задача 1", "Описание 1");
        updatedTask = new Task(1, "Задача 1 Новое", "Описание 1 Новое");
        epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        updatedEpic = new Epic(2, "Эпик 1 Новое", "Описание эпика 1 Новое");
        subTask = new SubTask(epic, 3, "Подзадача 1 эпика 1", "Описание подзадачи 1");
        updatedSubTask = new SubTask(epic, 3, "Подзадача 1 эпика 1 обновленная",
                "Описание подзадачи 1 обновленное");
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
    public void shouldNotAddEpicAndSubtaskAsTask() {
        manager.addTask(epic);
        manager.addTask(subTask);
        List<Task> taskList = manager.getTasks();

        assertEquals(0, taskList.size(), "Эпик/сабтаск добавлен как задача!");
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
    public void shouldDeleteTask() {
        task = new Task(3, "Задача 1", "Описание 1");
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
    public void shouldSaveToFile() {
        Task managerTask = manager.createNewTask("Задача 1 создана менеджером", "Описание 1");
        manager.addTask(managerTask);
        // в файле должно быть две строки
        try {
            List<String> lines = Files.readAllLines(tmpFile.toPath());
            assertEquals(2, lines.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager.deleteTaskById(managerTask.getId());
        // в файле должна остаться одна строка
        try {
            List<String> lines = Files.readAllLines(tmpFile.toPath());
            assertEquals(1, lines.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldRestoreFromFile() {
        Task managerTask = manager.createNewTask("Задача 1 создана менеджером", "Описание 1");
        Epic managerEpic = manager.createNewEpic("Эпик 2 создан менеджером", "Описание 2");
        SubTask managerSubTask = manager.createNewSubtask(managerEpic,
                "Подзадача 3 создана менеджером", "Описание 3");

        manager.addTask(managerTask);
        manager.addEpic(managerEpic);
        manager.addSubTask(managerSubTask);

        try {
            File tmpFile2 = File.createTempFile("test_", ".txt");
            Files.copy(tmpFile.toPath(), tmpFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileBackedTaskManager taskManagerRestored = FileBackedTaskManager.loadFromFile(tmpFile2);

            assertEquals(managerTask, taskManagerRestored.getTaskById(managerTask.getId()));
            assertEquals(managerEpic, taskManagerRestored.getTaskById(managerEpic.getId()));
            assertEquals(managerSubTask, taskManagerRestored.getTaskById(managerSubTask.getId()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldRestoreFromEmptyFile() {
        try {
            File tmpFile2 = File.createTempFile("test_", ".txt");
            FileBackedTaskManager taskManagerRestored = FileBackedTaskManager.loadFromFile(tmpFile2);
            assertEquals(0, taskManagerRestored.getTasks().size());
            assertEquals(0, taskManagerRestored.getEpics().size());
            assertEquals(0, taskManagerRestored.getSubTasks().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldRestoreFromNonexistingFile() {
        try {
            File tmpFile2 = File.createTempFile("test_", ".txt");
            tmpFile2.delete();
            FileBackedTaskManager taskManagerRestored = FileBackedTaskManager.loadFromFile(tmpFile2);
            assertEquals(0, taskManagerRestored.getTasks().size());
            assertEquals(0, taskManagerRestored.getEpics().size());
            assertEquals(0, taskManagerRestored.getSubTasks().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
