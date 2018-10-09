package ddsampling;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author 
 *
 */

public class DDSamping{
	//****************** Fonction principale ********************//
	public static void main(String[] args) throws IOException {
		//******************
		String typePartitionnement = "HD"; // HL(horizontal), VL(vertical), HD(hybride)
		//*******************
		Integer N=10000; //Taille de l'échantillon
		//******************
		/* util = "Freq" si on considére la fréquence comme unitilité
		 * util = "Aire" si on considére l'aire comme unitilité
		 */
		String util = "Freq"; // "Freq" ou "Aire"
		//****************** 
		/*
		 * si util = "Aire" et bornee = true, alors on aura : u(area)*u(<=M)
		 * si util = "Freq" et bornee = true, alors on aura : u(<=M)
		 * si util = "Aire" et bornee = false, alors on aura : u(area)
		 * si util = "Freq" et bornee = false, alors on aura : u(freq)
		 */
		boolean bornee = true;
		//******************
		Integer tailleMin=1; //contrainte de taille minimale. 
							 //Mettez tailleMin=0 si vous désirez tirer l'ensemble vide
		//******************
		Integer M=3; //contrainte de taille maximale. Si bornee = false, elle fera plus sens.
		//******************
		Integer nbSitesEnPannes = 2; // nombre de sites pas disponibles
		//******************
		Float p=(float) 0.0; //Probabilite qu'un site ne soit pas accessible
		//******************
		Integer nbSites = 10; //Nombre total de sites;
		//******************
		String nomBase ="chess"; //nom de la base de données distribuées
		//*******************
		//Chargement de chaque base au site local associé
		Reseau reseau = new Reseau(nomBase, typePartitionnement, nbSitesEnPannes, p, nbSites);
		//******************
		//Remplisage de la matrice de pondération
		Hashtable<String, LinkedList<InfoTrans>>  matricePonderation= new Hashtable<String, LinkedList<InfoTrans>>();
		List<Integer> listIdNoeud=new ArrayList<Integer>();
		for(int j=1; j<= reseau.getSystem().size(); j++){
			VertexStructure vertex = reseau.getSystem().get(j);
			Enumeration<String> mesKeys = vertex.getBaseItemsets().keys();
			while (mesKeys.hasMoreElements()){
				String currentKey  = mesKeys.nextElement();
				InfoTrans info= new InfoTrans();
				LinkedList<InfoTrans> listInfo;
				if(matricePonderation.containsKey(currentKey)){
					listInfo = matricePonderation.get(currentKey);
					info.setParcourue(listInfo.getLast().getSomTaille());
				}
				else 
					listInfo = new LinkedList<InfoTrans>();
				info.setIdNoeud(j);
				info.setTaille(vertex.sizeOf(currentKey));
				info.setSomTaille(info.getParcourue()+vertex.sizeOf(currentKey));
				listInfo.addLast(info);
				matricePonderation.put(currentKey, listInfo);
			}
			listIdNoeud.add(j);
		}
		//******************
		//Pondération des lignes de la matrice
		double[] tabVal = new double[matricePonderation.size()];
		String[] tabKey=new String[matricePonderation.size()];
		Hashtable<String, Object> crible=new Hashtable<String, Object>();
		
		Enumeration<String> mesKeys = matricePonderation.keys();
		int norm;
		double val=0;
		int l=0;
		while (mesKeys.hasMoreElements()){
			String currentKey  = mesKeys.nextElement();
			norm = matricePonderation.get(currentKey).getLast().getSomTaille();
			double[] cribleS= phi(norm, tailleMin, M, util, bornee);
			crible.put(currentKey, cribleS);
			val+=sum(cribleS);
			tabVal[l]=val;
			tabKey[l]= currentKey;
			l++;
		}
		//******************
		int indice;
		List<Integer> listeIndices; //Liste des sites en pannes après pondération par simulation aléatoire
		Random rn= new Random();
		List<Integer> ind = new ArrayList<Integer>();
		for(int i=0; i< listIdNoeud.size(); i++){
			ind.add(listIdNoeud.get(i));
		}
		List<Integer> listIdVetexNotAvailable = new ArrayList<Integer>();
		for(int i=0; i< nbSitesEnPannes; i++){
			int m=rn.nextInt(ind.size());
			listIdVetexNotAvailable.add(ind.get(m));
			ind.remove(m);
		}
		//******************
		// Tirage d'un échantillon de N motifs
		Integer nbRejet=0;
		Hashtable<Integer, String> echantillon = new Hashtable<Integer, String>();
		for(Integer iter=0; iter<N; ){
			float val1=(float)(Math.random()*sommeTotale);
			int k=0, j=matricePonderation.size();
			indice=trouverBis(tabVal, k, j, val1);
			LinkedList<InfoTrans> currentTrans = matricePonderation.get(tabKey[indice].toString());
			double[] cribleS = (double[]) crible.get(tabKey[indice]);
			norm = currentTrans.getLast().getSomTaille();
			listeIndices = sousEnsemble(cribleS, norm, tailleMin);
			Integer[] tabIndiceItem = new Integer[listeIndices.size()];
			Integer[] tabIndiceVertex = new Integer[listeIndices.size()];
			for(int b=0;b<listeIndices.size(); b++){
				Integer x =listeIndices.get(b);
				for(int a=0;a<currentTrans.size(); a++){
					InfoTrans inf = currentTrans.get(a);
					if(x < inf.getSomTaille()){
						x= x-inf.getParcourue();
						tabIndiceItem[b]=x;
						tabIndiceVertex[b]=inf.getIdNoeud();
						a=currentTrans.size();
					}
				}
			}
			boolean ok=true;
			float alea=(float)Math.random();
			j=0;
			while (j<tabIndiceItem.length && ok) {
				if(listIdVetexNotAvailable.contains(tabIndiceVertex[j]) || alea <=p){
					ok=false;
				}else if(p>0) {
					alea=(float)Math.random();
				}
				j++;
			}
			if(ok){
				String pattern = "";
				for(j=0; j<tabIndiceItem.length; j++){
					pattern = pattern + reseau.getSystem().get(tabIndiceVertex[j]).itemAt(tabKey[indice], tabIndiceItem[j])+" ";
				}
				echantillon.put(iter, pattern);
				iter++;
			}else
				nbRejet++;
		}
		//******************
		// Ecriture en sortie des N motifs de l'échantillon
		String borne="";
		if(bornee)
			borne="Bornee";
		else
			borne="Non Bornee";
		BufferedWriter printerAvecBuffer = null;
		try{
			printerAvecBuffer = new BufferedWriter(
					new FileWriter("Samples/"+util+"/"+borne+"/"+reseau.getTypePartitionnement()+"/"+reseau.getNomBase()+"/"+reseau.getNomBase()+N.toString()+"N"+tailleMin.toString()+"m"+M.toString()+"M"+nbSitesEnPannes.toString()+"z"+p.toString().replace(".", "_")+"p.DDS", false));
		}
		catch(FileNotFoundException exc){
			System.out.println("Erreur d'ecriture"+exc.toString());
		}
		String output  = echantillonToString(echantillon);
		printerAvecBuffer.write(output , 0, output.length());
		printerAvecBuffer.close();
		float tauxRejet = (float)nbRejet/(nbRejet+N);
		System.out.println("Fin du tirage avec un taux de rejet de : "+ ((int)(tauxRejet*100))/100. +"\nVeuillez recupérer votre échantillon dans : \n"+"Samples/"+util+"/"+borne+"/"+reseau.getTypePartitionnement()+"/"+reseau.getNomBase()+"/"+reseau.getNomBase()+N.toString()+"N"+tailleMin.toString()+"m"+M.toString()+"M"+nbSitesEnPannes.toString()+"z"+p.toString().replace(".", "_")+"p.DDS");
	}

	//****************** Fonctions secondaires **********************//
	public static String echantillonToString(Hashtable<Integer, String> echantillon) {
		StringBuilder sb = new StringBuilder();
		
		Enumeration<?> e = echantillon.keys();
		while(e.hasMoreElements()){
			Object key = e.nextElement();
			sb.append(echantillon.get(key)+"\n");
		}	
		return sb.toString();
	}
	
	public static int trouverBis(double[] tabVal, int i, int j, float x){
		int m=(i+j)/2;
		if(m==0 || (tabVal[m-1]<x && x<=tabVal[m]))
			return m;
		if(tabVal[m]<x)
			return trouverBis(tabVal,m+1,j,x);
		return trouverBis(tabVal,i,m,x);
	}
	
	//pile ou face
	public static boolean pile(){
		Random rn= new Random();
		return (rn.nextInt(2)==1);
	}
			
	public static int k_taille(double[] cribleS){
		double[] tab=new double[cribleS.length];
		int som=0;
		for(int i=0; i<cribleS.length; i++){
			som+=cribleS[i];
			tab[i]=som;
		}
		int i=0, j=tab.length;
		float alea= (float)(Math.random()*som);
		int k=trouverBis(tab,i,j,alea);
		return k+1;
	}
	
	public static String formatStrList(List<String> s, String sep){
		return s.toString().replace("[", "").replace("]", "").replace(",",sep);
	}
	
	public static List<Integer> sousEnsemble(double[] cribleS,int norm, Integer m){
		int x=k_taille(cribleS)+Math.max(0, m-1); //Par défaut m=1 pour toutes nos xp
		List<Integer>  T=new ArrayList<Integer>();
		int i;
		int indClass=-1; // indice de la classe, pas pris en compte dans cette implémentation
		for(i=0; i<norm; i++)
			if (i != indClass) T.add(i);
		List<Integer>  X=new ArrayList<Integer>();
		Random rn= new Random();
		for(i=0;i<x;i++){
			int l=rn.nextInt(T.size());
			X.add(T.get(l));
			T.remove(T.get(l));
		}		
		return X;
	}

	//Nombre de combinaisons de n objets pris k a k
	public static double combinaison(double n, double k){
		if(k>n || n==0) return 0;
		if(k > n/2)
			k = n-k;
		double x = 1;
		double y = 1;
		double i = n-k+1;
		while(i <= n){
			x = (x*i)/y;
			y += 1;
			i += 1;
		}
		return x;
	}

	
	public static int sum(double[] cribleS){
		int som=0,i;
		for(i=0; i<cribleS.length;i++)
			som+=cribleS[i];
		return som;
	}
	
	//Nombre de sous-ensembles d'un itemset suivant une fonction d'utilité
	public static double[] phi(int tailleItemset, int m, int M, String util, boolean bornee){
		if(M==0 || tailleItemset==0 || m>M){
			double[] Tab={0};
			return Tab;
		}
		int k;
		if(bornee)
			k=Math.min(M,tailleItemset);
		else
			k=tailleItemset;
		double[] Tab= new double[k-m+1];
		if(util.equals("Aire"))
			for (int i=m, j=0; i<=k; i++, j++)
				Tab[j]=(int) combinaison(tailleItemset,i)*i;
		else if(util.equals("Freq"))
			for (int i=m, j=0; i<=k; i++, j++)
				Tab[j]=(int) combinaison(tailleItemset,i);
		else {
			System.out.println("Veillez à bien spécifier les paramétres d'entrée s'il vous plaît.");
			System.exit(0);			
		}
		return Tab;
	}
	
	//Moyenne
	public static double Moyenne(double arrayCoutCom[]) {
		double meanSum=0;
		for (int i = 0; i < arrayCoutCom.length; i++) 
			meanSum += arrayCoutCom[i]; 
	    return (double)meanSum/arrayCoutCom.length; 
	}
	
	//Ecart-type
	public static double EcartType(double mean, double arrayCoutCom[]) {
		double deviationSum=0;
		double arrayVar[] = new double[arrayCoutCom.length];
	    for (int i = 0; i < arrayCoutCom.length; i++) 
	    	arrayVar[i] = (Math.pow((arrayCoutCom[i] - mean), 2)); 
	    for (int i = 0; i < arrayCoutCom.length; i++) 
	    	deviationSum += arrayVar[i]; 
	    double variance = ((deviationSum/arrayCoutCom.length)); 
	    return ((int)(Math.sqrt(variance)*100))/100.; 
	}

}
