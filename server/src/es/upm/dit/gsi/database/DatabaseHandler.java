package es.upm.dit.gsi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import es.upm.dit.gsi.logger.Logger;

public class DatabaseHandler {
	private Connection dbCon;
	private MysqlDataSource dataSource;
	private static DatabaseHandler conf;
	public static String url = "jdbc:mysql://localhost:3306/NoticiasTVi";
	public static String driver = "com.mysql.jdbc.Driver";
	public static String servername = "localhost";
	public static String dbname = "NoticiasTVi";
	public static String user = "root";
	public static String pass = "toor";	
	
	
	private static final Logger LOGGER = Logger.getLogger("connection.Configuration");
	
	/**
	 * Constructor de la clase Configuration que abre la conexión 
	 * e inicia los valores por defecto tanto de la conexión con la 
	 * base de de datos como de nuesto objeto dataSource.
	 *  
	 */
	private DatabaseHandler(){
		dataSource = new MysqlDataSource();
		dataSource.setServerName(servername);
		dataSource.setDatabaseName(dbname);
		dataSource.setUser(user);
		dataSource.setPassword(pass);
		LOGGER.info("Establecida conexión de datos a través de Mysql");
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			LOGGER.severe("Driver MySQL not load");
		}
		setDbCon(url, user, pass);
		LOGGER.info("Se ha abierto la conexión con la base de datos");
	}
	
	/**
	 * Nos devuelve el objeto configuración que contiene los datos
	 * de la base de datos que estamos utilizando.
	 * 
	 * @return conf
	 */
	 public static DatabaseHandler getInstance(){
	      if (conf == null)
	          conf = new DatabaseHandler();
	      return conf;
	 }
	 
	 /**
	  * Método al que llamamos para crear el objeto configuración en nuestros casos de prueba.
	  * 
	  * @param dbname
	  * @param user
	  * @param pass
	  */
	 /*public static void setTestConfiguration(String dbname, String user, String pass, String url){
		 conf = new Configuration (dbname, user, pass, url);
	 }*/
	
	/**
	 * Nos devuelve el objeto dataSource de nuestro recomendador.
	 * 
	 * @return dataSource
	 */
	public MysqlDataSource getDataSource(){
		return dataSource;
	}
	
	/**
	 * Configura los valores de nuestra conexión con la base de datos.
	 * 
	 */
	public void setDbCon(String url, String user, String pass){
		try {
			dbCon= DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Nos devuelve nuestro objeto conexión asociado a la base 
	 * de datos que estamos usando.
	 * 
	 * @return dbCon
	 */
	public Connection getDbCon(){
		return dbCon;
	}
	
}
