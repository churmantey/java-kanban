package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> taskHistoryMap; // мап для хранения истории
    private Node head; // "голова" списка истории
    private Node tail; // "хвост" списка истории


    public InMemoryHistoryManager() {
        taskHistoryMap = new HashMap<>();
        head = null;
        tail = null;
    }

    // Добавляет задачу в историю просмотров
    @Override
    public void add(Task task) {
        if (task == null) return;
        int taskId = task.getId();
        remove(taskId);
        taskHistoryMap.put(taskId, linkLast(task));
    }

    // Получает список истории просмотров задач
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // получает список задач истории из связного списка
    private ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        Node nextNode = head;
        while (nextNode != null) {
            taskList.add(nextNode.getValue());
            nextNode = nextNode.getNext();
        }
        return taskList;
    }

    @Override
    public void remove(int id) {
        // если задача уже есть в истории - удалим ее
        if (taskHistoryMap.containsKey(id)) {
            Node nodeToDelete = taskHistoryMap.remove(id);
            removeNode(nodeToDelete);
        }
    }

    // Удаляет узел из списка
    private void removeNode(Node node) {
        if (node == null) return;
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();

        // перекидываем ссылки в соседних узлах
        if (prevNode != null) {
            prevNode.setNext(nextNode);
        }
        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        }

        // если удаляется головной или хвостовой элемент - обновляем соотв. поля
        if (node.equals(head)) {
            head = nextNode;
        }
        if (node.equals(tail)) {
            tail = prevNode;
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(tail, null, task);
        if (head == null) {
            head = newNode;
        }
        if (tail != null) {
            tail.setNext(newNode);
        }
        tail = newNode;

        return newNode;
    }

}
