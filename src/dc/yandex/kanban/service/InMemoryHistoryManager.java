package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory; // История просмотренных задач
    private final int MAX_HISTORY_SIZE = 10; // Размер списка истории просмотренных задач

    public InMemoryHistoryManager() {
        taskHistory = new LinkedList<>();
    }

    // Добавляет задачу в историю просмотров
    @Override
    public void add(Task task) {
        while (taskHistory.size() >= MAX_HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    // Получает список истории просмотров задач
    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(taskHistory);
    }
}
