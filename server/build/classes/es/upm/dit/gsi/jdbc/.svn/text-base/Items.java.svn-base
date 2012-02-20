package es.upm.dit.gsi.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import com.mysql.jdbc.PreparedStatement;

import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.connection.Configuration;

public class Items {
	private static Configuration conf = Configuration.getConfiguration();
	private static Connection con = conf.getDbCon();

	private static final Logger LOGGER = Logger.getLogger("jdbc.Items");
	
	/**
	 * Introduce un nuevo artículo en la base de datos y le asigna un
	 * identificador con el cuál se le asociará a partir de ahora.
	 * También obtiene el identificar si ya esta registrado.
	 * 
	 * @param gesforItemId 
	 * @return itemId
	 */
	public static long itemIdentifier (String gesforItemId){
		Long itemId=null;
		
		try {

			String selectStatement = "SELECT id FROM items WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, gesforItemId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	// Si el artículo ha sido ya introducido en la base de datos.
	    	if (res.next()){
	    		itemId = res.getLong("id");
	    		LOGGER.info("El identificador asociado al artículo " +gesforItemId+ " es: " + itemId);
	    	// Si es la primera vez que aparece el artículo.	
	    	} else {
	    		selectStatement = "INSERT INTO items (identifier) VALUES (?)";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, gesforItemId);
		    	prepStmt.executeUpdate();
	    		LOGGER.info("Se ha introducido un nuevo artículo");
	    		
	    		selectStatement = "SELECT id FROM items WHERE identifier = ? ";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, gesforItemId);
		    	res = prepStmt.executeQuery();
		    	if (res.next()){
		    		itemId = res.getLong("id");
		    		LOGGER.info("El identificador asociado al artículo " +gesforItemId+ " es: " + itemId);
		    	} else {
		    		LOGGER.severe("No deberíamos llegar aqui FALLO");
		    	}
	    	}
	    
		} catch (Exception e) {
	    	e.printStackTrace();
		}
		return itemId;
	}
	
	/**
	 * Nos devuelve el identificador asociado a un artículo ya registrado.
	 * 
	 * @param gesforItemId
	 * @return itemId
	 */
	
	public static Long getItemId (String gesforItemId) {
		Long itemId=null;
		
		try {
			String selectStatement = "SELECT id FROM items WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, gesforItemId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		itemId = res.getLong("id");
	    		LOGGER.info("El identificador asociado al artículo " +gesforItemId+ " es: " + itemId);
	    	} else {
	    		LOGGER.warning("No existe el artículo seleccionado");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return itemId;
	}
	
	/**
	 * Nos devuelve el nombre de un determinado artículo a 
	 * partir de su identificador.
	 * 
	 * @param itemId
	 * @return gesforItemId
	 */
	public static String getGesforItemId (Long itemId) {
		String gesforItemId="";

		try {
			String selectStatement = "SELECT identifier FROM items WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, itemId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		gesforItemId = res.getString("identifier");
	    		//LOGGER.info("El identificador: " +itemId+ " se corresponde con el artículo: " +gesforItemId);
	    	} else {
	    		LOGGER.warning("No existe ningún artículo con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	
		return gesforItemId;
	}
}