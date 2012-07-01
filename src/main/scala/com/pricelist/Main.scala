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
	  val dir = new File("data/takst/20120101")
	  val pricelist = Loader.loadPricelist(dir, None)
	  val index = pricelist.map("Laegemiddel")("drugid")
	  println(index == 28103666304L)
	}

	def allPricelists() {
	  val dir = new File("data/takst")
	  val pricelists = Loader.loadPricelists(dir)
	  println("DONE " + pricelists)
	}
	
}