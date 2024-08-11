package dc.yandex.kanban.service.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dc.yandex.kanban.model.Task;

import java.io.IOException;

import static dc.yandex.kanban.service.server.adapters.LocalDateTimeAdapter.defaultDateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        writeCommonFields(jsonWriter, task);
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        return null;
    }

    protected void writeCommonFields(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.name("id").value(task.getId());
        jsonWriter.name("name").value(task.getName());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("status").value(task.getStatus().toString());
        if (task.getStartTime() != null) {
            jsonWriter.name("startTime").value(task.getStartTime().format(defaultDateTimeFormatter));
        } else {
            jsonWriter.name("startTime").nullValue();
        }
        if (task.getDuration() != null) {
            jsonWriter.name("duration").value(task.getDuration().toMinutes());
        } else {
            jsonWriter.name("duration").nullValue();
        }
    }
}
