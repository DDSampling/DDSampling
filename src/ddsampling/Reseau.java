package ddsampling;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 
 *
 */

public class Reseau {
	//******************
	Integer nbSitesEnPannes = 0; // nombre de sites pas disponibles
	//******************
	Float p=(float) 0.0; //Probabilite aqu'un noeud ne soit pas accessible
	//******************
	Integer nbSites = 0; //Nombre total de sites;
	//******************
	String nomBase =""; //nom de la base de données distribuées
	//*******************
	String typePartitionnement = "VL"; // HL(horizontal), VL(vertical), HD(hybride)
	//*******************
	Hashtable<String, List<String>> baseItemsets;
    Hashtable<Integer, VertexStructure> system;
	
	//Chargement des bases en local de chaque site
	 final Pattern separator = Pattern.compile("[\t ,]");
	
    public Reseau(String nomBase, String typePartitionnement, Integer nbSitesEnPannes, Float p, Integer nbSites) throws IOException{
    	this.nbSitesEnPannes = nbSitesEnPannes;
		this.p = p;
		this.nbSites = nbSites;
		this.nomBase = nomBase;
		this.typePartitionnement = typePartitionnement;
		this.system = new Hashtable<Integer, VertexStructure>();
		String cheminBases = "dataSplited/"+this.typePartitionnement+"/"+this.nomBase+"/"+this.typePartitionnement+this.nomBase; //args[2];
		   for(Integer n=1; n<=this.nbSites; n++){
			BufferedReader lecteurAvecBuffer = null;
			String ligne;
			VertexStructure vertexValue=new VertexStructure();
			baseItemsets=new Hashtable<String, List<String>>();
			String nomBaseItemsets=cheminBases+n.toString()+".MDS";
			try{
				lecteurAvecBuffer = new BufferedReader(
						new FileReader(nomBaseItemsets));
			}
			catch(FileNotFoundException exc){
				System.out.println("Erreur d'ouverture"+exc.toString());
			}
			String key=null;
			while ((ligne = lecteurAvecBuffer.readLine()) != null){
				String[] infoItems=ligne.split(" ; ");
				key=infoItems[0];
				String [] items= separator.split(infoItems[1]);
				List<String> itemset = new ArrayList<String>();
				for(int i=0;i<items.length;i++) itemset.add(items[i]);
				baseItemsets.put(key,itemset);
			}
			vertexValue.setBaseItemsets(baseItemsets);
			this.system.put(n, vertexValue);
		}
	}

	public Integer getNbSitesEnPannes() {
		return nbSitesEnPannes;
	}

	public void setNbSitesEnPannes(Integer nbSitesEnPannes) {
		this.nbSitesEnPannes = nbSitesEnPannes;
	}

	public Float getP() {
		return p;
	}

	public void setP(Float p) {
		this.p = p;
	}

	public Integer getNbSites() {
		return nbSites;
	}

	public void setNbSites(Integer nbSites) {
		this.nbSites = nbSites;
	}

	public String getNomBase() {
		return nomBase;
	}

	public void setNomBase(String nomBase) {
		this.nomBase = nomBase;
	}

	public String getTypePartitionnement() {
		return typePartitionnement;
	}

	public void setTypePartitionnement(String typePartitionnement) {
		this.typePartitionnement = typePartitionnement;
	}

	public Hashtable<String, List<String>> getBaseItemsets() {
		return baseItemsets;
	}

	public void setBaseItemsets(Hashtable<String, List<String>> baseItemsets) {
		this.baseItemsets = baseItemsets;
	}

	public Hashtable<Integer, VertexStructure> getSystem() {
		return system;
	}

	public void setSystem(Hashtable<Integer, VertexStructure> system) {
		this.system = system;
	}
    
    
    
}
