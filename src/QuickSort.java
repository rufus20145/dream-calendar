import java.util.Map;

public class QuickSort {

    public static void quickSortTreeMap(Map<Integer, Event> eventMemory, int lowKey, int highKey) {
        if (eventMemory.size() == 0)
            return;//завершить выполнение если длина массива равна 0

        if (lowKey >= highKey)
            return;//завершить выполнение если уже нечего делить

        // выбрать опорный элемент
        int middle = lowKey + (highKey - lowKey) / 2;
        int opora = spawnTime(middle, eventMemory);

        // разделить на подмассивы, который больше и меньше опорного элемента
        int i = lowKey, j = highKey;
        while (i <= j) {
            while (spawnTime(i, eventMemory) < opora) {
                i++;
            }

            while (spawnTime(j, eventMemory) > opora) {
                j--;
            }

            if (i <= j) {//меняем местами
                Event temp = eventMemory.get(i);
                eventMemory.put(i, eventMemory.get(j));
                eventMemory.put(j, temp);
                i++;
                j--;
            }
        }

        // вызов рекурсии для сортировки левой и правой части
        if (lowKey < j)
            quickSortTreeMap(eventMemory, lowKey, j);

        if (highKey > i)
            quickSortTreeMap(eventMemory, i, highKey);
    }

    public static int spawnTime(int i, Map<Integer, Event> eventMemory) {
        return Integer.parseInt(eventMemory.get(i).getEventHours()) * 60 + Integer.parseInt(eventMemory.get(i).getEventMinutes());
    }
}