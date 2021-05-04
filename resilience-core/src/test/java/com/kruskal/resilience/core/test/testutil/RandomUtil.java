package com.kruskal.resilience.core.test.testutil;

import java.util.Random;

public class RandomUtil {

  public static boolean generateRandomBoolean() {
    Random random = new Random();
    return random.ints(1, 10)
        .findFirst()
        .getAsInt() % 2 == 0;
  }
}
