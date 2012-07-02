package com.pricelist

import dk.trifork.sdm.importer.takst.TakstImporter
import dk.trifork.sdm.importer.takst.model.Takst
import dk.trifork.sdm.importer.takst.model.Laegemiddel
import java.io.File
import java.io.FilenameFilter
import dk.trifork.sdm.importer.takst.model.TakstEntity

object Main extends App {
	
	var pricelists = List[Seq[CompactEntity]]()

	run()
	
	def run() {
	  allPricelists()
	}

	def allPricelists() {
	  val dir = new File("data/takst")
	  val pricelists = Loader.loadPricelists(dir)
	  println("DONE " + pricelists)
	}
	
	def onePricelist() {
	  val dir = new File("data/takst/20081103")
	  val pricelist = Loader.loadPricelist(dir, None)
	  val index = pricelist.map("Laegemiddel")("drugid")
	  println("fetching: " + (index === 28101906797L))
	  val indexOnName = pricelist.map("Laegemiddel")("navn")
	  //indexOnName.map.range("a", "b").values.flatten.foreach{ce: CompactEntity => println(ce.get("navn"))}
	  //indexOnName.map.range("A", "B").values.flatten.foreach{ce: CompactEntity => println(ce.get("navn"))}
	  (indexOnName === "Ancozan Comp").foreach(println(_))
	  
	  {
	  val t1 = System.currentTimeMillis()
	  val res = indexOnName.map.range("Pan", "Pao").values.flatten
	  val t2 = System.currentTimeMillis()
	  println("time " + (t2-t1) + " millis. res " + res.size)
	  }
	  
	  {
	  val t1 = System.currentTimeMillis()
	  val res = indexOnName.map.range("Ven", "Veo").values.flatten
	  val t2 = System.currentTimeMillis()
	  println("time " + (t2-t1) + " millis. res " + res.size)
	  }
	}
	
}