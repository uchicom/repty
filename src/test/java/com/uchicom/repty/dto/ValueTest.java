// (C) 2019 uchicom
package com.uchicom.repty.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ValueTest {

  @Test
  public void setAlignString() {
    assertEquals(0, new Value(1, 2, "3", "L").align);
    assertEquals(1, new Value(1, 2, "3", "C").align);
    assertEquals(2, new Value(1, 2, "3", "R").align);
    assertEquals(0, new Value(1, 2, "3", "B").align);
    assertEquals(10, new Value(1, 2, "3", "M").align);
    assertEquals(20, new Value(1, 2, "3", "T").align);
    assertEquals(0, new Value(1, 2, "3", "LB").align);
    assertEquals(10, new Value(1, 2, "3", "LM").align);
    assertEquals(20, new Value(1, 2, "3", "LT").align);
    assertEquals(1, new Value(1, 2, "3", "CB").align);
    assertEquals(11, new Value(1, 2, "3", "CM").align);
    assertEquals(21, new Value(1, 2, "3", "CT").align);
    assertEquals(2, new Value(1, 2, "3", "RB").align);
    assertEquals(12, new Value(1, 2, "3", "RM").align);
    assertEquals(22, new Value(1, 2, "3", "RT").align);
  }
}
