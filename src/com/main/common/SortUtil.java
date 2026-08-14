package common;
import java.util.*;

/**
 * Sort utility — mirrors Python {@code sortUtil.py}.
 *
 * <p>Provides JSON-path-like extraction, multi-key sorting, and bubble sort
 * on lists of maps.
 */
public final class SortUtil {

    private SortUtil() { /* static utility */ }

    // ------------------------------------------------------------------
    // Nested-map access (lightweight jsonpath replacement)
    // ------------------------------------------------------------------

    /**
     * Drill into a nested map by dotted path.
     * Example: {@code getByPath(map, "Result", "0", "name")}.
     */
    @SuppressWarnings("unchecked")
    public static Object getByPath(Map<String, Object> root, String... keys) {
        Object current = root;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
            } else if (current instanceof List) {
                try {
                    int idx = Integer.parseInt(key);
                    current = ((List<Object>) current).get(idx);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
            if (current == null) return null;
        }
        return current;
    }

    // ------------------------------------------------------------------
    // Multi-key sorting (mirrors Python sort_by_keys)
    // ------------------------------------------------------------------

    /**
     * Sort a list of maps by one or more keys.
     *
     * @param list     List of maps to sort.
     * @param reverse  True for descending order.
     * @param keys     Sort keys in priority order.
     * @return A new sorted list (does not mutate the original).
     */
    @SafeVarargs
    public static List<Map<String, Object>> sortByKeys(
        List<Map<String, Object>> list,
        boolean reverse,
        String... keys) {

        List<Map<String, Object>> sorted = new ArrayList<>(list);
        Comparator<Map<String, Object>> comparator = (a, b) -> {
            for (String key : keys) {
                Object va = a.get(key);
                Object vb = b.get(key);
                if (va == null && vb == null) continue;
                if (va == null) return reverse ? 1 : -1;
                if (vb == null) return reverse ? -1 : 1;

                @SuppressWarnings("unchecked")
                int cmp = ((Comparable<Object>) va).compareTo(vb);
                if (cmp != 0) return reverse ? -cmp : cmp;
            }
            return 0;
        };

        sorted.sort(comparator);
        System.out.println("Sort with multiple keys: " + sorted);
        return sorted;
    }

    // ------------------------------------------------------------------
    // Bubble sort (mirrors Python sort_list)
    // ------------------------------------------------------------------

    /**
     * In-place bubble sort of a list of maps by a single key.
     */
    public static void bubbleSort(List<Map<String, Object>> list, String key, boolean reverse) {
        try {
            for (int i = list.size() - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {
                    Map<String, Object> a = list.get(j);
                    Map<String, Object> b = list.get(j + 1);
                    Object va = a.get(key);
                    Object vb = b.get(key);
                    if (va == null || vb == null) continue;

                    @SuppressWarnings("unchecked")
                    int cmp = ((Comparable<Object>) va).compareTo(vb);
                    boolean shouldSwap = reverse ? cmp < 0 : cmp > 0;
                    if (shouldSwap) {
                        list.set(j, b);
                        list.set(j + 1, a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Not item found or not a list: " + e.getMessage());
        }
    }
}
