package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> taskHistory; // История просмотренных задач
    private final int MAX_HISTORY_SIZE = 10; // Размер списка истории просмотренных задач

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
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
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
