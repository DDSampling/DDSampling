package ddsampling;
import java.util.*;

/**
 * @author 
 *
 */


public class VertexStructure {
	public Hashtable<String, List<String>> baseItemsets;

	public VertexStructure() {
		this.baseItemsets = new Hashtable<String, List<String>>();
	}
	
	public VertexStructure(Hashtable<String, List<String>> baseItemsets) {
		this.baseItemsets = baseItemsets;
	}

	public Hashtable<String, List<String>> getBaseItemsets() {
		return baseItemsets;
	}
	
	/**
	 * @param baseItemsets the baseItemsets to set
	 */
	
	public void setBaseItemsets(Hashtable<String, List<String>> baseItemsets) {
		this.baseItemsets = baseItemsets;
	}
	
	// Les deux operateurs de base
	
	public String itemAt(String keyTrans , Integer i ) {
		return baseItemsets.get(keyTrans).get(i);
	}
	
	public Integer sizeOf(String keyTrans) {
		return baseItemsets.get(keyTrans).size();
	}
	
	public int size() {
		return baseItemsets.size();
	}
}
