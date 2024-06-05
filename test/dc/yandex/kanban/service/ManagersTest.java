package dc.yandex.kanban.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    public void shouldReturnInitializedTaskManager(){
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    public void shouldReturnInitializedHistoryManager(){
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
        assertEquals(0, manager.getHistory().size());
     }
}