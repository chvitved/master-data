package com.pricelist
import java.lang.reflect.Modifier
import java.lang.reflect.Field
import java.util.{Date, Calendar}
import dk.trifork.sdm.importer.takst.model.DivEnheder

object ObjectParser {
  
	val types = Set[Class[_]](
	    classOf[String], classOf[Number], classOf[Date], classOf[java.lang.Boolean],
	    classOf[Calendar], classOf[DivEnheder]
	    
	)
  
	var warnFields = Set[Field]();
  
  	def apply(objects: Seq[AnyRef]): Seq[CompactEntity] = {
  	  objects.map(apply(_))
	}

  	def apply(o: AnyRef): CompactEntity = {
	  val clas = o.getClass
	  val fs = clas.getDeclaredFields
	  fs.foreach(_.setAccessible(true))
	  val fields = fs.filter{f => !Modifier.isStatic(f.getModifiers())}
	  
	  val map = fields.foldLeft(Map[String, AnyRef]()){(map, f) => 
	    val v = f.get(o)
	    if (v != null && canParseValue(v)) {
	    	map + (f.getName() -> v)
	    } else {
	      if (v != null  && !warnFields.contains(f)) {
	        warnFields += f
	    	println("NOT saving " + f)
	    	println("value " + v)
	    	println("class " +v.getClass())
	      }
	      map
	    }
	  }
	  CompactEntity(clas.getName, map)
	}
  	
  	def canParseValue(value: Any) = {
  	  types.exists(_.isAssignableFrom(value.getClass))
  	}

}