package com.pricelist
import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedMap

object NewPricelist {

  val declaredIndexes = Map[String, Map[String,DeclaredIndexes[_]]](
    "Laegemiddel" -> Map[String,DeclaredIndexes[_]]("drugid" -> new DeclaredIndexes[Long]("drugid", (ce) => ce.get("drugid").asInstanceOf[Long]))
  )
  
  class DeclaredIndexes[IndexType<%Ordered[IndexType]](val name: String, indexMethod: CompactEntity => IndexType) {
	//TODO should be possible without hacking the type system  
    def emptyIndex: NewIndex[Ordered[_]] = {
     (new NewIndex(TreeMap[IndexType, Set[CompactEntity]](), indexMethod)).asInstanceOf[NewIndex[Ordered[_]]] 
    }
  }
  
  def apply(es: Map[String, Set[CompactEntity]], previousPricelist: Option[NewPricelist]): NewPricelist = {
    val map = declaredIndexes.foldLeft(Map[String, Map[String, NewIndex[_]]]()) {
      (map, tuple) =>
        val entityName = tuple._1
        val declaredIndexes = tuple._2
        val entityIndexMap = declaredIndexes.foldLeft(Map[String, NewIndex[_]]()) {
          (map, tuple) =>
            val indexName = tuple._1
            val indexDeclaration = tuple._2
            val previousIndex: Option[NewIndex[Ordered[_]]] = previousPricelist match {
              case Some(p) => p.map(entityName)(indexName).asInstanceOf[Some[NewIndex[Ordered[_]]]] //type hack...should be avoidable
              case None => None
            }
            map + (indexName -> createIndex(es(entityName), indexDeclaration, previousIndex))
        }
        map + (entityName -> entityIndexMap)
    }
    new NewPricelist(map)
  }
  
  private def createIndex(entities: Set[CompactEntity], declaredIndex: DeclaredIndexes[_], previous: Option[NewIndex[Ordered[_]]]): NewIndex[_] = {
    val index: NewIndex[Ordered[_]] = previous match {
      case Some(pi) => pi
      case None => declaredIndex.emptyIndex
    }
    
    entities.foldLeft(index) {(index, ce) =>
    	index + ce
    }
    
//    val map1 = removeDeleted(entities, map, previousEntities)
//    addNewEntries(entities, declaredIndex, previousEntities, map1)
//    null
  }
  
//  private def addNewEntries(entities: Set[CompactEntity], declaredIndex: DeclaredIndexes[_],
//      previousEntitySet: Set[CompactEntity], map: SortedMap[Ordered[_], Set[CompactEntity]]) : SortedMap[Ordered[_], Set[CompactEntity]] = {
//    val newEntities = entities -- previousEntitySet
//    newEntities.foldLeft(map) {
//      (map, entity) => 
//        val key = declaredIndex.indexMethod(entity).asInstanceOf[Ordered[_]]
//        val set = map.getOrElse(key, Set())
//        map + (key -> (set + entity))
//    }
//  }
//  
//  private def removeDeleted(entities: Set[CompactEntity], map: SortedMap[Ordered[_], Set[CompactEntity]], 
//      previousEntitySet: Set[CompactEntity]) : SortedMap[Ordered[_], Set[CompactEntity]] = {
//	val deletedEntities = previousEntitySet -- entities
//    
//    map.foldLeft(map) {
//      (map, tuple) => 
//        val key: Ordered[_] = tuple._1
//        val set = tuple._2
//        val newSet = set -- deletedEntities
//        if (newSet != set) {
//          map + (key -> newSet)
//        } else map
//    }
//  } 
}

class NewPricelist private(val map: Map[String, Map[String, NewIndex[_]]])

