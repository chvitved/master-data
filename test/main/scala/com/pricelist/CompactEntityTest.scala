package com.pricelist

import org.junit.Test
import org.junit.Assert._

class CompactEntityTest {
  
  @Test
  def elementWithOneValue() {
    val e = Map("k" -> "v")
    val entity = CompactEntity("test", e)
    assertEntity(entity, e)
  }
  
  @Test
  def elementWithTwoValues() {
    val e = Map("key1" -> "value1", "key2" -> "value2") 
    val entity = CompactEntity("test", e)
    assertEntity(entity, e)
  }
  
  @Test
  def elementWithDifferentTypes() {
    val e = Map("key1" -> "value", "key2" -> 1, "key3" -> 1.3)
    val entity = CompactEntity("test", e)
    assertEntity(entity, e)
    
  }
  
  @Test
  def addTwoEntities() {
    val e1 = Map("key1" -> "val1", "key2" -> "val2")
    val e2 = Map("key1" -> "val12", "key2" -> "val22")
    
    val entity1 = CompactEntity("test", e1)
    assertEntity(entity1, e1)
    
    val entity2 = CompactEntity("test", e2)
    assertEntity(entity2, e2)
  }
  
  private def assertEntity(entity: CompactEntity, map: Map[String, Any]) {
    assertEquals(map, entity.get)
  }
  
}