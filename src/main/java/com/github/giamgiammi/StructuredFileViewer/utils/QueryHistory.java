package com.github.giamgiammi.StructuredFileViewer.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that manages a history of queries, allowing for navigation between previous and next entries.
 * The history has a maximum size of 1000, and older entries are removed
 * when the size limit is exceeded.
 */
public class QueryHistory {
    private final int MAX_HISTORY_SIZE = 1000;
    private List<String> history = new ArrayList<>();

    @Getter @Setter
    private boolean updating;

    private int current;

    public void add(String query) {
        if (updating) return;
        history = new ArrayList<>(history.subList(current, history.size()));
        history.addFirst(query);
        if (history.size() > MAX_HISTORY_SIZE) history.removeLast();
        current = 0;
    }

    public String getPrevious() {
        if (current == history.size() - 1) return "";
        return history.get(++current);
    }
    public String getNext() {
        if (current == 0) return history.getFirst();
        return history.get(--current);
    }
}
