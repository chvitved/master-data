package com.pricelist

import java.io.File
import dk.trifork.sdm.importer.takst.TakstImporter
import java.io.FilenameFilter

import scala.collection.JavaConversions._

object Loader {

	def loadPricelists(rootDir: File) : Seq[Pricelist] = {
		val pricelistDirs = rootDir.listFiles(new FilenameFilter() {
			def accept(file: File, name: String) = file.isDirectory && !name.startsWith(".")
		})
		
		var counter = 1
		var previous: Option[Pricelist] = None 
		pricelistDirs.foldLeft(List[Pricelist]()){(list, dir) =>
		  println("loading pricelist " + counter)
			val pricelist = loadPricelist(dir, previous) 
			counter += 1
			previous = Some(pricelist)
			printMem()
		  	printPools()
		  	pricelist :: list
		}
	}

	def loadPricelist(dir: File, previous: Option[Pricelist]) : Pricelist = {
		val pricelistElems = TakstImporter.importTakst(dir).getDatasets()
		
		val pricelistMap = pricelistElems.foldLeft(Map[String, Set[CompactEntity]]()) {
		  (map, elements) => 
		    val entities = ObjectParser(elements.getEntities().toSet)
		    if (entities.isEmpty) {
		      map
		    } else { 
		    	val name = entities.elements.next().name
		    	map + (name -> entities)
			}
		}
		Pricelist(pricelistMap, previous)
	}
	
	private def printMem() {
		System.gc();
		val r = Runtime.getRuntime();
		val mem = (r.totalMemory() - r.freeMemory()) / (1024*1024); //megabytes
		println("used mem: " + mem);
	}
	
	var lastCePoolSize = 0;
	var lastValuePoolSize = 0;
	private def printPools() {
	  val newValueSize = ValuePool.allValues.size
	  val newCeSize = CompactEntityPool.allEntities.size
	  println("value pool size " + newValueSize)
	  println("value pool grown " + (newValueSize - lastValuePoolSize))
	  println("ce pool size " + newCeSize)
	  println("ce pool grown " + (newCeSize - lastCePoolSize))
	  lastCePoolSize = newCeSize
	  lastValuePoolSize = newValueSize
	}
}