package dc.yandex.kanban.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    public void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    public void shouldReturnInitializedHistoryManager() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
        assertEquals(0, manager.getHistory().size());
    }
}