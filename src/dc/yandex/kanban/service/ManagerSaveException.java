package dc.yandex.kanban.service;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Exception e) {
        super("Ошибка при сохранении файла", e);
    }
}
