package es.upm.dit.gsi.h2;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import es.upm.dit.gsi.logger.Logger;

import java.sql.*;
import java.io.*;

public class Configuration implements ServletContextListener, HttpSessionListener {

	private MysqlDataSource dataSource;
	private Connection globalConnection;
	private static Configuration conf;
	public static String driver = "com.mysql.jdbc.Driver";
    private final static String DBFILENAME = "NoticiasTVi";
    public static String user = "noticiasu";
	public static String pass = "noticiasp";
	public static String url;
	
	private static final Logger LOGGER = Logger.getLogger("connection.Configuration");
    

    /**
	 * Constructor de la clase Configuration que abre la conexión 
	 * e inicia los valores por defecto tanto de la conexión con la 
	 * base de de datos como de nuesto objeto dataSource.
	 *  
	 */
	private Configuration(){
		dataSource = new MysqlDataSource();
		dataSource.setDatabaseName(DBFILENAME);
		dataSource.setUser(user);
		dataSource.setPassword(pass);
		LOGGER.info("Establecida conexión de datos a través de Mysql");
		
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			System.out.println("Driver MySQL not load");
		}
		contextInitialized(new ServletContextEvent(null));
		LOGGER.info("Se ha abierto la conexión con la base de datos");
	}
	
	/**
	 * Nos devuelve el objeto configuración que contiene los datos
	 * de la base de datos que estamos utilizando.
	 * 
	 * @return conf
	 */
	 public static Configuration getConfiguration(){
	      if (conf == null)
	          conf = new Configuration();
	      return conf;
	 }
    
    public void contextInitialized(ServletContextEvent sce) {
        // Leemos el parametro del contexto
        String spath = sce.getServletContext().getInitParameter("h2.database.path");
        if (spath == null) {
            // Si no existe, es la home del usuario
            spath = System.getProperty("user.home");
        }
        // Comprobamos si no existe la base de datos
        boolean exists = new File(spath, DBFILENAME + ".data.db").exists();

        try {
            Class.forName("org.h2.Driver");
            File dbfile = new File(spath, DBFILENAME);
            url = "jdbc:h2:file:" + dbfile.getAbsolutePath().replaceAll("\\\\", "/");
            globalConnection = openConnection();
            if (!exists) {
                initDb();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDb() throws SQLException, IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("NoticiasTViPrueba.sql.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        Connection connection = null;
        try {
            connection = openConnection();
            String line = br.readLine();
            StringBuilder statement = new StringBuilder();
            while (line != null) {
                line = line.trim();
                if (!line.startsWith("--") && !line.startsWith("#") && !line.startsWith("//")) {
                    statement.append(line);
                    if (line.endsWith(";")) {
                        executeLine(connection, statement.toString());
                        statement = new StringBuilder();
                    }
                }
                line = br.readLine();
            }
            if (statement.length() > 0) {
                executeLine(connection, statement.toString());
            }
        } finally {
            try {
                br.close();
            } catch (Exception e) {;}
            try {
                if (connection != null) connection.close();
            } catch (Exception e) {;}
        }
    }
    
    
    private void executeLine(Connection connection, String statement) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(statement);
        pstmt.execute();
        pstmt.close();
        System.out.println("Ejecutando "+statement);
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, "", "sa");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            globalConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sessionCreated(HttpSessionEvent se) {
        try {
            se.getSession().setAttribute("h2.connection", openConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            Connection con = (Connection) se.getSession().getAttribute("h2.connection");
            con.close();
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
		return globalConnection;
	}
	
	/**
	 * Nos devuelve el objeto dataSource de nuestro recomendador.
	 * 
	 * @return dataSource
	 */
	public MysqlDataSource getDataSource(){
		return dataSource;
	}
	
	

}
