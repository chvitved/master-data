import scala.collection.immutable.TreeMap
import com.pricelist.NewIndex
import com.pricelist.CompactEntity
import com.pricelist.NewPricelist
object TestMain extends Application{
	new TestMain()
}

class TestMain {
  val l1 = new java.lang.Long(4)
  val l2 = new java.lang.Long(44)
  val l3 = new java.lang.Long(444)
  val e1 = CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l1, "b"-> "b"))
  val e2 = CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l2, "b"-> "bb"))
  val es: Set[CompactEntity] = Set(e1,e2)
  
  
  val res = NewPricelist(Map[String, Set[CompactEntity]]("Laegemiddel" -> es), None)
  println("res" + res)
  
//  val index = NewIndex.create(es, (es: CompactEntity) => es.get("a").asInstanceOf[Long])
//  
//  val index2 = index + CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l3, "b"-> "bbb"))
//  
//  println(index2)
  
}