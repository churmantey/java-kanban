package dc.yandex.kanban.model;

public class SubTask extends Task {

    private final Epic parentTask;

    public SubTask(Epic parentTask, int id, String name, String description) {
        super(id, name, description);
        this.parentTask = parentTask;
    }

    // Получает эпик подзадачи
    public Epic getParentTask() {
        return parentTask;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        parentTask.updateStatus();
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + super.getId() + '\'' +
                ",epic_id='" + parentTask.getId() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                '}';
    }

    @Override
    public void clearData() {
        super.clearData();
    }
}
