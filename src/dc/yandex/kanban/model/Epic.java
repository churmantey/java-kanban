package dc.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    private final HashMap<Integer, SubTask> subTasks; // Список подзадач эпика
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new HashMap<>();
        this.type = TaskType.EPIC;
        this.endTime = LocalDateTime.MIN;
    }

    public Epic(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
        subTasks = new HashMap<>();
        this.type = TaskType.EPIC;
        this.endTime = startTime.plus(duration);
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
        // флаг наличия подзадач в статусе NEW
        boolean hasNew = subTasks.values().stream()
                .anyMatch(subTask -> subTask.getStatus().equals(TaskStatus.NEW));

        // флаг наличия подзадач в статусе IN_PROGRESS
        boolean hasInProgress = subTasks.values().stream()
                .anyMatch(subTask -> subTask.getStatus().equals(TaskStatus.IN_PROGRESS));

        // флаг наличия подзадач в статусе DONE
        boolean hasDone = subTasks.values().stream()
                .anyMatch(subTask -> subTask.getStatus().equals(TaskStatus.DONE));

        if (hasInProgress || (hasDone && hasNew)) {
            super.setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            super.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.NEW);
        }

        // расчет времени начала/окончания и продолжительности
        startTime = LocalDateTime.MIN;
        endTime = LocalDateTime.MIN;
        duration = Duration.ZERO;

        subTasks.values().stream()
                .min((SubTask t1, SubTask t2) -> {
                    if (t1.startTime.isAfter(t2.startTime)) return 1;
                    if (t1.startTime.isBefore(t2.startTime)) return -1;
                    return 0;
                }).ifPresent(subTask -> startTime = subTask.getStartTime());

        subTasks.values().stream()
                .max((SubTask t1, SubTask t2) -> {
                    if (t1.getEndTime().isAfter(t2.getEndTime())) return 1;
                    if (t1.getEndTime().isBefore(t2.getEndTime())) return -1;
                    return 0;
                }).ifPresent(subTask -> endTime = subTask.getEndTime());

        duration = Duration.between(startTime, endTime);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Отключаем самостоятельное обновление статуса эпика пустым методом
    @Override
    public void setStatus(TaskStatus status) {
    }

    // Отключаем самостоятельное обновление времени начала эпика пустым методом
    @Override
    public void setStartTime(LocalDateTime startTime) {
    }

    // Отключаем самостоятельное обновление продолжительности
    @Override
    public void setDuration(Duration duration) {
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
