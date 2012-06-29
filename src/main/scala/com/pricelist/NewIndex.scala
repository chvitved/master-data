package com.pricelist
import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object NewIndex {
  private def buildMap[CompactEntity,IndexType<%Ordered[IndexType]](data: Set[CompactEntity], indexMethod: (CompactEntity) => IndexType) : SortedMap[IndexType, Set[CompactEntity]] = {
    val mutableMap = new scala.collection.mutable.HashMap[IndexType, scala.collection.mutable.ListBuffer[CompactEntity]]() {
      override def default(key: IndexType) = {
          val list = new scala.collection.mutable.ListBuffer[CompactEntity]()
          put(key, list)
          list
      }
    }
    data.foreach(entity => mutableMap(indexMethod(entity)) + entity)
    val mapWithSet = mutableMap.elements.map((tuple) => (tuple._1, Set(tuple._2.toSeq:_*)))
    TreeMap[IndexType, Set[CompactEntity]](mapWithSet.toList.toSeq:_*)
    //wow these lines got ugly --improve
  }
  
  def create[Entity,IndexType<%Ordered[IndexType]](data: Set[CompactEntity], indexMethod: (CompactEntity) => IndexType ) = {
    new NewIndex(buildMap(data, indexMethod), indexMethod)
  }
}

class NewIndex[IndexType<%Ordered[IndexType]] (val map: SortedMap[IndexType, Set[CompactEntity]], indexMethod: (CompactEntity) => IndexType) {

  def === (key: IndexType) : Set[CompactEntity] = {
    map.getOrElse(key, Set[CompactEntity]())
  }
  
  def + (ce: CompactEntity): NewIndex[IndexType] = {
    val key = indexMethod(ce)
    val set = map.getOrElse(key, Set()) + ce
    val newMap = map + (key -> set)
    new NewIndex(newMap, indexMethod)
  }
  
  def - (ce: CompactEntity): NewIndex[IndexType] = {
    val key = indexMethod(ce)
    val set = map(key) - ce
    val newMap = map + (key -> set)
    new NewIndex(newMap, indexMethod)
  }
  
  def entities = map.foldLeft(Set[CompactEntity]()) {
	  (set, tuple) => set ++ tuple._2
  }
  
}