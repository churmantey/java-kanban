package dc.yandex.kanban.service.exceptions;

public class TaskTimeInterferenceException extends RuntimeException {
    public TaskTimeInterferenceException(String message) {
        super(message);
    }
}
