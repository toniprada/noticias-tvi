package es.upm.dit.gsi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.mariadb.jdbc.MySQLDataSource;

import es.upm.dit.gsi.logger.Logger;

public class DatabaseHandler {
	private Connection dbCon;
	private MySQLDataSource dataSource;
	private static DatabaseHandler conf;

	
	private static final Logger LOGGER = Logger.getLogger("connection.Configuration");
	
	/**
	 * Constructor de la clase Configuration que abre la conexión 
	 * e inicia los valores por defecto tanto de la conexión con la 
	 * base de de datos como de nuesto objeto dataSource.
	 *  
	 */
	private DatabaseHandler(){
		dataSource = new MySQLDataSource();
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
		try {
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTables() throws SQLException{
	    Statement stmt = dbCon.createStatement();
		String sqlCreate = 
			"CREATE TABLE IF NOT EXISTS `" + dbname + "`.`contents` (" +
				"`id` bigint(20) NOT NULL AUTO_INCREMENT," +
		    	"`title` tinytext NOT NULL," +
		    	"`video` text NOT NULL," +
		    	"`capture` text NOT NULL," +
		    	"`time` tinytext NOT NULL," +
		    	"`content` longtext NOT NULL," +
		    	"`author` tinytext NOT NULL," +
		    	"PRIMARY KEY (`id`)" +
		    ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
		stmt.execute(sqlCreate);
		sqlCreate = 
			"CREATE TABLE IF NOT EXISTS `" + dbname + "`.`users` (" +
				"`id` bigint(20) NOT NULL AUTO_INCREMENT," +
				"`identifier` tinytext NOT NULL," +			    	
				"PRIMARY KEY (`id`)" +
			") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
		stmt.execute(sqlCreate);
		sqlCreate = 
			"CREATE TABLE IF NOT EXISTS `" + dbname + "`.`preferenceTable` (" +
				"`user_id` bigint(20) NOT NULL," +
				"`content_id` bigint(20) NOT NULL," +
				"`preference` float NOT NULL" +			
			") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
		stmt.execute(sqlCreate);
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
	public MySQLDataSource getDataSource(){
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
