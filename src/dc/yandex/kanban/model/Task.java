package dc.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    protected LocalDateTime startTime;
    protected Duration duration;


    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = null;
        this.duration = Duration.ZERO;
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Очищает данные задачи (при удалении)
    public void clearData() {
        id = 0;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        else if (duration == null) return startTime;
        else return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + getId() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", start_time='" + getStartTime() + '\'' +
                ", end_time='" + getEndTime() + '\'' +
                '}';
    }
}
