package com.pricelist
import java.util.Arrays

object CompactEntity {
  
	var map = Map[String, Map[String, Int]]()
	var reverseMap = Map[String, Map[Int, String]]()
		
	def apply(entityName: String, entityAsMap: Map[String, Any]): CompactEntity = {
	  if (entityAsMap.keys.size > 32) {
	    throw new Exception("We use an integer as bitmap and cannot store entities with more than 32 values")
	  }
	  val eName = entityName.intern()
      val indexValueTuple = for((name, value) <- entityAsMap) yield {
        val eMap = map.getOrElse(eName, Map())
        eMap.get(name) match {
          case Some(index) => (index, value)
          case None => {
            val index = ValuePool.intern(eMap.size)
            val newMap = eMap + (name.intern -> index)
            map = map + (eName -> newMap)
            val newReverseMap = reverseMap.getOrElse(eName, Map()) + (index -> name)
            reverseMap = reverseMap + (eName -> newReverseMap)
            (index, value)
          }
        }
      }
      createCompactEntity(eName, indexValueTuple)
	}
	
	private def createCompactEntity(name: String, values: Map[Int, Any]) = {
		val indexBitmap = values.map(_._1).foldLeft(0){(acc, index) => acc | (1 << index)} 
		val sortedValues = values.toArray.sortWith{(v1, v2) => v1._1 < v2._1}.map(_._2)
		val svInterned = sortedValues.map(ValuePool.intern(_))
		val ce = new CompactEntity(name, ValuePool.intern(indexBitmap), svInterned)
		CompactEntityPool.intern(ce)
	}
	
	private def get(ce: CompactEntity): Map[String, Any] = {
	  val indexes = (0 to 31).foldRight(List[Int]()) {(index, indexList) => 
	    if (containsIndex(ce, index)) {
	      index :: indexList
	    } else indexList
	  }

	  val indexMap = reverseMap(ce.name) 
	  Map(indexes.map(indexMap(_)).zip(ce.valueArray):_*)
	}
	
	private def containsIndex(ce: CompactEntity, index: Int): Boolean = {
	  (ce.indexBitmap & (1 << index)) > 0
	}
}

class CompactEntity private(val name: String, private val indexBitmap: Int, private val valueArray: Array[Any]) {
  
  def get : Map[String, Any] = {
    CompactEntity.get(this)
  }
  
  //TODO stupid impl....make efficient
  def getValue(attribute: String) = get(attribute)
  
  override def toString() = get.toString
  
  override def hashCode(): Int = {
		val prime = 31;
		var result = 1;
		result = prime * result + indexBitmap;
		result = prime * result + Arrays.hashCode(valueArray.asInstanceOf[Array[Object]])
		val number: Int = if (name eq null) 0 else name.hashCode()
		result = prime * result + number 
		return result;
	}
	override def equals(obj: Any): Boolean = {
		val anyRef = obj.asInstanceOf[AnyRef]
		if (this eq anyRef)
			return true;
		if (anyRef eq null)
			return false;
		if (!(getClass() eq obj.getClass()))
			return false;
		val other = obj.asInstanceOf[CompactEntity];
		if (indexBitmap != other.indexBitmap)
			return false;
		if (!(Arrays.equals(valueArray.asInstanceOf[Array[Object]], other.valueArray.asInstanceOf[Array[Object]])))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!(name == other.name))
			return false;
		return true;
	}
  
}
