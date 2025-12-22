package com.sylvain.chess.utils;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

/**
 * Credits to <a href="https://github.com/a0x8o/kafka/blob/master/clients/src/main/java/org/apache/kafka/common/utils/CircularIterator.java">Kafka</a>.
 * An iterator that cycles through the {@code Iterator} of a {@code Collection}
 * indefinitely. Useful for tasks such as round-robin load balancing. This class
 * does not provide thread-safe access. This {@code Iterator} supports
 * {@code null} elements in the underlying {@code Collection}. This
 * {@code Iterator} does not support any modification to the underlying
 * {@code Collection} after it has been wrapped by this class. Changing the
 * underlying {@code Collection} may cause a
 * {@link ConcurrentModificationException} or some other undefined behavior.
 */
public class CircularIterator<T> implements Iterator<T> {

  private final Iterable<T> iterable;
  private Iterator<T> iterator;
  private T nextValue;

  /**
   * Create a new instance of a CircularIterator. The ordering of this
   * Iterator will be dictated by the Iterator returned by Collection itself.
   *
   * @param col The collection to iterate indefinitely
   *
   * @throws NullPointerException if col is {@code null}
   * @throws IllegalArgumentException if col is empty.
   */
  public CircularIterator(final Collection<T> col) {
    this.iterable = Objects.requireNonNull(col);
    this.iterator = col.iterator();
    if (col.isEmpty()) {
      throw new IllegalArgumentException("CircularIterator can only be used on non-empty lists");
    }
    this.nextValue = advance();
  }

  /**
   * Returns true since the iteration will forever cycle through the provided
   * {@code Collection}.
   *
   * @return Always true
   */
  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public T next() {
    final T next = nextValue;
    nextValue = advance();
    return next;
  }

  /**
   * Return the next value in the {@code Iterator}, restarting the
   * {@code Iterator} if necessary.
   *
   * @return The next value in the iterator
   */
  private T advance() {
    if (!iterator.hasNext()) {
      iterator = iterable.iterator();
    }
    return iterator.next();
  }

  /**
   * Peek at the next value in the Iterator. Calling this method multiple
   * times will return the same element without advancing this Iterator. The
   * value returned by this method will be the next item returned by
   * {@code next()}.
   *
   * @return The next value in this {@code Iterator}
   */
  public T peek() {
    return nextValue;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
