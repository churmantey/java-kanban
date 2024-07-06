package dc.yandex.kanban.service;

import static org.junit.jupiter.api.Assertions.*;

import dc.yandex.kanban.model.Task;
import org.junit.jupiter.api.Test;

public class NodeTest {

    @Test
    public void CheckNodeEquality() {
        Node node1 = new Node(null, null, new Task(1, "a", "b"));
        Node node2 = new Node(null, null, new Task(1, "a", "b"));
        assertEquals(node1, node2);

        Node node3 = new Node(node1, null, new Task(1, "a", "b"));
        assertNotEquals(node1, node3);
    }

    @Test
    public void CheckHashEquality() {
        Node node1 = new Node(null, null, new Task(1, "a", "b"));
        Node node2 = new Node(null, null, new Task(1, "a", "b"));
        assertEquals(node1.hashCode(), node2.hashCode());

        Node node3 = new Node(node1, null, new Task(1, "a", "b"));
        assertNotEquals(node1.hashCode(), node3.hashCode());
    }


}
