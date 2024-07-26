package dc.yandex.kanban.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {

    @Test
    public void shouldBeEqualEpicsWithSameId() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        Epic epic2 = new Epic(222, "Эпик20", "Описание20");

        assertEquals(epic1, epic2, "Эпики с совпадающими Id не равны!");
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        SubTask subTask = new SubTask(epic1, 223, "Подзадача 223", "Описание 223");
        epic1.addSubTask(subTask);
        ArrayList<SubTask> subTasks = epic1.getSubTasks();

        assertNotNull(subTasks, "Список подзадач отсутствует.");
        assertEquals(1, subTasks.size(), "Размер списка подзадач некорректен");
    }

    @Test
    public void shouldDeleteSubtask() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        SubTask subTask = new SubTask(epic1, 223, "Подзадача 223", "Описание 223");
        epic1.addSubTask(subTask);
        epic1.deleteSubTask(subTask);
        ArrayList<SubTask> subTasks = epic1.getSubTasks();

        assertNotNull(subTasks, "Список подзадач отсутствует.");
        assertEquals(0, subTasks.size(), "Размер списка подзадач некорректен");
    }

    @Test
    public void shouldUpdateStatus() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        SubTask subTask = new SubTask(epic1, 223, "Подзадача 223", "Описание 223");

        assertEquals(epic1.getStatus(), TaskStatus.NEW);

        epic1.addSubTask(subTask);

        assertEquals(epic1.getStatus(), TaskStatus.NEW);

        SubTask subTask2 = new SubTask(epic1, 224, "Подзадача 224", "Описание 224");
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        epic1.addSubTask(subTask2);

        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);

        subTask.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        epic1.addSubTask(subTask);
        epic1.addSubTask(subTask2);

        assertEquals(epic1.getStatus(), TaskStatus.DONE);

        epic1.deleteAllSubTasks();
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        SubTask subTask = new SubTask(epic1, 223, "Подзадача 223", "Описание 223");
        SubTask subTask2 = new SubTask(epic1, 224, "Подзадача 224", "Описание 224");
        epic1.addSubTask(subTask);
        epic1.addSubTask(subTask2);
        epic1.deleteAllSubTasks();
        ArrayList<SubTask> subTasks = epic1.getSubTasks();

        assertNotNull(subTasks, "Список подзадач отсутствует.");
        assertEquals(0, subTasks.size(), "Список подзадач не очищен!");
    }
}