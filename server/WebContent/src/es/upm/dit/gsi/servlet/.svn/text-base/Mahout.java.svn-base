package es.upm.dit.gsi.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.common.TasteException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
//import es.upm.dit.gsi.logger.LoggerService;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.jdbc.Users;

public class Mahout extends HttpServlet {
   private static final long serialVersionUID = 1L;
   private MysqlDataSource dataSource;
   private JDBCDataModel dataModel;
   private UserNeighborhood neighborhood;
   private UserSimilarity similarity;
   private Recommender recommender;

   private static final Logger LOGGER = Logger.getLogger("servlet.Mahout");

	/**
	 * init del servlet
	 * @throws ServletException si se produce un error
	 */
	public void init() throws ServletException {
		super.init();
		dataSource = new MysqlDataSource();
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("mahout");
		dataSource.setUser("mahoutu");
		dataSource.setPassword("mahoutp");
		dataModel = new MySQLJDBCDataModel(dataSource, "preference_table", "user_id","item_id", "preference");
		try {
			similarity = new PearsonCorrelationSimilarity(dataModel);
		} catch (TasteException e) {
			e.printStackTrace();
		}
		neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
		recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
	}

   /**
	 * Pone en la base de datos la preferencia de un cierto usuario asociada a un objeto.
	 * @param res, HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
	private synchronized void setPreference(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		try {
			String gesforId = req.getParameter(Constants.IDENTIFIER);
			long userId = Users.userIdentifier(gesforId);
			//long userId = new Long (req.getParameter(Constants.USER_ID_PARAM));
			long itemId = new Long (req.getParameter(Constants.ITEM_ID_PARAM));
			float preference = new Float(req.getParameter(Constants.PREFERENCE_PARAM));
			dataModel.setPreference(userId, itemId, preference);
			String gesforId1 = Users.getGesforId(userId);
			LOGGER.info("El usuario " + gesforId1 + " añade un valoración de " + preference + " al objeto con identificador " + itemId);
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

	}

   /**
	 * Genera la recomendación para un determinado usuario del servicio.
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
	private void getRecommendation(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			String identifier = req.getParameter(Constants.IDENTIFIER);
			Long userId = Users.getUserId(identifier);
			//Integer userId = new Integer(req.getParameter(Constants.USER_ID_PARAM));
			Integer max = new Integer(req.getParameter(Constants.MAX_RECOM_PARAM));
			if (userId !=null){
				List<RecommendedItem> recommendations = recommender.recommend(userId, max);
				for (RecommendedItem recommendation : recommendations) {
					System.out.println(recommendation);
				}
			} else {
				LOGGER.warning("No se puede dar recomendación ya que no existe el usuario"); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Nos devuelve los objetos que ha valorado un usuario dado.
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
	private void itemsFromUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			String gesforId= req.getParameter (Constants.IDENTIFIER);
			long userId = Users.getUserId(gesforId);
			//Integer userId = new Integer(req.getParameter(Constants.USER_ID_PARAM));
			if (userId!=-1){
				LOGGER.info("Los objetos valorados por el usuario" + userId + "son:");
				dataModel.getItemIDsFromUser(userId);
			} else {
				LOGGER.warning("Usuario no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}

   /**
	 * Nos devuelve la tabla de preferencias guardada en la base de datos
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
	private void table(HttpServletResponse res) throws ServletException, IOException{
		try{
			dataModel.getUserIDs();
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Elimina un  elemento de la base de datos.
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
	private void removePreference(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try {
			String gesforId = req.getParameter(Constants.IDENTIFIER);
			long userId = Users.getUserId(gesforId);
			//long userId = new Long (req.getParameter(Constants.USER_ID_PARAM));
			long itemId = new Long (req.getParameter(Constants.ITEM_ID_PARAM));
			if (userId!=-1){
				dataModel.removePreference(userId, itemId);
				LOGGER.info("Eliminamos la valoración del usuario " + userId + " para el objeto " + itemId);
			} else {}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

   /**
	 * Ejecuta la acción solicitada por el cliente
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algun error
	 * @throws IOException si se produce algun error
	 */
   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Esperando respuesta");

		if (req.getParameter("action").equals("getRecommendation")) {
			getRecommendation(req, res);
		} else if (req.getParameter("action").equals("setPreference")) {
			setPreference(req, res);
		} else if (req.getParameter("action").equals("table")) {
			table(res);
		} else if (req.getParameter("action").equals("itemsUSer")) {
			itemsFromUser(req, res);
		} else if (req.getParameter("action").equals("removePreferece")) {
			removePreference(req,res);
   		}
		res.setStatus(HttpServletResponse.SC_OK);
   }

   /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       // TODO Auto-generated method stub
   }

}