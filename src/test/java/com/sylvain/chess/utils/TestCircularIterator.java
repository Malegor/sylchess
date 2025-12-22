package com.sylvain.chess.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestCircularIterator {
  @Test
  public void testCircularIterator() {
    final CircularIterator<Integer> it = new CircularIterator<>(List.of(0,1));
    int i = 0;
    Integer last = null;
    while (it.hasNext() && i < 10) {
      Integer current = it.next();
      System.out.println(current);
      Assert.assertNotNull(current);
      if (last != null)
        Assert.assertNotEquals(last, current);
      else
        Assert.assertEquals(0, current.intValue());
      i++;
      last = current;
    }
    Assert.assertTrue(it.hasNext() && i == 10);
  }
}
