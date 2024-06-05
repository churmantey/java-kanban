package dc.yandex.kanban.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void shouldBeEqualSubTasksWithSameId() {
        Epic epic1 = new Epic(21, "Эпик10", "Описание10");
        Epic epic2 = new Epic(22, "Эпик20", "Описание20");
        SubTask subTask1 = new SubTask(epic1,222, "Подзадча1", "Описание1");
        SubTask subTask2 = new SubTask(epic2,222, "Подзадча2", "Описание2");

        assertEquals(subTask1, subTask2, "Подзадачи с совпадающими Id не равны!");
    }

    @Test
    public void shouldGetParentTask() {
        Epic epic1 = new Epic(222, "Эпик10", "Описание10");
        //Epic epic2 = new Epic(223, "Эпик20", "Описание20");
        SubTask subTask1 = new SubTask(epic1,222, "Подзадча1", "Описание1");

        assertNotNull(subTask1.getParentTask(), "Эпик подзадачи не задан");
        assertEquals(epic1, subTask1.getParentTask(), "Эпик подзадачи определяется неверно");
    }
}