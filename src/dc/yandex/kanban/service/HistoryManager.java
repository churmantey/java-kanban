package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;
import java.util.List;

public interface HistoryManager {

    // Список просмотренных задач
    List<Task> getHistory();

    // Добавляет задачу в список истории просмотров
    void add(Task task);
}
