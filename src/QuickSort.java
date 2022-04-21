import java.util.Map;

public class QuickSort {
    private static final int COEFF_RATE_IN_MINUTES = 60;

    public static void quickSortTreeMap(Map<Integer, Event> eventMemory, int lowKey, int highKey) {
        if (eventMemory.size() == 0)
            return; // Завершение выполнения, если длина массива равна 0

        if (lowKey >= highKey)
            return; // Завершение выполнения, если уже нечего делить

        // Выбор опорного элемента
        int middle = lowKey + (highKey - lowKey) / 2;
        int opora = spawnTime(middle, eventMemory);

        // Разделение на подмассивы, который больше и меньше опорного элемента
        int countLowKey = lowKey, countHighKey = highKey;
        while (countLowKey <= countHighKey) {
            while (spawnTime(countLowKey, eventMemory) < opora) {
                countLowKey++;
            }

            while (spawnTime(countHighKey, eventMemory) > opora) {
                countHighKey--;
            }

            // Меняем местами
            if (countLowKey <= countHighKey) {
                Event temp = eventMemory.get(countLowKey);
                eventMemory.put(countLowKey, eventMemory.get(countHighKey));
                eventMemory.put(countHighKey, temp);
                countLowKey++;
                countHighKey--;
            }
        }

        // Вызов рекурсии для сортировки левой и правой частей
        if (lowKey < countHighKey)
            quickSortTreeMap(eventMemory, lowKey, countHighKey);

        if (highKey > countLowKey)
            quickSortTreeMap(eventMemory, countLowKey, highKey);
    }

    public static int spawnTime(int i, Map<Integer, Event> eventMemory) {
        return Integer.parseInt(eventMemory.get(i).getEventHours()) * COEFF_RATE_IN_MINUTES + Integer.parseInt(eventMemory.get(i).getEventMinutes());
    }
}