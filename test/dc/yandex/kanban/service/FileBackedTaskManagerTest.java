package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private File tmpFile;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        try {
            tmpFile = File.createTempFile("test_", ".txt");
            manager = new FileBackedTaskManager(tmpFile.getAbsolutePath());
            System.out.println(tmpFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
