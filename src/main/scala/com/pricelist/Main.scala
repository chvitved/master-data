package com.pricelist

import dk.trifork.sdm.importer.takst.TakstImporter
import dk.trifork.sdm.importer.takst.model.Takst
import dk.trifork.sdm.importer.takst.model.Laegemiddel
import scala.collection.JavaConversions._
import java.io.File
import java.io.FilenameFilter
import dk.trifork.sdm.importer.takst.model.TakstEntity

object Main extends App {
	
	type Pricelist = Seq[CompactEntity]
	var pricelists = List[Pricelist]()

	run()
	
	def run() {
		printMem()
		val rootDir = new File("data/takst")
		val pricelistDirs = rootDir.listFiles(new FilenameFilter() {
		  def accept(file: File, name: String) = file.isDirectory && !name.startsWith(".")
		})
		
		var counter = 1
		for(pDir <- pricelistDirs) {
		  println("loading pricelist " + counter)
		  val pricelist = loadPricelist(pDir)
		  pricelists = pricelist :: pricelists 
		  printMem()
		  counter += 1
		}
	}
	
		
	def loadPricelist(dir: File) : Pricelist = {
		val pricelist = TakstImporter.importTakst(dir)
		val pricelistElems = pricelist.getDatasets().map(_.getEntities())
		val pricelistElements = for(seq <- pricelistElems; e <- seq) yield e //flatten
		ObjectParser(pricelistElements)
	}
	
	def printMem() = {
		System.gc();
		val r = Runtime.getRuntime();
		val mem = (r.totalMemory() - r.freeMemory()) / (1024*1024); //megabytes
		println("used mem: " + mem);
	}

}