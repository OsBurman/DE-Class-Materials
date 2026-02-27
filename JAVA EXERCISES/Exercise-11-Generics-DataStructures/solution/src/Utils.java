import java.util.*;

/** Generic Utility Methods — Solution */
public class Utils {
    public static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static <T extends Comparable<T>> T findMax(List<T> list) {
        if (list == null || list.isEmpty())
            throw new NoSuchElementException();
        T max = list.get(0);
        for (T item : list)
            if (item.compareTo(max) > 0)
                max = item;
        return max;
    }

    public static double sumList(List<? extends Number> list) {
        double total = 0;
        for (Number n : list)
            total += n.doubleValue();
        return total;
    }

    public static <T> List<T> repeat(T element, int times) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < times; i++)
            result.add(element);
        return result;
    }
}
