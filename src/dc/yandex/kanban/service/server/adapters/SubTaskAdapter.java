package dc.yandex.kanban.service.server.adapters;

import com.google.gson.stream.JsonWriter;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;

import java.io.IOException;


public class SubTaskAdapter extends TaskAdapter {

    @Override
    protected void writeCommonFields(JsonWriter jsonWriter, Task task) throws IOException {
        super.writeCommonFields(jsonWriter, task);
        jsonWriter.name("parentTask").value(((SubTask) task).getParentTask().getId());
    }
}
