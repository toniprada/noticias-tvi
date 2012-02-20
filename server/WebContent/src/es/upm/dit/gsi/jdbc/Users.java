package es.upm.dit.gsi.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import com.mysql.jdbc.PreparedStatement;

import es.upm.dit.gsi.logger.Logger;

public class Users {
	public static String url = "jdbc:mysql://localhost:3306/mahout";
	public static String driver = "com.mysql.jdbc.Driver";
	public static String user = "root";
	public static String pass = "pass";
	
	private static final Logger LOGGER = Logger.getLogger("jdbc.Users");
	
	/**
	 * Introduce un nuevo usuario en la base de datos y le asigna un
	 * identificador con el cuál se le asociará a partir de ahora.
	 * También obtiene el identificador si esta registrado.
	 * 
	 * @param gesforId
	 * @return userId
	 */
	public static long userIdentifier (String gesforId){
		Long userId=null;
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT id FROM users WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
	    	prepStmt.setString(1, gesforId);
	    	ResultSet res = prepStmt.executeQuery();	    	
	    	// Si el usuario ya habia sido introducido
	    	if (res.next()){
	    		userId = res.getLong("id");
	    		LOGGER.info("La correspondencia de usuario id = " + userId);
	    	// Si es la primera vez que lo registramos	
	    	} else {
	    		selectStatement = "INSERT INTO users (identifier) VALUES (?)";
				prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
		    	prepStmt.setString(1, gesforId);
		    	prepStmt.executeUpdate();
	    		LOGGER.info("Se ha introducido un nuevo usuario");
	    		
	    		selectStatement = "SELECT id FROM users WHERE identifier = ? ";
				prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
		    	prepStmt.setString(1, gesforId);
		    	res = prepStmt.executeQuery();
		    	if (res.next()){
		    		userId = res.getLong("id");
		    		LOGGER.info("la correspondencia de usuario es id = " + userId);
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
		return userId;
	}
	
	/**
	 * Nos devuelve el identificador de un determinado usuario.
	 * 
	 * @param gesforId
	 * @return userId
	 */
	public static Long getUserId (String gesforId) {
		Long userId=null;
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT id FROM users WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
	    	prepStmt.setString(1, gesforId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		userId = res.getLong("id");
	    		LOGGER.info("la correspondencia de usuario es id = " + userId);
	    	} else {
	    		LOGGER.warning("No existe el usuario seleccionado");
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
		return userId;
	}
	
	/**
	 * Nos devuelve el nombre de un determinado usuario a 
	 * partir de su identificador.
	 * 
	 * @param userId
	 * @return gesforId
	 */
	public static String getGesforId (Long userId) {
		String gesforId="";
		Connection dbCon = null;
	    
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		try {
			dbCon = DriverManager.getConnection(url, user, pass);
			String selectStatement = "SELECT identifier FROM users WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) dbCon.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, userId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		gesforId = res.getString("identifier");
	    		LOGGER.info("El identificador: " +userId+ " se corresponde con el usuario: " +gesforId);
	    	} else {
	    		LOGGER.warning("No existe ningún usuario con este identificador");
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
		return gesforId;
	}
}