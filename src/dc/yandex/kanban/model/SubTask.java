package dc.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final Epic parentTask;

    public SubTask(Epic parentTask, int id, String name, String description) {
        super(id, name, description);
        this.parentTask = parentTask;
    }

    public SubTask(Epic parentTask, int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
        this.parentTask = parentTask;
    }

    // Получает эпик подзадачи
    public Epic getParentTask() {
        return parentTask;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        parentTask.updateStatusAndTime();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + super.getId() + '\'' +
                ",epic_id='" + parentTask.getId() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", start_time='" + getStartTime() + '\'' +
                ", end_time='" + getEndTime() + '\'' +
                '}';
    }

    @Override
    public void clearData() {
        super.clearData();
    }
}
