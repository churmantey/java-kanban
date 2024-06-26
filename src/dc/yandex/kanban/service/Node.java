/*
Класс для хранения узлов двусвязного списка
*/
package dc.yandex.kanban.service;

import dc.yandex.kanban.model.Task;

import java.util.Objects;

public class Node {
    Node prev; // ссылка на предыдущий узел
    Node next; // ссылка на следующий узел
    Task value; // хранимое значение

    public Node(Node prev, Node next, Task value) {
        this.prev = prev;
        this.next = next;
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return node.getValue().equals(this.getValue())
                && node.getPrev() == this.getPrev()
                && node.getNext() == this.getNext();
    }

    @Override
    public int hashCode() {
        int hash = 17 * Objects.hash(getPrev(), getNext());
        if (value != null) hash += 31 * value.hashCode();
        hash = hash * 31;

        return hash;
    }
}
