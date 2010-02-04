import groovy.xml.MarkupBuilder
import groovy.xml.*
import javax.xml.parsers.DocumentBuilderFactory
import groovy.xml.dom.DOMCategory





def istart=10;//do podzialu na katalogi
def k = 1;
for (int i=10; i <= 300; i+=10) {
    def network = new XmlSlurper().parse("parameters.xml")
    if (i == istart+30) {istart=i; k++;} 
    
    network.parameters.hidden = i
    def algorithm = network.parameters.algorithm 
    def algId = network.algorithm.find { it.name == algorithm }.@id
    def parameters = network.algorithm.find { it.name == algorithm }.parameter
    parameters.collect {it.toDouble()}
    //String s = "_"
    //for (int w=0; w<parameters.size();w++){
    //	s += parameters[w];
    //	s += "_";}
    
    for (int j=1; j<=30; j++) {
	if (j<10) jnr = "0";
	else jnr = "";
	if (i<100) inr = "0";
	else inr = "";

	String outdir = algId.text() + "/30epoch/"// <------------------nazwa badania----------

    	network.parameters.weights_file = "weights/" + outdir + algId + "_" + inr + i + "_" + jnr + j +".dat"	//plik do zapisu wag

    	def outputBuilder = new StreamingMarkupBuilder()
    	String result = XmlUtil.serialize(outputBuilder.bind{ mkp.yield network })
    	
	/*---------------tworzenie plikow-------------------------------*/
    	def fw = null
    	try {
            File dir = new File("config/" + outdir + algId + k);
	        dir.mkdirs();
            fw = new OutputStreamWriter(new FileOutputStream(dir.getPath() + "/" + algId + "_" + inr + i + "_" + jnr + j + ".xml"))
            fw << result
    	} finally {
            fw?.close()
    	}
    }
}








