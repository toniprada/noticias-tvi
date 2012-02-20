package es.upm.dit.gsi.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.mysql.jdbc.PreparedStatement;

//import es.upm.dit.gsi.h2.Configuration;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.connection.Configuration;

public class Contents {
	private static Configuration conf = Configuration.getConfiguration();
	private static Connection con = conf.getDbCon();

	private static final Logger LOGGER = Logger.getLogger("jdbc.Contents");
	
	/**
	 * Introduce un nuevo contenido en la base de datos y le asigna un
	 * identificador con el cuál se le asociará a partir de ahora.
	 * También obtiene el identificar si ya esta registrado.
	 * 
	 * @param nameOfContent
	 * @return itemId
	 */
	public static long introduceContent(String title, String video, String capture, String date, String content, String author){
		Long contentId=null;
		
		try {

			String selectStatement = "SELECT id FROM contents WHERE title = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, title);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	// Si el artículo ha sido ya introducido en la base de datos.
	    	if (res.next()){
	    		contentId = res.getLong("id");
	    		LOGGER.info("El identificador asociado al contenido " +title+ " es: " + contentId);
	    	// Si es la primera vez que aparece el artículo.	
	    	} else {
	    		selectStatement = "INSERT INTO contents (title, video, capture, date, content, author) VALUES (?,?,?,?,?,?)";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, title);
		    	prepStmt.setString(2, video);
		    	prepStmt.setString(3, capture);
		    	prepStmt.setString(4, date);
		    	prepStmt.setString(5, content);
		    	prepStmt.setString(6, author);
		    	prepStmt.executeUpdate();
	    		LOGGER.info("Se ha introducido un nuevo contenido");
	    		
	    		selectStatement = "SELECT id FROM contents WHERE title = ? ";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, title);
		    	res = prepStmt.executeQuery();
		    	if (res.next()){
		    		contentId = res.getLong("id");
		    		LOGGER.info("El identificador asociado al contenido " +title+ " es: " + contentId);
		    	} else {
		    		LOGGER.severe("No deberíamos llegar aqui FALLO");
		    	}
	    	}
	    
		} catch (Exception e) {
	    	e.printStackTrace();
		}
		return contentId;
	}
	
	/**
	 * Nos devuelve el identificador asociado a un contenido ya registrado.
	 * 
	 * @param nameOfContent
	 * @return contentId
	 */
	public static Long getContentId (String title) {
		Long contentId=null;
		
		try {
			String selectStatement = "SELECT id FROM contents WHERE title = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, title);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		contentId = res.getLong("id");
	    		LOGGER.info("El identificador asociado al contenido " +title+ " es: " + contentId);
	    	} else {
	    		LOGGER.warning("No existe el contenido seleccionado");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return contentId;
	}
	
	/**
	 * Nos devuelve el nombre de un determinado contenido a 
	 * partir de su identificador.
	 * 
	 * @param contentId
	 * @return nameOfContent
	 */
	public static String getTitleOfContent (Long contentId) {
		String titleOfContent="";

		try {
			String selectStatement = "SELECT title FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		titleOfContent = res.getString("title");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return titleOfContent;
	}
	
	/**
	 * Nos devuelve el video d parsing is lenient: If the input is not in the form used bye un determinado contenido a 
	 * partir de su identificador.
	 * 
	 * @param contentId
	 * @return videoOfContent
	 */
	public static String getVideoOfContent (Long contentId) {
		String videoOfContent="";

		try {
			String selectStatement = "SELECT video FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		videoOfContent = res.getString("video");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return videoOfContent;
	}
	
	/**
	 * Nos devuelve la captura de un determinado contenido a 
	 * partir de su identificador.
	 * 
	 * @param contentId
	 * @return captureOfContent
	 */
	public static String getCaptureOfContent (Long contentId) {
		String captureOfContent="";

		try {
			String selectStatement = "SELECT capture FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		captureOfContent = res.getString("capture");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return captureOfContent;
	}
	
	/**
	 * Nos devuelve la fecha de un determinado contenido a 
	 * partir de su identificador.
	 * 
	 * @param contentId
	 * @return dateOfContent
	 */
	public static String getDateOfContent (Long contentId) {
		String dateOfContent="";

		try {
			String selectStatement = "SELECT date FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		dateOfContent = res.getString("date");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return dateOfContent;
	}
	
	/**
	 * Nos devuelve el autor de un determinado contenido a 
	 * partir de su identificador.
	 * 
	 * @param contentId
	 * @return authorOfContent
	 */
	public static String getAuthorOfContent (Long contentId) {
		String authorOfContent="";

		try {
			String selectStatement = "SELECT author FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		authorOfContent = res.getString("author");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return authorOfContent;
	}
	
	/**
	 * Nos devuelve el contenido a partir de su identificador.
	 * 
	 * @param contentId
	 * @return content
	 */
	public static String getContent (Long contentId) {
		String content="";

		try {
			String selectStatement = "SELECT content FROM contents WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, contentId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		content = res.getString("content");
	    	} else {
	    		LOGGER.warning("No existe ningún contenido con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return content;
	}
	
	/**
	 * Nos devuelve el número de contenidos disponibles en la base de datos.
	 * 
	 * @return numContent
	 */
	public static int getNumContents () {
		int numContent = 0;
		
		try {
			String selectStatement = "SELECT DISTINCT id FROM contents";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	while (res.next()){
	    		numContent++;
	    	} 	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return numContent;
	}
	
	
	/**
	 * Nos devuelve el cojunto de identificadores de los contenidos disponibles
	 * 
	 * @return contentsIds
	 */
	 public static Vector<Long> getContentsIds(){
		Vector<Long> contentsIds = new Vector<Long>();
		try {
			String selectStatement = "SELECT id FROM contents";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	ResultSet res = prepStmt.executeQuery();
	    	while (res.next()){
	    		contentsIds.addElement(res.getLong("id"));
	    	} 
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return contentsIds;
		
	}

	 
	/**
	 * Este método nos devuelve las num noticias más recientes en el tiempo
	 * 
	 * @param num
	 * @return idsRecientes nos devuelve las ids de los contenidos pedidos
	 */
	 public static long [] getMoreRecients(int num) {
		 long[] idsRecients = new long[num];
		 GregorianCalendar [] recients = new GregorianCalendar [num];
		 try {
			String selectStatement = "SELECT id, date FROM contents";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    ResultSet res = prepStmt.executeQuery();
		    int ini = 0;
		    for (int i = 0; i<recients.length; i++){
		    	recients[i]= new GregorianCalendar(ini, ini, ini, ini, ini);
		    }
		    while (res.next()){
		    	long id = res.getLong("id");
		    	String s = res.getString("date");
		    	int day = Integer.parseInt((String) s.subSequence(0, 2));
		    	int month = Integer.parseInt((String) s.subSequence(3, 5));
		    	int year = Integer.parseInt((String) s.subSequence(6, 10));
		    	int hour = Integer.parseInt((String) s.subSequence(11, 13));
		    	int minute = Integer.parseInt((String) s.subSequence(14, 16));   	
		    	GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute);
		    	for (int k = 0; k<recients.length; k++){
		    		if (cal.compareTo(recients[k])>0){
		    			for (int h=recients.length-1; h>k; h--){
							recients[h]=recients[h-1];
							idsRecients[h]=idsRecients[h-1];
						}
		    			recients[k]=cal;
		    			idsRecients[k]=id;
		    			break;
		    		}
		    	}
		    
		    }  
		 } catch (Exception e) {
		    	e.printStackTrace();
		 }
		 return idsRecients;
	 }
	 
}