package com.pricelist
import java.util.Date
import java.util.Calendar
import dk.trifork.sdm.importer.takst.model.DivEnheder

object ValuePool {

  
  var allValues = Map[Any, Any]()
  
  val types = Set[Class[_]](
	    classOf[Int], classOf[Boolean], classOf[Date],
	    classOf[Calendar], classOf[DivEnheder]
	)
  
  def intern[T](v: T): T = {
    if (v.isInstanceOf[String]) {
      v.asInstanceOf[String].intern().asInstanceOf[T]
    } else if (shouldPoolValue(v)) {
    	//interesting what makes sence o pool
    	//does it make sense to pool longs?
    	//measure this on existing pricelists
    	add(v)
    } else v 
    	
  }
  
  def shouldPoolValue(value: Any) = {
	  types.exists(_.isAssignableFrom(value.getClass))
  }
  
  private def add[T](v: T): T = {
    allValues.get(v) match {
      case Some(value) => value.asInstanceOf[T]
      case None => {
        allValues += v -> v
        v
      }
    }
  }
}