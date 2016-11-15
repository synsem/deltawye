package deltawye.lib;

import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A list data structure for representing rotation schemes.
 *
 * @param <E>
 *            the type of elements stored in the list
 */
public class RotationList<E> extends LinkedList<E> {

    /**
     * Serial Version ID.
     */
    private static final long serialVersionUID = 885401109478230132L;

    /**
     * Create an empty RotationList.
     */
    public RotationList() {
        super();
    }

    /**
     * Create a new RotationList with elements from the specified collection.
     *
     * @param c
     *            a collection of elements
     */
    public RotationList(Collection<? extends E> c) {
        super(c);
    }

    /**
     * Return the element at the specified index, modulo the size of the list.
     *
     * @param index
     *            index of the element
     * @return the element at that index
     */
    public E getMod(int index) {
        return get(Math.floorMod(index, size()));
    }

    /**
     * Return the element that is {@code steps}-many steps next in the rotation
     * order.
     *
     * <p>
     * For example, if {@code steps == 1} then the clockwise next element is
     * returned. If {@code steps} is negative, the direction is
     * counterclockwise.
     *
     * @param elem
     *            the element relative to which steps are taken
     * @param steps
     *            the distance in number of steps
     * @return the element at the specified distance
     */
    public E rotateBy(E elem, int steps) {
        int idx = indexOf(elem);
        if (idx == -1) {
            throw new NoSuchElementException();
        }
        int idxNext = Math.floorMod(idx + steps, size());
        return get(idxNext);
    }

    /**
     * Return the element that is one step next in the specified direction.
     *
     * @param elem
     *            the element relative to which the step is taken
     * @param dir
     *            the direction into which to move
     * @return the element one step in the specified direction
     */
    public E rotateAt(E elem, RotationDirection dir) {
        if (dir == RotationDirection.CLOCKWISE) {
            return nextAfter(elem);
        } // else: COUNTERCLOCKWISE
        return prevBefore(elem);
    }

    /**
     * Return the clockwise next element in the list.
     *
     * @param elem
     *            the previous element
     * @return the next element
     */
    public E nextAfter(E elem) {
        return rotateBy(elem, 1);
    }

    /**
     * Return the clockwise previous element in the list.
     *
     * <p>
     * In order words, return the counterclockwise next element in the list.
     *
     * @param elem
     *            the next element
     * @return the previous element
     */
    public E prevBefore(E elem) {
        return rotateBy(elem, -1);
    }

}
