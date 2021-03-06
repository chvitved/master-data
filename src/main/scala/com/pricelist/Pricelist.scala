package com.pricelist
import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedMap

object Pricelist {

  val DeclaredIndex = setupIndexes(
	    declare[String]("Administrationsvej", "kode"),
	    declare[String]("ATCKoderOgTekst", "tekst"),
	    declare[String]("Beregningsregler", "kode"),
	    declare[Long]("Dosering", "doseringKode"),
	    declare[Long]("Doseringskode", "drugid"),
	    declare[String]("EmballagetypeKoder", "kode"),
	    declare[Long]("Enhedspriser", "varenummer"),
	    declare[Long]("Firma", "firmanummer"),
	    declare[Long]("Indholdsstoffer", "drugID"),
	    declare[Long]("Indikation", "indikationskode"),
	    declare[Long]("Indikationskode", "drugID"),
	    declare[String]("Klausulering", "kode"),
	    declare[Long]("Laegemiddel", "drugid"),
	    declare[String]("Laegemiddel", "navn"),
	    declare[Long]("LaegemiddelAdministrationsvejRef", "drugId"),
	    declare[String]("LaegemiddelformBetegnelser", "kode"),
	    declare[Long]("Laegemiddelnavn", "drugid"),
	    declare[String]("Medicintilskud", "kode"),
	    declare[String]("Opbevaringsbetingelser", "kode"),
	    declare[Long]("OplysningerOmDosisdispensering", "drugid"),
	    declare[Long]("Pakning", "drugid"),
	    declare[Long]("Pakningskombinationer", "varenummerOrdineret"),
	    declare[Long]("PakningskombinationerUdenPriser", "varenummerOrdineret"),
	    //declare[...]("Pakningsstoerrelsesenhed", "enheder"),
	    declare[Long]("Priser", "varenummer"),
	    declare[Long]("Rekommandationer", "varenummer"),
	    declare[String]("SpecialeForNBS", "kode"),
	    //declare[...]("Styrkeenhed", "enheder"),
	    declare[Long]("Substitution", "substitutionsgruppenummer"),
	    declare[Long]("SubstitutionAfLaegemidlerUdenFastPris", "varenummer"),
	    //declare[Long]("Takst", "substitutionsgruppenummer"), I dont think we should load this one
	    //declare[Long]("Tilskudsintervaller", "type + niveau"),
	    declare[Long]("TilskudsprisgrupperPakningsniveau", "varenummer"),
	    declare[Long]("UdgaaedeNavne", "drugid"),
	    declare[String]("Udleveringsbestemmelser", "kode")
    )
    
    val a = classOf[String]
  
  private def setupIndexes(tuples: (String, String, DeclaredIndex[_])*) : Map[String, Map[String,DeclaredIndex[_]]] = {
    tuples.foldLeft(Map[String, Map[String,DeclaredIndex[_]]]()) {
      (map, indexdeclaration) => 
        val entityName = indexdeclaration._1
        val attributeName = indexdeclaration._2
        val declaredIndex = indexdeclaration._3
        val innerMap = map.getOrElse(entityName, Map[String,DeclaredIndex[_]]())
        map + (entityName -> (innerMap + (attributeName -> declaredIndex)))
    }
  }
  
  private def declare[T<%Ordered[T]](entityName: String, attributeName: String): (String, String, DeclaredIndex[_]) = {
	  def indexMethod[T] = (ce: CompactEntity) => ce.get.getOrElse(attributeName, null).asInstanceOf[T]
	  (entityName, attributeName, new DeclaredIndex[T](attributeName, indexMethod))
  }
  
  class DeclaredIndex[IndexType<%Ordered[IndexType]](val name: String, indexMethod: CompactEntity => IndexType) {
	//TODO should be possible without hacking the type system  
    def emptyIndex: Index[Ordered[_]] = {
     (new Index(TreeMap[IndexType, Set[CompactEntity]](), indexMethod)).asInstanceOf[Index[Ordered[_]]] 
    }
  }
  
  def apply(es: Map[String, Set[CompactEntity]], previousPricelist: Option[Pricelist]): Pricelist = {
    val map = DeclaredIndex.foldLeft(Map[String, Map[String, Index[Ordered[_]]]]()) {
      (map, tuple) =>
        val entityName = tuple._1
        val DeclaredIndex = tuple._2
        val entityIndexMap = DeclaredIndex.foldLeft(Map[String, Index[Ordered[_]]]()) {
          (map, tuple) =>
            val indexName = tuple._1
            val indexDeclaration = tuple._2
            val previousIndex: Option[Index[Ordered[_]]] = previousPricelist match {
              case Some(p) if(p.map.contains(entityName) && p.map(entityName).contains(indexName)) => 
                Some(p.map(entityName)(indexName).asInstanceOf[Index[Ordered[_]]]) //type hack...should be avoidable
              case _ => None
            }
            if (es.contains(entityName)) {
            	map + (indexName -> createIndex(es(entityName), indexDeclaration, previousIndex))
            } else map
        }
        map + (entityName -> entityIndexMap)
    }
    new Pricelist(map.asInstanceOf[Map[String, Map[String, Index[Any]]]]) //TODO type hack
  }
  
  private def createIndex(entities: Set[CompactEntity], declaredIndex: DeclaredIndex[_], previous: Option[Index[Ordered[_]]]): Index[Ordered[_]] = {
    val index: Index[Ordered[_]] = previous match {
      case Some(pi) => pi
      case None => declaredIndex.emptyIndex
    }
    val previousEntities = index.entities
    
    val deletedEntities = previousEntities -- entities
    val newEntities = entities -- previousEntities
    println("Entity: " + entities.first.name + " Number of entities " + entities.size + " Deleted: " + deletedEntities.size + " Added: " + newEntities.size)
    (index -- deletedEntities) ++ newEntities 
  }
}

class Pricelist private(val map: Map[String, Map[String, Index[Any]]])

