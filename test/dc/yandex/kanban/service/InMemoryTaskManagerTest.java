package dc.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        manager = new InMemoryTaskManager();
    }

}