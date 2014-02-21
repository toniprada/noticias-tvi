package es.upm.dit.gsi.model;

import java.util.List;

public class Conjunto {
    public String dia;
	public List<Noticia> noticias;
	
	public Conjunto(){}

	public Conjunto(List<Noticia> noticias){
	    this.noticias = noticias;
	}
}
