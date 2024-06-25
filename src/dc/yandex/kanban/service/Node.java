/*
Класс для хранения узлов двусвязного списка
*/
package dc.yandex.kanban.service;

import java.util.Objects;

public class Node<T> {
    Node<? extends T> prev; // ссылка на предыдущий узел
    Node<? extends T> next; // ссылка на следующий узел
    T value; // хранимое значение

    public Node(Node<? extends T> prev, Node<? extends T> next, T value) {
        this.prev = prev;
        this.next = next;
        this.value = value;
    }

    public Node<? extends T> getPrev() {
        return prev;
    }

    public void setPrev(Node<? extends T> prev) {
        this.prev = prev;
    }

    public Node<? extends T> getNext() {
        return next;
    }

    public void setNext(Node<? extends T> next) {
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<? extends T> node = (Node<? extends T>) o;
        return node.getValue().equals(this.getValue())
                && node.prev == this.prev
                && node.next == this.next;
    }

    @Override
    public int hashCode() {
        int hash = 17 * Objects.hash(prev, next);
        if (value != null) hash += 31 * value.hashCode();
        hash = hash * 31;

        return hash;
    }
}
