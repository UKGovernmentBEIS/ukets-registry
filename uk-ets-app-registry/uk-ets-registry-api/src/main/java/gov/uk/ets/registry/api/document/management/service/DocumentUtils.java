package gov.uk.ets.registry.api.document.management.service;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class DocumentUtils {

    /**
     * Checks for duplicate objects based on the fieldFunction.
     *
     * @param list supplied list
     * @param fieldFunction function regarding the field for comparison
     * @return true if the list contains duplicate objects or false otherwise
     * @param <T> object type
     */
    public <T> boolean hasDuplicates(List<T> list, Function<T, String> fieldFunction) {
        Set<String> names = list.stream().map(fieldFunction).collect(Collectors.toSet());
        return list.size() != names.size();
    }

    /**
     * Updates order attributes of the list according to the new order of an item.
     * The list should not contain null elements.
     * Example:
     * List [ItemA(order:1, updatedOn:2hoursAgo), ItemB(order:1, updatedOn:now), ItemC(order:3, updatedOn:1hourAgo)]
     * Expected Result: [ItemB(order:1), ItemA(order:2), ItemC(order:3)]
     *
     * @param list the list with the old order attributes. This list will be updated with the new order attributes.
     * @param orderGetter the function in order to get the order attribute.
     * @param fallbackDateGetter the function in order to get the createdOn attribute.
     * @param orderSetter the function in order to update the order attribute.
     */
    public <T> void updateOrdering(List<T> list,
                                    Function<T, Integer> orderGetter,
                                    Function<T, Date> fallbackDateGetter,
                                    BiConsumer<T, Integer> orderSetter) {

        LinkedList<Integer> newIndexQueue =
            IntStream.rangeClosed(1, list.size()).boxed().collect(Collectors.toCollection(LinkedList::new));

        Comparator<T> orderComparator = Comparator.comparing(orderGetter);
        Comparator<T> afterExisting = orderComparator.thenComparing(fallbackDateGetter);
        Comparator<T> beforeExisting = orderComparator.thenComparing(fallbackDateGetter, Comparator.reverseOrder());

        List<Integer> currentIndex = list.stream().map(orderGetter).toList();

        Comparator<T> comparator = newIndexQueue.stream()
            .filter(exitingIndex -> !currentIndex.contains(exitingIndex))
            .findFirst()
            .filter(replaced -> getDuplicate(currentIndex).filter(duplicate -> duplicate > replaced).isPresent())
            .map(integer -> afterExisting)
            .orElse(beforeExisting);

        list.sort(comparator);
        for (T t : list) {
            orderSetter.accept(t, newIndexQueue.pollFirst());
        }
    }

    private Optional<Integer> getDuplicate(List<Integer> list) {
        return list.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .findFirst();
    }
}
