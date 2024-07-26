package dc.yandex.kanban.service;

import dc.yandex.kanban.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String filename; // имя файла для хранения состояния менеджера
    private final String delimiter = ","; // разделитель значений в строках файла
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    // Сохраняет состояние менеджера в файл
    public void save() {
        String header = "id,type,start_time,duration,name,status,description,epic";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            bufferedWriter.write(header);
            bufferedWriter.newLine();
            writeTaskList(bufferedWriter, getTasks());
            writeTaskList(bufferedWriter, getEpics());
            writeTaskList(bufferedWriter, getSubTasks());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла", e);
        }
    }

    // Выводит задачи из списка в виде строк в буфер вывода
    private void writeTaskList(BufferedWriter bufferedWriter, List<? extends Task> taskList) throws IOException {
        taskList.forEach(task -> {
            try {
                bufferedWriter.write(toString(task));
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    // Возвращает объект менеджера, восстановленного из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getAbsolutePath());
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                int maxId = 0;
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.isBlank()) continue;
                    Task task = manager.fromString(line);
                    if (task == null) continue;
                    switch (task.getType()) {
                        case EPIC:
                            manager.addEpic((Epic) task);
                            break;
                        case SUBTASK:
                            manager.addSubTask((SubTask) task);
                            break;
                        case TASK:
                            manager.addTask(task);
                            break;
                        default:
                            System.out.println("Неизвестный тип задачи: " + task.getType());
                    }
                    maxId = Integer.max(maxId, task.getId());
                }
                manager.setTaskCounter(maxId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }

    // Формирует строковое представление задачи для сохранения в файл
    private String toString(Task task) {
        ArrayList<String> parts = new ArrayList<>();

        if (task != null) {
            parts.add(Integer.toString(task.getId()));
            parts.add(task.getType().name());
            parts.add(task.getStartTime().format(dateTimeFormatter));
            parts.add(Long.toString(task.getDuration().toMinutes()));
            parts.add(task.getName());
            parts.add(task.getStatus().name());
            parts.add(task.getDescription());
            if (task.getType().equals(TaskType.SUBTASK)) {
                parts.add(Integer.toString(((SubTask) task).getParentTask().getId()));
            }
            return String.join(delimiter, parts);
        } else {
            return "";
        }
    }

    // Создает задачу по ее строковому представлению
    private Task fromString(String value) {
        // Если передали null или пустую строку - разобрать не можем
        if (value == null || value.isBlank()) {
            return null;
        }
        String[] parts = value.split(delimiter);
        // Если в строке меньше 7 частей - разобрать не можем
        if (parts.length < 7) {
            return null;
        }

        Task task;
        int taskId = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        LocalDateTime taskTime = LocalDateTime.parse(parts[2], dateTimeFormatter);
        Duration taskDuration = Duration.ofMinutes(Long.parseLong(parts[3]));
        String taskName = parts[4];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[5]);
        String taskDescription = parts[6];
        int epicId = 0;
        if (parts.length > 7) {
            epicId = Integer.parseInt(parts[7]);
        }
        switch (taskType) {
            case EPIC:
                task = new Epic(taskId, taskName, taskDescription, taskTime, taskDuration);
                break;
            case SUBTASK:
                task = new SubTask((Epic) getTaskById(epicId), taskId, taskName, taskDescription, taskTime, taskDuration);
                break;
            default:
                task = new Task(taskId, taskName, taskDescription, taskTime, taskDuration);
        }
        task.setStatus(taskStatus);
        return task;
    }

}
