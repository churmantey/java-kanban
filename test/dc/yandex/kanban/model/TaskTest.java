package dc.yandex.kanban.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void shouldBeEqualTasksWithSameId() {
        Task task1 = new Task(222, "Название1", "Описание1");
        Task task2 = new Task(222, "Название2", "Описание2");

        assertEquals(task1, task2, "Задачи с совпадающими Id не равны!");
    }

}