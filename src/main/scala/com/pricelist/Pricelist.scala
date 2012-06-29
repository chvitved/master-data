package com.pricelist
import scala.collection.immutable.SortedMap
import scala.collection.immutable.TreeMap

object Pricelist {

	val declaredIndexes = Map[String, Seq[IndexDeclaration]](
	    "Laegemiddel" -> List(new LongIndexDeclaration("drugid", (ce: CompactEntity) => ce.getValue("drugid").asInstanceOf[Long]))
	)
	
	def getIndexAttributes(entityName: String) = declaredIndexes.getOrElse(entityName, List())
	
}

abstract class IndexDeclaration(val name: String, val method: CompactEntity => Ordered[_]) {
  def emptyMap: TreeMap[Ordered[_], Set[CompactEntity]]
}

class LongIndexDeclaration(name: String, method: CompactEntity => Ordered[_]) extends IndexDeclaration(name, method) {
  
  //wrrr can make this line work without hacking the type systen, it should be possible!!
  override def emptyMap = TreeMap[Long, Set[CompactEntity]]().asInstanceOf[TreeMap[Ordered[_], Set[CompactEntity]]]
}

class Pricelist(es: Map[String, Set[CompactEntity]], previousPricelist: Option[Pricelist]) {
  val entities: Map[String, Entities] = setup(es, previousPricelist)

  //could not make the foldleft work without putting it inside the setup function
  //dig into this later
  def setup(es: Map[String, Set[CompactEntity]], previousPricelist: Option[Pricelist]): Map[String, Entities] = {
    es.foldLeft(Map[String, Entities]()) {
		(map, tuple) =>
			val e = previousPricelist match {
			  case Some(p) => p.entities.get(tuple._1)
			  case None => None
			}
			map + (tuple._1 -> new Entities(tuple._2, Pricelist.getIndexAttributes(tuple._1), e))
	}
  }
}

class Entities(entities: Set[CompactEntity], indexDeclarations: Seq[IndexDeclaration], previous: Option[Entities]) {
	
	val indexes: Map[String, Index] = setup(entities, indexDeclarations, previous)
	
	//could not make the foldleft work without putting it inside the setup function
	//dig into this later
	def setup(entities: Set[CompactEntity], indexDeclarations: Seq[IndexDeclaration], previous: Option[Entities]): Map[String, Index] = {
		indexDeclarations.foldLeft(Map[String, Index]()) {
			(map, indexDeclaration) => 
			  val index = previous match { //TODO this can be done smarter maybe monadic to avoid the some none stuff
			    case Some(p) => p.indexes.get(indexDeclaration.name)
			    case None => None 
			  }
			  map + (indexDeclaration.name -> new Index(entities, indexDeclaration, index))
		}
	}
}

class Index(entities: Set[CompactEntity], indexDeclaration: IndexDeclaration, previous: Option[Index]) {
  
  val map: TreeMap[Ordered[_], Set[CompactEntity]] = Index.createIndexFromPrevious(entities, indexDeclaration, previous)
  
}

object Index {
  
  type IndexMap = TreeMap[Ordered[_], Set[CompactEntity]]

  private def createIndexFromPrevious[IndexType](entities: Set[CompactEntity], indexDeclaration: IndexDeclaration, previous: Option[Index]) : IndexMap = {
	
    val initialMap: IndexMap = previous match {
      case Some(p) => p.map
      case None => indexDeclaration.emptyMap
    }
    
    val previousEntitySet = initialMap.foldLeft(Set[CompactEntity]()) {
		(set, tuple) => set ++ tuple._2
	}
    val map = removeDeleted(entities, initialMap, previousEntitySet)
    addNewEntries(entities, indexDeclaration, previousEntitySet, map)
  }
  
  private def addNewEntries(entities: Set[CompactEntity], indexDeclaration: IndexDeclaration,
      previousEntitySet: Set[CompactEntity], map: IndexMap) : IndexMap = {
    val newEntities = entities -- previousEntitySet
    newEntities.foldLeft(map) {
      (map, entity) => 
        val key = indexDeclaration.method(entity)
        val set = map.getOrElse(key, Set())
        map + (key -> (set + entity))
    }
  }
  
  private def removeDeleted(entities: Set[CompactEntity], map: IndexMap, 
      previousEntitySet: Set[CompactEntity]) : IndexMap = {
	val deletedEntities = previousEntitySet -- entities
    
    map.foldLeft(map) {
      (map, tuple) => 
        val key: Ordered[_] = tuple._1
        val set = tuple._2
        val newSet = set -- deletedEntities
        if (newSet != set) {
          map + (key -> newSet)
        } else map
    }
  } 
  
}