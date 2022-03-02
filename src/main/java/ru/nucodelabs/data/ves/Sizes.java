package ru.nucodelabs.data.ves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sizes {
    private Sizes() {
    }

    private static List<Integer> sizesList(Object data, boolean nonEmptyOnly) {
        List<Integer> res = new ArrayList<>();
        for (var field : data.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = new Object();
            try {
                value = field.get(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value instanceof List) {
                if (((List<?>) value).stream().allMatch(e -> e instanceof Number)) {
                    if (nonEmptyOnly) {
                        if (!((List<?>) value).isEmpty()) {
                            res.add(((List<?>) value).size());
                        }
                    } else {
                        res.add(((List<?>) value).size());
                    }
                }
            }
        }
        return res;
    }

    protected static int minSize(Object data, boolean nonEmptyOnly) {
        var sizesList = sizesList(data, nonEmptyOnly);
        if (sizesList.isEmpty()) {
            return 0;
        } else {
            return Collections.min(sizesList);
        }
    }

    protected static boolean isEqualSizes(Object data, boolean nonEmptyOnly) {
        return sizesList(data, nonEmptyOnly).stream().distinct().count() == 1;
    }
}
