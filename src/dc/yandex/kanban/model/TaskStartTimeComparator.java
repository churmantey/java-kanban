package dc.yandex.kanban.model;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.startTime.isBefore(o2.startTime)) return -1;
        if (o1.startTime.isAfter(o2.startTime)) return 1;
        return 0;
    }
}
