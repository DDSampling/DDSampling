package ddsampling;
/**
 * @author 
 *
 */

public class InfoTrans {
	public Integer idNoeud;
	public Integer taille;
	public int somTaille;
	public int parcourue;
	
	public InfoTrans() {
		this.taille = 0;
		this.somTaille = 0;
		this.parcourue = 0;
	}
	
	public int getParcourue() {
		return parcourue;
	}
	public void setParcourue(int parcourue) {
		this.parcourue = parcourue;
	}
	public Integer getIdNoeud() {
		return idNoeud;
	}
	public void setIdNoeud(Integer idNoeud) {
		this.idNoeud = idNoeud;
	}
	public Integer getTaille() {
		return taille;
	}
	public void setTaille(Integer taille) {
		this.taille = taille;
	}
	public int getSomTaille() {
		return somTaille;
	}
	public void setSomTaille(int somTaille) {
		this.somTaille = somTaille;
	}
}
