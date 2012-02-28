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

/**
 * An interface for videos, news, etc
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public interface Item{
		
	public static final String EXTRA = "es.upm.dit.gsi.noticiastvi.gtv.Item.item";
	public static final String TITLE = "title";
	public static final String SUBTITLE = "subtitle";
	public static final String DESCRIPTION = "description";
	public static final String URL = "url";
	public static final String THUMB = "thumb";
	//public static final String type = "type";


	public String getNombre();

	public void setNombre(String nombre);
	
	public String getVideo();
	
	public void setVideo(String video);

	public String getCaptura();

	public void setCaptura(String captura);

	public String getContenido();

	public void setContenido(String contenido);

	public String getAutor();

	public void setAutor(String autor);
	
	public void setId(int id);
	
	public int getId();

//	public Type getType();
//	
//	public void setType(Type type);
	

 


}
