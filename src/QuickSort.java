import java.util.Arrays;
import java.util.Map;

public class QuickSort {

    public static void quickSort(int[] array, int low, int high) {
        if (array.length == 0)
            return;//завершить выполнение если длина массива равна 0

        if (low >= high)
            return;//завершить выполнение если уже нечего делить

        // выбрать опорный элемент
        int middle = low + (high - low) / 2;
        int opora = array[middle];

        // разделить на подмассивы, который больше и меньше опорного элемента
        int i = low, j = high;
        while (i <= j) {
            while (array[i] < opora) {
                i++;
            }

            while (array[j] > opora) {
                j--;
            }

            if (i <= j) {//меняем местами
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }

        // вызов рекурсии для сортировки левой и правой части
        if (low < j)
            quickSort(array, low, j);

        if (high > i)
            quickSort(array, i, high);
    }

    public static void quickSortTreeMap(Map<Integer, Event> eventMemory, int lowKey, int highKey) {
        if (eventMemory.size() == 0)
            return;//завершить выполнение если длина массива равна 0

        if (lowKey >= highKey)
            return;//завершить выполнение если уже нечего делить

        // выбрать опорный элемент
        int middle = lowKey + (highKey - lowKey) / 2;
        int opora = Integer.parseInt(eventMemory.get(middle).getEventHours());

        // разделить на подмассивы, который больше и меньше опорного элемента
        int i = lowKey, j = highKey;
        while (i <= j) {
            while (Integer.parseInt(eventMemory.get(i).getEventHours()) < opora) {
                i++;
            }

            while (Integer.parseInt(eventMemory.get(j).getEventHours()) > opora) {
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

//    public static void main(String[] args) {
//        int[] x = { 8, 0, 4, 7, 3, 7, 10, 12, -3 };
//        System.out.println("Было");
//        System.out.println(Arrays.toString(x));
//
//        int low = 0;
//        int high = x.length - 1;
//
//        quickSort(x, low, high);
//        System.out.println("Стало");
//        System.out.println(Arrays.toString(x));
//    }

}