package es.upm.dit.gsi.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import com.mysql.jdbc.PreparedStatement;

//import es.upm.dit.gsi.h2.Configuration;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.connection.Configuration;

public class Users {
	private static Configuration conf = Configuration.getConfiguration();
	private static Connection con = conf.getDbCon();

	private static final Logger LOGGER = Logger.getLogger("jdbc.Users");
	
	/**
	 * Introduce un nuevo usuario en la base de datos y le asigna un
	 * identificador con el cuál se le asociará a partir de ahora.
	 * También obtiene el identificador si esta registrado.
	 * 
	 * @param nameOfUser
	 * @return userId
	 */
	public static long introduceUser (String nameOfUser){
		Long userId=null;

		try {
			String selectStatement = "SELECT id FROM users WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, nameOfUser);
	    	ResultSet res = prepStmt.executeQuery();	    	
	    	// Si el usuario ya habia sido introducido
	    	if (res.next()){
	    		userId = res.getLong("id");
	    		LOGGER.info("La correspondencia de usuario id = " + userId);
	    	// Si es la primera vez que lo registramos	
	    	} else {
	    		selectStatement = "INSERT INTO users (identifier) VALUES (?)";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, nameOfUser);
		    	prepStmt.executeUpdate();
	    		LOGGER.info("Se ha introducido un nuevo usuario");
	    		
	    		selectStatement = "SELECT id FROM users WHERE identifier = ? ";
				prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
		    	prepStmt.setString(1, nameOfUser);
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
	    } 
		return userId;
	}
	
	/**
	 * Elimina la entrada de un usuario de la base de datos.
	 * 
	 * @param nameOfUser
	 */
	public static void removeUser (String nameOfUser){
		try {
			String selectStatement = "DELETE FROM users WHERE identifier=?";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
			prepStmt.setString(1, nameOfUser);
	    	prepStmt.execute();
	    	LOGGER.info("Se ha dado de baja el usuario: "+nameOfUser);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * Nos devuelve el identificador de un determinado usuario.
	 * 
	 * @param nameOfUser
	 * @return userId
	 */
	public static Long getUserId (String nameOfUser) {
		Long userId=null;

		try {
			String selectStatement = "SELECT id FROM users WHERE identifier = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setString(1, nameOfUser);
	    	ResultSet res = prepStmt.executeQuery();
	    	if (res.next()){
	    		userId = res.getLong("id");
	    		LOGGER.info("la correspondencia de usuario es id = " + userId);
	    	} else {
	    		LOGGER.warning("No existe el usuario seleccionado");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return userId;
	}
	
	/**
	 * Nos devuelve el nombre de un determinado usuario a 
	 * partir de su identificador.
	 * 
	 * @param userId
	 * @return nameOfUser
	 */
	public static String getnameOfUser(Long userId) {
		String nameOfUser="";

		try {
			String selectStatement = "SELECT identifier FROM users WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, userId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		nameOfUser = res.getString("identifier");
	    		LOGGER.info("El identificador: " +userId+ " se corresponde con el usuario: " +nameOfUser);
	    	} else {
	    		LOGGER.warning("No existe ningún usuario con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return nameOfUser;
	}
}