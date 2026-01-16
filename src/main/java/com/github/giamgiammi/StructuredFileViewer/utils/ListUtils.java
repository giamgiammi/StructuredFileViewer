package com.github.giamgiammi.StructuredFileViewer.utils;

import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils {
    /**
     * Concatenates multiple lists into a single list.
     * This method combines all elements from the provided lists into a new list.
     * If no lists are provided or the input array is null, an empty list is returned.
     *
     * @param <T> the type of elements in the lists
     * @param lists an array of lists to be concatenated; can be null or empty
     * @return a new list containing all elements from the input lists
     */
    public static <T> List<T> concat(List<T> ...lists) {
        if (lists == null || lists.length == 0) return new ArrayList<>();
        val size = Arrays.stream(lists).map(List::size).reduce(0, Integer::sum);
        val list = new ArrayList<T>(size);
        for (val subList : lists) list.addAll(subList);
        return list;
    }
}
