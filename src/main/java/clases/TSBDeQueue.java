package clases;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Gonzalo
 * @param <E>
 */
public class TSBDeQueue<E> extends AbstractCollection<E> implements Deque<E>, Serializable, Cloneable {

    /**
     * Aclararaciones.
     *
     * 1)Tal como se indico en el Preview del parcial, se consulto la
     * documentacion de Java,
     * mas precisamente
     * https://docs.oracle.com/javase/7/docs/api/java/util/ArrayDeque.html
     *
     * 2)Los metodos se ordenaron alfabeticamente para hacer coincidir con la
     * documentacion
     * y que sean mas faciles de controlar mientras se programaban.
     *
     *
     *
     *
     */
    //**********************************************************
    //********************* Atributos **************************
    //**********************************************************
    // Arreglo de soporte.    
    private Object items[];

    // Contador de elementos. 
    private int count;

    // Cantidad inicial del arreglo. 
    private int initialCapacity;

    // Contador de las modificacion para hacer thread-safe al iterador. 
    private transient int modCount;

    //**********************************************************
    //********* Constructores definidos en ArrayDeque **********
    //**********************************************************
    /**
     * JavaDoc: Constructs an empty array deque with an initial capacity
     * sufficient to hold 16 elements.
     */
    public TSBDeQueue() {
        this(16);

    }

    /**
     * JavaDoc: Constructs an empty array deque with an initial capacity
     * sufficient to hold the specified number of elements.
     *
     * @param capacity - lower bound on initial capacity of the deque.
     */
    public TSBDeQueue(int capacity) {
        if (capacity < 0) {
            initialCapacity = 16;
        } else {
            initialCapacity = capacity;

        }

        items = new Object[initialCapacity];
        count = 0;
        modCount = 0;
    }

    /**
     * JavaDoc: Constructs a deque containing the elements of the specified
     * collection, in the order they are returned by the collection's iterator.
     * (The first element returned by the collection's iterator becomes the
     * first element, or front of the deque.)
     *
     * @param col - the collection whose elements are to be placed into the
     * deque.
     * @throws NullPointerException - if the specified collection is null.
     */
    public TSBDeQueue(Collection<? extends Object> col) throws NullPointerException {
        items = col.toArray();
        count = col.size();
        initialCapacity = col.size();
        modCount = 0;
    }

    //**********************************************************
    //****** Metodos definidos en ArrayDeque (algunos) *********
    //**********************************************************
    /**
     * JavaDoc: Inserts the specified element at the end of this deque.
     * This method is equivalent to addLast(E).
     *
     * @param e - the element to add
     * @return - true.
     * @throws NullPointerException - if the specified element is null.
     */
    @Override
    public boolean add(E e) throws NullPointerException {
        if (e == null) {
            throw new NullPointerException("El elemento no puede ser null.");
        }

        checkCapacityToAdd();

        items[count] = e;
        count++;
        modCount++;
        return true;
    }

    /**
     * JavaDoc: Inserts the specified element at the front of this deque.
     *
     * @param e - the element to add.
     * @throws NullPointerException - if the specified element is null.
     */
    @Override
    public void addFirst(E e) throws NullPointerException {
        if (e == null) {
            throw new NullPointerException("El elemento no puede ser null.");
        }
        checkCapacityToAdd();

        Object[] temp = new Object[items.length];
        temp[0] = e;
        System.arraycopy(items, 0, temp, 1, count);
        items = temp;
        count++;
        modCount++;

    }

    /**
     * JavaDoc: Inserts the specified element at the end of this deque. This
     * method is equivalent to add(E).
     *
     * @param e - the element to add.
     * @throws NullPointerException - if the specified element is null.
     */
    @Override
    public void addLast(E e) throws NullPointerException {
        add(e);
    }

    /**
     * JavaDoc: Returns a copy of this deque.
     *
     * @return - a copy of this deque.
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        TSBDeQueue<?> temp = (TSBDeQueue<?>) super.clone();
        temp.items = new Object[items.length];
        System.arraycopy(this.items, 0, temp.items, 0, count);
        temp.modCount = 0;
        return temp;
    }

    /**
     * JavaDoc: Returns an iterator over the elements in this deque in reverse
     * sequential order.
     *
     * @return - an iterator over the elements in this deque in reverse
     * sequence.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new TSBDeQueueDescendingIterator();
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the head of the queue
     * represented by this deque. This method is equivalent to getFirst().
     *
     * @return - the head of the queue represented by this deque.
     * @throws NoSuchElementException - if this deque is empty
     */
    @Override
    public E element() throws NoSuchElementException {
        return getFirst();
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the first element of this deque.
     * This method differs from peekFirst only in that it throws an exception if
     * this deque is empty.
     *
     * @return - the head of this deque.
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E getFirst() throws NoSuchElementException {
        if (count == 0) {
            throw new NoSuchElementException("No hay elementos en la Dequeue.");
        }
        return (E) items[0];
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the last element of this deque.
     * This method differs from peekLast only in that it throws an exception if
     * this deque is empty.
     *
     * @return - the tail of this deque.
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E getLast() throws NoSuchElementException {
        if (count == 0) {
            throw new NoSuchElementException("No hay elementos en la Dequeue.");
        }
        return (E) items[count - 1];
    }

    /**
     * JavaDoc: RReturns an iterator over the elements in this deque. The
     * elements will be ordered from first (head) to last (tail). This is the
     * same order that elements would be dequeued (via successive calls to
     * remove() or popped (via successive calls to pop()).
     *
     * @return - an iterator over the elements in this deque.
     */
    @Override
    public Iterator<E> iterator() {
        return new TSBDeQueueIterator();
    }

    /**
     * JavaDoc: Inserts the specified element at the end of this deque. This
     * method is equivalent to offerLast(E).
     *
     * Aclaracion: Si bien JavaDoc no lo establece, se determino que
     * este metodo es identico al metodo add(), ya que ambos insertan un
     * elemento al final de la Dequeue, retornan true si lo lograr y
     * tiran una excepcion si el elemento es null. Por lo que se invocara al
     * metodo add() desde offer().
     *
     * @param e - the element to add.
     * @return - true.
     * @throws NullPointerException - if the specified element is null
     */
    @Override
    public boolean offer(E e) throws NullPointerException {
        return add(e);
    }

    /**
     * JavaDoc: Inserts the specified element at the front of this deque.
     *
     * Aclaracion: se utilizara el metodo addFirst(). Dado que el mismo
     * no retorna nada (void), se invocara a dicho metodo primero y luego
     * se retornara true. De esta manera si el metodo addFirst() puede
     * agregar el elemento se retornara true, y si el metodo lanza una
     * excepcion, este metodo lo hara tambien.
     *
     *
     * @param e - the element to add.
     * @return - true.
     * @throws NullPointerException - if the specified element is null.
     */
    @Override
    public boolean offerFirst(E e) throws NullPointerException {
        addFirst(e);
        return true;
    }

    /**
     * JavaDoc: Inserts the specified element at the end of this deque.
     *
     * Aclaracion: Si bien JavaDoc no lo establece, se determino que
     * este metodo es identico al metodo add(), ya que ambos insertan un
     * elemento al final de la Dequeue, retornan true si lo lograr y
     * tiran una excepcion si el elemento es null. Por lo que se invocara al
     * metodo add() desde offerLast().
     *
     * @param e - the element to add.
     * @return - true.
     * @throws NullPointerException - if the specified element is null
     */
    @Override
    public boolean offerLast(E e) throws NullPointerException {
        return add(e);
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the head of the queue
     * represented by this deque, or returns null if this deque is empty.
     * This method is equivalent to peekFirst().
     *
     * @return - the head of the queue represented by this deque, or null if
     * this deque is empty.
     */
    @Override
    public E peek() {
        return peekFirst();
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the first element of this deque,
     * or returns null if this deque is empty.
     *
     * @return - the head of this deque, or null if this deque is empty.
     */
    @Override
    public E peekFirst() {
        if (count == 0) {
            return null;
        }
        return (E) items[0];
    }

    /**
     * JavaDoc: Retrieves, but does not remove, the last element of this deque,
     * or returns null if this deque is empty.
     *
     * @return - the tail of this deque, or null if this deque is empty.
     */
    @Override
    public E peekLast() {
        if (count == 0) {
            return null;
        }
        return (E) items[count - 1];
    }

    /**
     * JavaDoc: Retrieves and removes the head of the queue represented by this
     * deque (in other words, the first element of this deque), or returns null
     * if this deque is empty.
     * This method is equivalent to pollFirst().
     *
     *
     * @return - the head of the queue represented by this deque, or null if
     * this deque is empty.
     */
    @Override
    public E poll() {
        return pollFirst();
    }

    /**
     * JavaDoc: Retrieves and removes the first element of this deque, or
     * returns null if this deque is empty.
     *
     * Aclaracion: Si bien JavaDoc no lo establece, se reconocio
     *
     * @return - the head of this deque, or null if this deque is empty.
     */
    @Override
    public E pollFirst() {
        if (count == 0) {
            return null;
        }
        return removeFirst();
    }

    /**
     * JavaDoc: Retrieves and removes the last element of this deque, or returns
     * null if this deque is empty.
     *
     * @return - the tail of this deque, or null if this deque is empty.
     */
    @Override
    public E pollLast() {
        if (count == 0) {
            return null;
        }
        return removeLast();
    }

    /**
     * JavaDoc: Pops an element from the stack represented by this deque. In
     * other words, removes and returns the first element of this deque.
     * This method is equivalent to removeFirst().
     *
     * @return - the element at the front of this deque (which is the top of the
     * stack represented by this deque).
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E pop() throws NoSuchElementException {
        return removeFirst();
    }

    /**
     * JavaDoc: Pushes an element onto the stack represented by this deque. In
     * other words, inserts the element at the front of this deque.
     * This method is equivalent to addFirst(E).
     *
     * @param e - the element to push.
     * @throws NullPointerException - if the specified element is null.
     */
    @Override
    public void push(E e) {
        addFirst(e);
    }

    /**
     * JavaDoc: Retrieves and removes the head of the queue represented by this
     * deque. This method differs from poll only in that it throws an exception
     * if this deque is empty.
     * This method is equivalent to removeFirst().
     *
     * @return - the head of the queue represented by this deque.
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E remove() throws NoSuchElementException {
        return removeFirst();
    }

    /**
     * JavaDoc: Retrieves and removes the first element of this deque. This
     * method differs from pollFirst only in that it throws an exception if this
     * deque is empty.
     *
     * @return - the head of the queue represented by this deque.
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E removeFirst() throws NoSuchElementException {
        if (count == 0) {
            throw new NoSuchElementException("No hay elementos en la Dequeue.");
        }
        Object toBeRemoved = items[0];
        Object temp[] = new Object[items.length];

        System.arraycopy(items, 1, temp, 0, count);
        items = temp;

        count--;
        modCount++;

        checkCapacityWhenRemoved();

        return (E) toBeRemoved;

    }

    /**
     * JavaDoc: Removes the first occurrence of the specified element in this
     * deque (when traversing the deque from head to tail). If the deque does
     * not contain the element, it is unchanged. More formally, removes the
     * first element e such that o.equals(e) (if such an element exists).
     * Returns true if this deque contained the specified element (or
     * equivalently, if this deque changed as a result of the call).
     *
     * @param o - element to be removed from this deque, if present.
     * @return - true if the deque contained the specified element.
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        E objAux;
        for (Iterator it = this.iterator(); it.hasNext();) {

            objAux = (E) it.next();

            if (o.equals(objAux)) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    /**
     * JavaDoc: Retrieves and removes the last element of this deque. This
     * method differs from pollLast only in that it throws an exception if this
     * deque is empty.
     *
     * @return - the tail of this deque.
     * @throws NoSuchElementException - if this deque is empty.
     */
    @Override
    public E removeLast() throws NoSuchElementException {
        if (count == 0) {
            throw new NoSuchElementException("No hay elementos en la Dequeue.");
        }
        Object toBeRemoved = items[count - 1];
        items[count - 1] = null;

        count--;
        modCount++;

        checkCapacityWhenRemoved();

        return (E) toBeRemoved;
    }

    /**
     * JavaDoc: Removes the last occurrence of the specified element in this
     * deque (when traversing the deque from head to tail). If the deque does
     * not contain the element, it is unchanged. More formally, removes the last
     * element e such that o.equals(e) (if such an element exists). Returns true
     * if this deque contained the specified element (or equivalently, if this
     * deque changed as a result of the call).
     *
     * @param o - element to be removed from this deque, if present.
     * @return - true if the deque contained the specified element.
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        E objAux;
        for (Iterator it = this.descendingIterator(); it.hasNext();) {

            objAux = (E) it.next();

            if (o.equals(objAux)) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    /**
     * JavaDoc: Returns the number of elements in this deque.
     *
     * @return - the number of elements in this deque.
     */
    @Override
    public int size() {
        return count;
    }

    //**********************************************************
    //******* Metodos heredados de java.util.Collection ********
    //********************************************************** 
    /**
     * JavaDoc: Compares the specified object with this collection for equality.
     *
     * Aclaracion: Se escribio de nuevo el codigo de este metodo.
     * a) Si apuntan al mismo objeto, retorna true.
     * b) Si no son de la misma clase, retorna false.
     * c) Si son de la misma clase y el contenido de sus arreglos es el mismo,
     * retorna true.
     *
     * @param obj - object to be compared for equality with this collection.
     * @return - true if the specified object is equal to this collection.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TSBDeQueue<?> temp = (TSBDeQueue<?>) obj;
        return Arrays.equals(this.items, temp.items);

    }

    /**
     * JavaDoc: Returns the hash code value for this collection.
     *
     * Aclaracion: dado que se escribio equals, se volvio a
     * escribir hashCode, tal como lo indica la documentacion,
     * haciendo que el hashCode depende de cada uno de los elementos
     * del arreglo, de la misma forma que se calcula equals.
     *
     * @return - the hash code value for this collection.
     */
    @Override
    public int hashCode() {
        if (this.isEmpty()) {
            return 0;
        }

        int hc = 0;
        for (E elem : this) {
            hc += elem.hashCode();
        }

        return hc;
    }

    //**********************************************************
    //****************** Metodos adicionales *******************
    //********************************************************** 
    /**
     *
     * Se asegura que exita tamaño suficiente para una cantidad
     * indicada. Si esa cantidad es igual al tamaño actual o
     * dicha cantidad es menor al numero de objetos existentes,
     * no realiza ningun cambio.
     *
     *
     * @param minCapacity - cantidad minima que se solicita.
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity == items.length) {
            return;
        }
        if (minCapacity < count) {
            return;
        }

        Object[] temp = new Object[minCapacity];
        System.arraycopy(items, 0, temp, 0, count);
        items = temp;

    }

    /**
     * Es un metodo wrapper de ensureCapacity, que
     * se utilizara cada vez que se agregue algo.
     */
    private void checkCapacityToAdd() {
        if (count == items.length) {
            this.ensureCapacity(items.length * 2);
        }
    }

    /**
     * Es un metodo wrapper de ensureCapacity, que
     * se utilizara cada vez que se elminie algo.
     */
    private void checkCapacityWhenRemoved() {
        int t = items.length;
        if (count < t / 2) {
            this.ensureCapacity(t / 2);
        }
    }

    /**
     * Elimina un elemento, segun su parametro y luego
     * acomoda el arreglo.
     *
     * @param index - indice del elemento a eliminar.
     * @throws IndexOutOfBoundException - si el idice no pertenece a
     * la lista de elementos.
     */
    private void remove(int index) throws IndexOutOfBoundsException {
        if (index >= count || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int n = count;
        System.arraycopy(items, index + 1, items, index, n - index - 1);
        count--;
        modCount++;
        items[count] = null;
        checkCapacityWhenRemoved();

    }

    //**********************************************************
    //************************ Iteradores **********************
    //********************************************************** 
    /**
     * Iterador que recorre de inicio (head) al final (tail).
     */
    private class TSBDeQueueIterator implements Iterator<E> {

        private int current;
        private boolean next_ok;
        private int expectedModCount;

        /**
         * Constructor del iterador. Setea current en -1.
         */
        public TSBDeQueueIterator() {
            current = -1;
            next_ok = false;
            expectedModCount = modCount;
        }

        /**
         * Indica si queda algun objeto en el recorrido del iterador,
         * que se indica controlando que "current" sea menor que
         * size()-1.
         *
         * @return - true si queda algun objeto en el recorrido.
         */
        @Override
        public boolean hasNext() {
            if (isEmpty()) {
                return false;
            }
            return current < size() - 1;
        }

        /**
         * Retorna el siguiente objeto en el recorrido del iterador.
         *
         *
         * @return - el siguiente objeto en el recorrido.
         * @throws NoSuchElementException - si la lista está vacia o en la lista
         * no quedan elementos por recorrer.
         * @throws ConcurrentModificationException - si la lista es modificada
         * mientras es recorrida.
         */
        @Override
        public E next() throws NoSuchElementException, ConcurrentModificationException {
            if (TSBDeQueue.this.modCount != expectedModCount) {
                throw new ConcurrentModificationException("Modificación inesperada en tabla.");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan elementos por recorrer.");
            }

            current++;
            next_ok = true;
            return (E) items[current];
        }

        /**
         * Elimina el ultimo elemento que retornó el iterador. Debe invocarse
         * antes de invocar a next(). El iterador queda posicionado en el
         * elemento anterior al eliminado.
         *
         * @throws IllegalStateException - si se invoca a remove() sin haber
         * invocado a next(), o si remove() fue invocado mas de una vez
         * luego de una sola invocacion a next().
         */
        @Override
        public void remove() throws IllegalStateException {
            if (!next_ok) {
                throw new IllegalStateException("Debe invocar a next() antes de remove().");
            }

            TSBDeQueue.this.remove(current);
            next_ok = false;
            current--;
            expectedModCount++;
        }
    }

    private class TSBDeQueueDescendingIterator implements Iterator<E> {

        private int current;
        private boolean next_ok;
        private int expectedModCount;

        /**
         * Constructor, setea a current en size().
         */
        public TSBDeQueueDescendingIterator() {
            current = size();
            next_ok = false;
            expectedModCount = modCount;
        }

        /**
         * Indica si queda algun objeto en el recorrido del iterador,
         * que se indica controlando que "current" sea mayor a 0.
         *
         * @return - true si queda algun objeto en el recorrido - false si no
         * quedan objetos.
         */
        @Override
        public boolean hasNext() {
            if (isEmpty()) {
                return false;
            }
            return current > 0;
        }

        /**
         * Retorna el siguiente objeto en el recorrido del iterador.
         *
         *
         * @return - el siguiente objeto en el recorrido.
         * @throws NoSuchElementException - si la lista está vacia o en la lista
         * no quedan elementos por recorrer.
         * @throws ConcurrentModificationException - si la lista es modificada
         * mientras es recorrida.
         */
        @Override
        public E next() throws NoSuchElementException, ConcurrentModificationException {
            if (TSBDeQueue.this.modCount != expectedModCount) {
                throw new ConcurrentModificationException("Modificación inesperada en tabla.");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan elementos por recorrer.");
            }

            current--;
            next_ok = true;
            return (E) items[current];
        }

        /**
         * Elimina el ultimo elemento que retornó el iterador. Debe invocarse
         * antes de invocar a next(). El iterador queda posicionado en el
         * elemento anterior al eliminado.
         *
         * @throws IllegalStateException - si se invoca a remove() sin haber
         * invocado a next(), o si remove() fue invocado mas de una vez
         * luego de una sola invocacion a next().
         */
        @Override
        public void remove() throws IllegalStateException {
            if (!next_ok) {
                throw new IllegalStateException("Debe invocar a next() antes de remove().");
            }

            TSBDeQueue.this.remove(current);
            next_ok = false;
            //current++;
            expectedModCount++;
        }
    }
}
