package es.upm.dit.gsi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;





import java.sql.SQLException;





//import es.upm.dit.gsi.h2.Configuration;
import es.upm.dit.gsi.logger.Logger;

public class UserDB {

	private static final Logger LOGGER = Logger.getLogger("jdbc.Users");
	
	/**
	 * Introduce un nuevo usuario en la base de datos y le asigna un
	 * identificador con el cu??l se le asociar?? a partir de ahora.
	 * Tambi??n obtiene el identificador si esta registrado.
	 * 
	 * @param nameOfUser
	 * @return userId
	 */
	public static long introduceUser (String nameOfUser){
		Long userId=null;
		Connection con = null;
		try {
			con = new DatabaseHandler().getCon();
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
		    		LOGGER.severe("No deber??amos llegar aqui FALLO");
		    	}
	    	}
	    
		} catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return userId;
	}
	
	/**
	 * Elimina la entrada de un usuario de la base de datos.
	 * 
	 * @param nameOfUser
	 */
	public static void removeUser (Long userId){
		Connection con = null;
		try {
			con = new DatabaseHandler().getCon();
			String selectStatement = "DELETE FROM users WHERE id=?";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
			prepStmt.setLong(1, userId);
	    	if (prepStmt.execute())
	    		LOGGER.info("Se ha dado de baja el usuario: "+getnameOfUser(userId));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		Connection con = null;
		try {
			con = new DatabaseHandler().getCon();

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
	    } finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		Connection con = null;
		try {
			con = new DatabaseHandler().getCon();

			String selectStatement = "SELECT identifier FROM users WHERE id = ? ";
			PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
	    	prepStmt.setLong(1, userId);
	    	ResultSet res = prepStmt.executeQuery();
	    	
	    	if (res.next()){
	    		nameOfUser = res.getString("identifier");
	    		LOGGER.info("El identificador: " +userId+ " se corresponde con el usuario: " +nameOfUser);
	    	} else {
	    		LOGGER.warning("No existe ning??n usuario con este identificador");
	    	}
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nameOfUser;
	}
}