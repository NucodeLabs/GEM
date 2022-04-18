package ru.nucodelabs.data.validation;

import java.lang.reflect.Field;
import java.util.*;

class SizesReflectionUtils {
    private SizesReflectionUtils() {
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private static List<Integer> sizesList(Object data, boolean nonEmptyOnly, String[] names) {
        List<Integer> res = new ArrayList<>();
        for (var field : getAllFields(data.getClass())) {
            if (Arrays.stream(names).anyMatch(s -> s.equals(field.getName()))) {
                field.setAccessible(true);
                Object value = new Object();
                try {
                    value = field.get(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value instanceof Collection<?> collection) {
                    if (nonEmptyOnly) {
                        if (!collection.isEmpty()) {
                            res.add(collection.size());
                        }
                    } else {
                        res.add(collection.size());
                    }
                }
            }
        }
        return res;
    }

    /**
     * Counts minimal size of all fields that are {@code List} of {@code Number} in object {@code data}
     *
     * @param data         object with lists of numbers
     * @param nonEmptyOnly exclude empty lists
     * @return minimal size of lists
     */
    protected static int minSize(Object data, boolean nonEmptyOnly, String[] names) {
        var sizesList = sizesList(data, nonEmptyOnly, names);
        if (sizesList.isEmpty()) {
            return 0;
        } else {
            return Collections.min(sizesList);
        }
    }

    /**
     * Returns if all sizes of all fields that are {@code List} of {@code Number} in object {@code data} are equal
     *
     * @param data         object with lists of numbers
     * @param nonEmptyOnly exclude empty lists
     * @return if lists sizes are equal
     */
    protected static boolean isEqualSizes(Object data, boolean nonEmptyOnly, String[] names) {
        return sizesList(data, nonEmptyOnly, names).isEmpty()
                || sizesList(data, nonEmptyOnly, names).stream().distinct().count() == 1;
    }
}
