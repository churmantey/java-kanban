package dc.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, SubTask> subTasks; // Список подзадач эпика
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new HashMap<>();
        this.endTime = null;
    }

    public Epic(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
        subTasks = new HashMap<>();
        this.endTime = null;
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(duration);
        } else if (duration == null) {
            this.endTime = startTime;
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    // Добавляет подзадачу эпика
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateStatusAndTime();
        }
    }

    // Удаляет одну подзадачу эпика
    public void deleteSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.remove(subTask.getId());
            updateStatusAndTime();
        }
    }

    // Удаляет все подзадачи эпика
    public void deleteAllSubTasks() {
        subTasks.clear();
        updateStatusAndTime();
    }

    // Возвращает список всех подзадач эпика
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Обновляет статус эпика на основе статусов подзадач
    // и рассчитывает продолжительность
    public void updateStatusAndTime() {

        boolean hasNew = false; // флаг наличия подзадач в статусе NEW
        boolean hasInProgress = false; // флаг наличия подзадач в статусе IN_PROGRESS
        boolean hasDone = false; // флаг наличия подзадач в статусе DONE
        startTime = null;
        endTime = null;
        duration = Duration.ZERO;

        for (SubTask subTask : subTasks.values()) {
            switch (subTask.getStatus()) {
                case IN_PROGRESS:
                    hasInProgress = true;
                    break;
                case DONE:
                    hasDone = true;
                    break;
                default:
                    hasNew = true;
            }
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            LocalDateTime subTaskEndTime = subTask.getEndTime();
            if (subTaskStartTime != null && (startTime == null || startTime.isAfter(subTaskStartTime))) {
                startTime = subTaskStartTime;
            }
            if (subTaskEndTime != null && (endTime == null || endTime.isBefore(subTaskEndTime))) {
                endTime = subTaskEndTime;
            }
        }

        if (hasInProgress || (hasDone && hasNew)) {
            super.setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            super.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.NEW);
        }

        if (startTime != null && endTime != null) {
            duration = Duration.between(startTime, endTime);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Отключаем самостоятельное обновление статуса эпика пустым методом
    @Override
    public void setStatus(TaskStatus status) {
        throw new RuntimeException("Непосредственная установка статуса эпика недопустима.");
    }

    // Отключаем самостоятельное обновление времени начала эпика пустым методом
    @Override
    public void setStartTime(LocalDateTime startTime) {
        throw new RuntimeException("Непосредственная установка времени начала эпика недопустима.");
    }

    // Отключаем самостоятельное обновление продолжительности
    @Override
    public void setDuration(Duration duration) {
        throw new RuntimeException("Непосредственная установка продолжительности эпика недопустима.");
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + super.getId() + '\'' +
                ",subtasks_id=" + subTasks.keySet() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", start_time='" + getStartTime() + '\'' +
                ", end_time='" + getEndTime() + '\'' +
                '}';
    }

}
