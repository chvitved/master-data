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
		val pricelists = Loader.loadPricelist(dir, None)
		val index = pricelists.entities("Laegemiddel").indexes("drugid")
		val res = index.map(28100734576L)
		println(res)
	}
	
	
}