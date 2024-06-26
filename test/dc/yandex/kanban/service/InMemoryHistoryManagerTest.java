package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;
import dc.yandex.kanban.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddHistoryAndCheckHistory() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        Epic epic = new Epic(2, "Эпик 2", "Описание 2");
        SubTask subTask = new SubTask(epic, 3, "Подзадача 3", "Описание 3");
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        List<Task> history = manager.getHistory();

        assertNotNull(history, "В истории null");
        assertEquals(3, history.size(), "Задачи не добавлены в историю.");
        assertEquals(task, history.get(0), "Задача в истории и исходная не совпадают!");
        assertEquals(epic, history.get(1), "Эпик в истории и исходный не совпадают!");
        assertEquals(subTask, history.get(2), "Подзадача в истории и исходная не совпадают!");
    }

    @Test
    public void shouldPreserveTaskData() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        manager.add(task);
        List<Task> history = manager.getHistory();

        assertEquals("Задача 1", history.get(0).getName());
        assertEquals("Описание 1", history.get(0).getDescription());
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());

        Task updatedTask = new Task(1, "Задача 1 обновл", "Описание 1 обновл");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.add(updatedTask);
        history = manager.getHistory();

        assertEquals("Задача 1 обновл", history.get(0).getName());
        assertEquals(TaskStatus.IN_PROGRESS, history.get(0).getStatus());
        assertEquals("Описание 1 обновл", history.get(0).getDescription());

        assertEquals(1, history.size(), "Размер истории некорректен.");

    }
    @Test
    public void shouldContainUniqueValuesInHistory() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        Epic epic = new Epic(2, "Эпик 2", "Описание 2");
        SubTask subTask = new SubTask(epic, 3, "Подзадача 3", "Описание 3");
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        List<Task> history = manager.getHistory();

        assertNotNull(history, "В истории null");
        assertEquals(3, history.size(), "Задачи не добавлены в историю.");
        assertEquals(task, history.get(0), "Задача в истории и исходная не совпадают!");
        assertEquals(epic, history.get(1), "Эпик в истории и исходный не совпадают!");
        assertEquals(subTask, history.get(2), "Подзадача в истории и исходная не совпадают!");
    }

    @Test
    public void shouldRemoveFromHistory() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        Epic epic = new Epic(2, "Эпик 2", "Описание 2");
        SubTask subTask = new SubTask(epic, 3, "Подзадача 3", "Описание 3");
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);

        manager.remove(epic.getId());
        assertEquals(2, manager.getHistory().size(), "Задачи не удалены из истории.");
        manager.remove(subTask.getId());
        assertEquals(1, manager.getHistory().size(), "Задачи не удалены из истории.");
        manager.remove(task.getId());
        assertEquals(0, manager.getHistory().size(), "Задачи не удалены из истории.");

    }

}