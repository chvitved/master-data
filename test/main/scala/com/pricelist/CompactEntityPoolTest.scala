package com.pricelist

import org.junit.Test
import org.junit.Assert._

class CompactEntityPoolTest {

  @Test
  def testPool() {
    val ce = CompactEntity("test", Map("k" -> "v"))
    val same = CompactEntity("test", Map("k" -> "v"))
    assertTrue(ce eq same)
  }
}