package dc.yandex.kanban.service;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
