package es.upm.dit.gsi.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
//import java.sql.Statement;
import com.mysql.jdbc.PreparedStatement;

import es.upm.dit.gsi.logger.Logger;

public class Items {
	public static String url = "jdbc:mysql://localhost:3306/mahout";
	public static String driver = "com.mysql.jdbc.Driver";
	public static String user = "root";
	public static String pass = "pass";
	
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
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT id FROM items WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
	    	prepStmt.setString(1, gesforItemId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	// Si el artículo ha sido ya introducido en la base de datos.
	    	if (res.next()){
	    		itemId = res.getLong("id");
	    		LOGGER.info("El identificador asociado al artículo " +gesforItemId+ " es: " + itemId);
	    	// Si es la primera vez que aparece el artículo.	
	    	} else {
	    		selectStatement = "INSERT INTO items (identifier) VALUES (?)";
				prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
		    	prepStmt.setString(1, gesforItemId);
		    	prepStmt.executeUpdate();
	    		LOGGER.info("Se ha introducido un nuevo artículo");
	    		
	    		selectStatement = "SELECT id FROM items WHERE identifier = ? ";
				prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
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
	    } finally {
	    	if (dbCon != null) {
	    		try {
	    			dbCon.close();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
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
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT id FROM items WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
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
	    } finally {
	    	if (dbCon != null) {
	    		try {
	    			dbCon.close();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
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
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT identifier FROM items WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, itemId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		gesforItemId = res.getString("identifier");
	    		LOGGER.info("El identificador: " +itemId+ " se corresponde con el artículo: " +gesforItemId);
	    	} else {
	    		LOGGER.warning("No existe ningún artículo con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (dbCon != null) {
	    		try {
	    			dbCon.close();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    }
		return gesforItemId;
	}
}
