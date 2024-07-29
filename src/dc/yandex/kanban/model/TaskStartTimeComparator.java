package dc.yandex.kanban.model;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartTime() == null && o2.getStartTime() == null) {
            return o1.getId() - o2.getId();
        }
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;
        if (o1.getStartTime().isBefore(o2.getStartTime())) return -1;
        if (o1.getStartTime().isAfter(o2.getStartTime())) return 1;
        return 0;
    }
}
