package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;
import java.util.ArrayList;

public interface HistoryManager {

    // Список просмотренных задач
    ArrayList<Task> getHistory();

    // Добавляет задачу в список истории просмотров
    void add(Task task);
}
