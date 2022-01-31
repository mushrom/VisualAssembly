// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************
//
// Based on exercise 23.3, using MyList and MyArrayList
// in place of java's List and ArrayList

import java.util.Comparator;
import java.util.Collections;
import java.util.Random;

// Best case:    O(n log n)
// Average case: O(n log n)
// Worst case:   O(n^2) (uncommon, but possible)
public class QuickSort {
	public static <E extends Comparable<E>>
		void quickSort(MyList<E> list)
	{
		quickSort(list, (a, b) -> { return a.compareTo(b); });
	}

	public static <E>
		void quickSort(MyList<E> list, Comparator<? super E> comparator)
	{
		quickSort(list, comparator, 0, list.size() - 1);
	}

	// recursive helper method
	public static <E>
		void quickSort(MyList<E> list,
		               Comparator<? super E> comparator,
		               int start,
		               int end)
	{
		if (start < end) {
			int pivot = partition(list, comparator, start, end);

			quickSort(list, comparator, start, pivot - 1);
			quickSort(list, comparator, pivot + 1, end);
		}
	}

	public static <E>
		int partition(MyList<E> list,
		              Comparator<? super E> comparator,
		              int start,
		              int end)
	{
		E pivotValue = list.get(start);

		int low  = start + 1;
		int high = end;

		// repeatedly swap values when low and high values are on the wrong sides
		// of the pivot value, until low and high meet somewhere.
		while (low <= high) {
			while (low <= high && comparator.compare(list.get(low), pivotValue) <= 0) {
				low++;
			}

			while (low <= high && comparator.compare(list.get(high), pivotValue) > 0) {
				high--;
			}

			if (low < high) {
				E temp = list.get(low);
				list.set(low, list.get(high));
				list.set(high, temp);
			}
		}

		// move the high index until the value in it is less than the pivot value,
		// allowing for a valid swap with the start index.
		while (high > start && comparator.compare(list.get(high), pivotValue) >= 0) {
			high--;
		}

		// if we didn't reach the start at the last step,
		// then the start (pivot value) and high indices can be swapped.
		if (high > start) {
			list.set(start, list.get(high));
			list.set(high, pivotValue);
			return high;

		// otherwise the start index was the smallest value in the set,
		// so there's no left partition.
		} else {
			return start;
		}
	}

	private static <E extends Comparable<E>> boolean isSorted(MyList<E> list) {
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i-1).compareTo(list.get(i)) > 0) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		MyArrayList<Integer> data = new MyArrayList<Integer>();
		Random rand = new Random();

		for (int i = 0; i < 1_000_000; i++) {
			data.add(rand.nextInt(Integer.MAX_VALUE));
		}

		long startTime = System.nanoTime();
		quickSort(data);
		long totalTime = System.nanoTime() - startTime;
		float seconds = totalTime / 1_000_000_000.f;

		System.out.printf("Sorted: %b, time: %g s\n", isSorted(data), seconds);
	}
}
