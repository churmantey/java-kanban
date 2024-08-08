package dc.yandex.kanban.service.server.adapters;

import com.google.gson.stream.JsonWriter;
import dc.yandex.kanban.model.Epic;
import dc.yandex.kanban.model.SubTask;
import dc.yandex.kanban.model.Task;

import java.io.IOException;


public class EpicAdapter extends TaskAdapter {

    @Override
    protected void writeCommonFields(JsonWriter jsonWriter, Task task) throws IOException {
        super.writeCommonFields(jsonWriter, task);
        jsonWriter.name("subTasks").beginArray();
        for (SubTask subTask : ((Epic) task).getSubTasks()) {
            jsonWriter.value(subTask.getId());
        }
        jsonWriter.endArray();
    }
}
