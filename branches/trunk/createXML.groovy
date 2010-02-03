/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import groovy.xml.MarkupBuilder
import groovy.xml.*
import javax.xml.parsers.DocumentBuilderFactory
import groovy.xml.dom.DOMCategory

for (int i=30; i <= 300; i+=10) {
    def network = new XmlSlurper().parse("mainconfig.xml")
    
    network.parameters.hidden = i
    def algorithm = network.parameters.algorithm 
    def algId = network.algorithm.find { it.name == algorithm }.@id
    network.parameters.weights_file = algId + "_" + i + ".dat"	//plik do zapisu wag
    
    def outputBuilder = new StreamingMarkupBuilder()
    String result = XmlUtil.serialize(outputBuilder.bind{ mkp.yield network })
	
    /*---------------tworzenie plikow-------------------------------*/
    def fw = null
    try {
            fw = new OutputStreamWriter(new FileOutputStream("config/parameters_" + algId + "_" + i + ".xml"))
            fw << result
    } finally {
            fw?.close()
    }
    
}








