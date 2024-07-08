package dc.yandex.kanban.service;

import dc.yandex.kanban.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String filename; // имя файла для хранения состояния менеджера
    private final String DELIMITER = ","; // разделитель значений в строках файла

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateTask (Task task){
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic (Epic epic){
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask (SubTask subTask){
        super.updateSubTask(subTask);
        save();
    }

    // Сохраняет состояние менеджера в файл
    public void save() {
        String header = "id,type,name,status,description,epic";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            bufferedWriter.write(header);
            bufferedWriter.newLine();
            writeTaskList(bufferedWriter, getTasks());
            writeTaskList(bufferedWriter, getEpics());
            writeTaskList(bufferedWriter, getSubTasks());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void writeTaskList(BufferedWriter bufferedWriter, List<? extends Task> taskList) throws IOException {
        for (Task task : taskList) {
            bufferedWriter.write(toString(task));
            bufferedWriter.newLine();
        }
    }

    public static void loadFromFile(File file) {
        try {
            String fileData = Files.readString(file.toPath());
            for (String line : fileData.split("\r")) {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Формирует строковое представление задачи для сохранения в файл
    private String toString(Task task) {
        ArrayList<String> parts = new ArrayList<>();

        if (task != null) {
            parts.add(Integer.toString(task.getId()));
            if (task instanceof Epic) {
                parts.add(TaskType.EPIC.name());
            } else if (task instanceof SubTask) {
                parts.add(TaskType.SUBTASK.name());
            } else {
                parts.add(TaskType.TASK.name());
            }
            parts.add(task.getName());
            parts.add(task.getStatus().name());
            parts.add(task.getDescription());
            if (task instanceof SubTask) {
                parts.add(Integer.toString(((SubTask) task).getParentTask().getId()));
            }
            return String.join(DELIMITER, parts);
        } else {
            return "";
        }
    }

    // Создает задачу по ее строковому представлению
    private Task fromString(String value) {
        // Если передали null или пустую строку - разобрать не можем
        if (value == null || value.isEmpty() || value.isBlank()) {
            return null;
        }
        String[] parts = value.split(DELIMITER);
        // Если в строке меньше 5 частей - разобрать не можем
        if (parts.length < 5) {
            return null;
        }

        Task task;
        int taskId = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String taskName = parts[2];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String taskDescription = parts[4];
        int epicId = 0;
        if (parts.length > 5) {
            epicId = Integer.parseInt(parts[5]);
        }
        switch (taskType) {
            case EPIC:
                task = new Epic(taskId, taskName, taskDescription);
                break;
            case SUBTASK:
                task = new SubTask((Epic) getTaskById(epicId), taskId, taskName, taskDescription);
                break;
            default:
                task = new Task(taskId, taskName, taskDescription);
        }
        task.setStatus(taskStatus);
        return task;
    }

    public static void main(String[] args) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager("manager_save.txt");

        Task task1 = taskManager.createNewTask("Почитать",
                "Прочитать главу из книги Дж. Оруэлла '1984'");
        Task task2 = taskManager.createNewTask("Позаниматься музыкой",
                "Выучить 'Cullen Bay'");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = taskManager.createNewEpic("Двухэтажный дом", "Нужен двухэтажный кирпичный");
        SubTask subTask1e1 = taskManager.createNewSubtask(epic1, "Основание", "Заложить фундамент");
        SubTask subTask2e1 = taskManager.createNewSubtask(epic1, "Стены", "Возвести стены");
        SubTask subTask3e1 = taskManager.createNewSubtask(epic1, "Кровля", "Уложить кровлю");
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1e1);
        taskManager.addSubTask(subTask2e1);
        taskManager.addSubTask(subTask3e1);
        subTask2e1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2e1);

        Epic epic2 = taskManager.createNewEpic("Запастить продуктами", "В доме нужна еда");
        taskManager.addEpic(epic2);

    }

}
