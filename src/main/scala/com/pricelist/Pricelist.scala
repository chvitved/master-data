package com.pricelist
import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedMap

object Pricelist {

  val declaredIndexes = Map[String, Map[String,DeclaredIndexes[_]]](
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
  
  private def declare[T<%Ordered[T]](entityName: String, attributeName: String) = {
	  entityName -> Map[String,DeclaredIndexes[_]](attributeName -> new DeclaredIndexes[T](attributeName, (ce) => ce.get(attributeName).asInstanceOf[T]))
  }
  
  class DeclaredIndexes[IndexType<%Ordered[IndexType]](val name: String, indexMethod: CompactEntity => IndexType) {
	//TODO should be possible without hacking the type system  
    def emptyIndex: Index[Ordered[_]] = {
     (new Index(TreeMap[IndexType, Set[CompactEntity]](), indexMethod)).asInstanceOf[Index[Ordered[_]]] 
    }
  }
  
  def apply(es: Map[String, Set[CompactEntity]], previousPricelist: Option[Pricelist]): Pricelist = {
    val map = declaredIndexes.foldLeft(Map[String, Map[String, Index[Ordered[_]]]]()) {
      (map, tuple) =>
        val entityName = tuple._1
        val declaredIndexes = tuple._2
        val entityIndexMap = declaredIndexes.foldLeft(Map[String, Index[Ordered[_]]]()) {
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
    new Pricelist(map)
  }
  
  private def createIndex(entities: Set[CompactEntity], declaredIndex: DeclaredIndexes[_], previous: Option[Index[Ordered[_]]]): Index[Ordered[_]] = {
    val index: Index[Ordered[_]] = previous match {
      case Some(pi) => pi
      case None => declaredIndex.emptyIndex
    }
    val previousEntities = index.entities
    val deletedEntities = previousEntities -- entities
    val newEntities = entities -- previousEntities
    (index -- deletedEntities) ++ newEntities 
  }
}

class Pricelist private(val map: Map[String, Map[String, Index[Ordered[_]]]])

