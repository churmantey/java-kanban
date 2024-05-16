import java.util.HashMap;

public class Epic extends Task{

    private final HashMap<Integer, SubTask> subTasks; // Список подзадач эпика

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new HashMap<>();
    }

    // Добавляет подзадачу эпика
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateStatus();
        }
    }

    // Удаляет одну подзадачу эпика
    public void deleteSubTask (SubTask subTask) {
        if (subTask != null) {
            subTasks.remove(subTask.getId());
            updateStatus();
        }
    }

    // Удаляет все подзадачи эпика
    public void deleteAllSubTasks () {
        subTasks.clear();
        updateStatus();
    }

    // Возвращает список всех подза дач эпика
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    // Обновляет статус эпика на основе статусов подзадач
    public void updateStatus() {
        boolean hasNew = false; // флаг наличия подзадач в статусе NEW
        boolean hasInProgress = false; // флаг наличия подзадач в статусе IN_PROGRESS
        boolean hasDone = false; // флаг наличия подзадач в статусе DONE

        for (SubTask subTask : subTasks.values()) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                hasInProgress = true;
                break;
            }
            if (subTask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            } else {
                hasNew = true;
            }
        }

        if (hasInProgress || (hasDone && hasNew)) {
            super.setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            super.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.NEW);
        }
    }

    // Отключаем самостоятельное обновление статуса эпика пустым методом
    @Override
    public void setStatus(TaskStatus status) {
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + super.getId() + '\'' +
                ",subtasks_num=" + subTasks.size() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                '}';
    }

}
