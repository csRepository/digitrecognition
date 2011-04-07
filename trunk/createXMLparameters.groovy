import groovy.xml.MarkupBuilder
import groovy.xml.*
import javax.xml.parsers.DocumentBuilderFactory
import groovy.xml.dom.DOMCategory

    def root = new XmlSlurper().parse("parameters.xml")
    def network = root.network
    def i = network.parameters.hidden.toInteger()
    def algorithm = network.parameters.algorithm.type
    String algId = ""
    switch( algorithm ) {
  	case 'BackPropagation'     : algId = "bp"; break;
	case 'ResilentPropagation' : algId = "rp"; break;
	case 'QuickPropagation'    : algId = "qp"; break;
	default: break;
    }
    
    for (int j=1; j<=25; j++) {
	if (j<10) jnr = "0";
	else jnr = "";
	if (i<100) inr = "0";
	else inr = "";

	String outdir = algId + "/whole_data/validate/300neurons/weight_decay_1e-5/"   // <------------------nazwa badania----------

    	network.parameters.weights_file = "weights/" + outdir + algId + "_" + inr + i + "_" + jnr + j +".dat"	//plik do zapisu wag

    	def outputBuilder = new StreamingMarkupBuilder()
    	String result = XmlUtil.serialize(outputBuilder.bind{ mkp.yield root })
    	
	/*---------------tworzenie plikow-------------------------------*/
    	def fw = null
    	try {
            File dir = new File("config/" + outdir);
	        dir.mkdirs();
            fw = new OutputStreamWriter(new FileOutputStream(dir.getPath() + "/" + algId + "_" + inr + i + "_" + jnr + j + ".xml"))
            fw << result
    	} finally {
            fw?.close()
    	}
    }








