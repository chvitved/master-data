package com.pricelist

object CompactEntityPool {
  
  type Entity = Map[String, Any]
  
  var allEntities = Map[CompactEntity, CompactEntity]()

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