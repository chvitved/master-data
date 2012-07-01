import scala.collection.immutable.TreeMap
import com.pricelist.CompactEntity
import com.pricelist.Pricelist
object TestMain extends Application{
	new TestMain()
}

class TestMain {
  val l1 = 4L
  val l2 = 44L
  val l3 = 444L
  val e1 = CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l1, "b"-> "b"))
  val e2 = CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l2, "b"-> "bb"))
  val es: Set[CompactEntity] = Set(e1,e2)
  
  
  val res = Pricelist(Map[String, Set[CompactEntity]]("Laegemiddel" -> es), None)
  println("res " + res)
  
  val index = res.map("Laegemiddel")("drugid")
  val index2 = index + CompactEntity("Laegemiddel", Map[String, Any]("drugid" -> l3, "b"-> "bbb"))
  
  println(index === l1)
  
}