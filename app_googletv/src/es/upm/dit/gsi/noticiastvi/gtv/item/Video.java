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
 * A POJO of a video
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class Video implements Item, Serializable{
	
	private static final long serialVersionUID = -456468045140639434L;
		
    private String nombre;
    private String video;
    private String captura;
    private String contenido;
    private String autor;
    private int id;
    private String fecha;
    private int have;
    //private Type type;
    
 
	public Video() {}
	
	public Video(String nombre, String autor , String contenido, String video, String captura) {
		this.nombre = nombre;
		this.autor = autor;
		this.video = video;
		this.captura = captura;
		this.contenido = contenido;
		//this.type = Type.VIDEO;
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
		return captura;
	}

	@Override
	public void setCaptura(String captura) {
		this.captura = captura;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public int getHave() {
		return have;
	}

	public void setHave(int have) {
		this.have = have;
	}


}
