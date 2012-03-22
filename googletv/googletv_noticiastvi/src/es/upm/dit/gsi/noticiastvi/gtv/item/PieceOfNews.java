//   Copyright 2011 UPM-GSI: Group of Intelligent Systems -
//   - Universidad Politécnica de Madrid (UPM)
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package es.upm.dit.gsi.noticiastvi.gtv.item;

import java.io.Serializable;


/**
 * A POJO of a piece of news
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class PieceOfNews implements Item, Serializable{
	
		
	private static final long serialVersionUID = 4844425836589756999L;
	
	private String nombre;
    private String video;
    private String imagen;
    private String contenido;
    private String autor;
   // private Type type;

	public PieceOfNews() {}
	
	public PieceOfNews(String nombre, String autor , String contenido, String video, String imagen) {
		this.nombre = nombre;
		this.autor = autor;
		this.video = video;
		this.imagen = imagen;
		this.contenido = contenido;
//		this.type = Type.PIECEOFNEWS;
	}
	
	public int getId() {
		return 1;
	}

	public void setId(int id) {
//		this.id = id;
	}


	@Override
	public String getNombre() {
		return nombre;
	}

	@Override
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String getVideo() {
		return video;
	}

	@Override
	public void setVideo(String video) {
		this.video = video;
	}

	@Override
	public String getCaptura() {
		return imagen;
	}

	@Override
	public void setCaptura(String captura) {
		this.imagen = captura;
	}

	@Override
	public String getContenido() {
		return contenido;
	}

	@Override
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	@Override
	public String getAutor() {
		return autor;
	}

	@Override
	public void setAutor(String autor) {
		this.autor = autor;
	}
//
//	@Override
//	public Type getType() {
//		return type;
//	}
//
//	@Override
//	public void setType(Type type) {
//		this.type = type;
//		
//	}

	@Override
	public String getFecha() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFecha(String fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHave() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHave(int have) {
		// TODO Auto-generated method stub
		
	}


}
