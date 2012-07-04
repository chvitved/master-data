package com.pricelist

object CompactEntityPool {
  
  var allEntities = scala.collection.mutable.Map[CompactEntity, CompactEntity]()
  
  def intern(e: CompactEntity): CompactEntity = {
    allEntities.get(e) match {
      case Some(internalEntity) => {
        internalEntity 
      }
      case None => {
        allEntities += e -> e
        e
      }
    }
  }
}