package dc.yandex.kanban.service;

public class Managers {

    // Возвращает менеджер задач по умолчанию
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Возвращает менеджер истории просмотров по умолчанию
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
