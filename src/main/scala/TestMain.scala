import scala.collection.immutable.TreeMap
object TestMain extends Application{
	new TestMain()
}

class TestMain {
  
  var map = TreeMap[Long, String]() .asInstanceOf[TreeMap[Ordered[_], String]]
  val myLong: Long = 5;
  map += (myLong.asInstanceOf[Ordered[_]] -> "hej")
  map += (myLong.asInstanceOf[Ordered[_]] -> "dsgfdf")
  
  map += (myLong.asInstanceOf[Ordered[_]] -> "adasd")
  
  println(map)
  
}