package es.upm.dit.gsi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.common.TasteException;

import contentDiscriminator.*;

import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.social.InfoFacebook;
import es.upm.dit.gsi.social.InfoTwitter;
import es.upm.dit.gsi.auxiliar.Data;
//import es.upm.dit.gsi.h2.Configuration;
import es.upm.dit.gsi.jdbc.Contents;
import es.upm.dit.gsi.jdbc.Preference;
import es.upm.dit.gsi.jdbc.Users;
import es.upm.dit.gsi.connection.Configuration;


public class NoticiasTVi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static CDConfiguration cfg;
	private JDBCDataModel dataModel;
	private UserNeighborhood neighborhood;
	private UserSimilarity similarity;
	private Recommender recommender;
	public static Configuration conf;
	private static final Logger LOGGER = Logger.getLogger("servlet.Mahout");

	/**
	 * init del servlet
	 * 
	 * @throws ServletException si se produce un error
	 */
	public void init() throws ServletException {
		super.init();
		conf = Configuration.getConfiguration();
		dataModel = new MySQLJDBCDataModel(conf.getDataSource(), "preferenceTable", "user_id","content_id", "preference");
		LOGGER.info("dato model es"+dataModel);
		try {
			similarity = new PearsonCorrelationSimilarity(dataModel);
		} catch (TasteException e) {
			e.printStackTrace();
		}
		neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
		recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
	}

   /**
	 * Genera la recomendación para un determinado usuario del servicio.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getRecommendation(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			PrintWriter out =res.getWriter();
			String identifier = req.getParameter(Constants.IDENTIFIER);
			Long userId = Users.getUserId(identifier);
			Integer max = Constants.MAX_RECOM_PARAM;
			if (userId !=null){
				List<RecommendedItem> recommendations = recommender.recommend(userId, max);
				if (recommendations.size() == 0){	// Cuando no haya recomendaciones para el usuario no recomendamnos nada, darle lo más popular cuando no sabemos que recomendarte.
					LOGGER.info("No hay recomendaciones para el usuario, le recomendamos lo más popular");
					getPopular(req,res);
					return;
				}
				out.print("[");
				for (int i = 0; i < recommendations.size(); i++) {
					RecommendedItem recommendation = recommendations.get(i);
					long contentId = recommendation.getItemID();
					float estimation = recommender.estimatePreference(userId, contentId);
					out.print("{'nombre':'"+Contents.getTitleOfContent(contentId));
					out.print("','estimacion':'"+estimation);
					out.print("','video':'"+Contents.getVideoOfContent(contentId));
					out.print("','captura':'"+Contents.getCaptureOfContent(contentId));
					out.print("','fecha':'"+Contents.getDateOfContent(contentId));
					out.print("','contenido':'"+Contents.getContent(contentId));
					out.print("','autor':'"+Contents.getAuthorOfContent(contentId));
					if (i + 1 != recommendations.size())
						out.print("'},");
					else 
						out.print("'}]");
				}
				//out.print("])");
			} else {
				LOGGER.warning("No se puede dar recomendación ya que no existe el usuario"); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}
	
	
	/**
	 * Genera la recomendación para un determinado usuario del servicio teniendo en cuenta 
	 * las distintas redes sociales.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getRecommendationSocial(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HashMap<Long, Float> recommendationSocial = new HashMap<Long, Float>();
		float estimationSocial;
		float estimation;
		try {
			PrintWriter out =res.getWriter();
			String identifier = req.getParameter(Constants.IDENTIFIER);
			Long userId = Users.getUserId(identifier);
			Integer max = Constants.MAX_RECOM_PARAM;
			if (userId !=null){
				for (int i = 0; i < Contents.getNumContents(); i++){
					long contentId = Contents.getContentsIds().elementAt(i);
					String title = Contents.getTitleOfContent(contentId);
					estimationSocial = InfoFacebook.getLikesTot(title) + InfoTwitter.getPopularityTwitter(title);
					estimation = recommender.estimatePreference(userId, contentId) + estimationSocial/10; // La recomendaciones social le suma un 10% de sus valores obternidos
					recommendationSocial.put(contentId, estimation);
				}
				float [] values= new float [max];
				long [] keys= new long [max];
				List <Long> mapKeys = new ArrayList<Long>(recommendationSocial.keySet());
				for (int k=0; k<max; k++){
					keys[k]=0;values[k]=0;
				}
				for (int j=0; j < recommendationSocial.size(); j++){
					for (int l=0; l<max; l++){
						if (recommendationSocial.get(mapKeys.get(j))>values[l]){
							for (int h=max-1; h>l; h--){
								values[h]=values[h-1];
								keys[h]=keys[h-1];
							}
							values[l]=recommendationSocial.get(mapKeys.get(j));
							keys[l]=mapKeys.get(j);
							break;
						}
					}	
				}
				for (int m = 0; m < max ; m++) {
					out.print("{'nombre':'"+Contents.getTitleOfContent(keys[m]));
					out.print("','estimacion':'"+values[m]);
					out.print("','video':'"+Contents.getVideoOfContent(keys[m]));
					out.print("','captura':'"+Contents.getCaptureOfContent(keys[m]));
					out.print("','fecha':'"+Contents.getDateOfContent(keys[m]));
					out.print("','contenido':'"+Contents.getContent(keys[m]));
					out.print("','autor':'"+Contents.getAuthorOfContent(keys[m]));
					if (m + 1 != max)
						out.print("'},");
					else 
						out.print("'}]");
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
	 * Devuelve los contenidos más populares (los mejores votados) por los clientes del servicio
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getPopular (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try {
			PrintWriter out =res.getWriter();
			int popular = Constants.NUM_POPULAR;
			out.print("[");
			HashMap<Long, Float> averageRatings = Preference.averageRatings();
			Vector<Long> contentsids = Contents.getContentsIds();
			long[] id = new long[popular];
			float[] value = new float[popular];
			for (int j=0; j<popular; j++){
				value[j]=0;
			}
			for (int i=0; i<contentsids.size(); i++){
				for (int k=0; k<popular; k++){
					if (Preference.numVoteOfContent(contentsids.get(i)) > Constants.MIN_NUM_VOTE){
						if((averageRatings.get(contentsids.get(i))) != null){
							if (averageRatings.get(contentsids.get(i))>value[k]){
								for (int h=popular-1; h>k; h--){
									id[h]=id[h-1];
									value[h]=value[h-1];
								}
								id[k]=contentsids.get(i);
								value[k]=averageRatings.get(contentsids.get(i));
								break;
							}
						}
					}
				}
			}
			for (int j=0; j<popular; j++){
				out.print("{'nombre':'"+Contents.getTitleOfContent(id[j]));
				out.print("','id':'"+id[j]);
				out.print("','video':'"+Contents.getVideoOfContent(id[j]));
				out.print("','captura':'"+Contents.getCaptureOfContent(id[j]));
				out.print("','fecha':'"+Contents.getDateOfContent(id[j]));
				out.print("','contenido':'"+Contents.getContent(id[j]));
				out.print("','autor':'"+Contents.getAuthorOfContent(id[j]));
				if (j + 1 != popular)
					out.print("'},");
			}
			out.print("'}]");	
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * Devuelve un canal personalizado de contenidos al cliente.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getParrilla (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out =res.getWriter();
		HashMap<String, Content> offer = new HashMap<String, Content>();
		HashMap<String, Integer> preferences = new HashMap<String, Integer>();
		String identifier = req.getParameter(Constants.IDENTIFIER);
		Long userId = Users.getUserId(identifier);
		Vector<Long> contentsId = Contents.getContentsIds();
		for (int i=0; i<contentsId.size(); i++){
			String id = contentsId.get(i).toString();
			Content cnt = new Content();
			cnt.setContentID(id);
			cnt.setSourceChannel("NA");
			offer.put(id, cnt);
		}
		for (int j=0; j<offer.size(); j++){
			preferences.put(contentsId.get(j).toString(), Preference.userRateToContent(userId, contentsId.get(j)));
		}
		cfg = new CDConfiguration();
		cfg.setMaxSlots(Constants.SLOTS_PARRILLA);
		cfg.setMaxPopulation(Constants.POPULATION_PARILLA);
		ContentDiscriminator object = new ContentDiscriminator();
		List<Content> parrilla = object.launch(offer, preferences, cfg); 
		out.print("[");
		for (int k=0; k<parrilla.size(); k++){
			long contentId = Long.parseLong(parrilla.get(k).getContentID());
			out.print("{'nombre':'"+Contents.getTitleOfContent(contentId));
			out.print("','video':'"+Contents.getVideoOfContent(contentId));
			out.print("','captura':'"+Contents.getCaptureOfContent(contentId));
			out.print("','fecha':'"+Contents.getDateOfContent(contentId));
			out.print("','contenido':'"+Contents.getContent(contentId));
			out.print("','autor':'"+Contents.getAuthorOfContent(contentId));
			if (k + 1 != parrilla.size())
				out.print("'},");
		}
		out.print("'}]");
	}
	
	/**
	 * Nos devuelve las noticias más recientes en el tiempo.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getNews (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try{
			PrintWriter out =res.getWriter();
			long [] ids = Contents.getMoreRecients(Constants.MAX_RECOM_PARAM);
			out.print("[");
			for (int k=0; k<ids.length; k++){
				out.print("{'nombre':'"+Contents.getTitleOfContent(ids[k]));
				out.print("','video':'"+Contents.getVideoOfContent(ids[k]));
				out.print("','captura':'"+Contents.getCaptureOfContent(ids[k]));
				out.print("','fecha':'"+Contents.getDateOfContent(ids[k]));
				out.print("','contenido':'"+Contents.getContent(ids[k]));
				out.print("','autor':'"+Contents.getAuthorOfContent(ids[k]));
				if (k + 1 != ids.length)
					out.print("'},");
			}
			out.print("'}]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Pone en la base de datos la preferencia de un cierto usuario asociada a un contenido.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void setPreference(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException{
		try {
			String nameOfUser = req.getParameter(Constants.IDENTIFIER);
			Long userId = Users.introduceUser(nameOfUser);
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = Contents.getTitleOfContent(contentId);
			float preference = new Float(req.getParameter(Constants.PREFERENCE_PARAM));
			if (Preference.userHaveContent(userId, contentId)){
				dataModel.removePreference(userId, contentId);
				LOGGER.info("El usuario " + nameOfUser  + " ha modificado la valoración del contenido " + nameOfContent + " a " +preference);
			} else {
				LOGGER.info("El usuario " + nameOfUser  + " añade un valoración de " + preference + " al contenido " + nameOfContent);
			}
			dataModel.setPreference(userId, contentId, preference);
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

	}
	
	/**
	 * Elimina la valoración a un contenido de la base de datos.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void removePreference(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try {
			String nameOfUser = req.getParameter(Constants.IDENTIFIER);
			Long userId = Users.getUserId(nameOfUser);
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = Contents.getTitleOfContent(contentId);
			if (userId != null && contentId != null){
				if (Preference.userHaveContent(userId, contentId) == true){
					dataModel.removePreference(userId, contentId);
					LOGGER.info("Eliminamos la valoración del usuario " + nameOfUser + " para el contenido " + nameOfContent);
				}
			} else {
				LOGGER.warning("Usuario o objeto no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * Introduce un nuevo usuario en la base de datos
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void newUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try {
			Users.introduceUser(req.getParameter(Constants.NAME_USER));
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * Borra un usuario ya creado de la base de datos
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void removeUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		try {
			Preference.removePreferencesUser(Users.getUserId(req.getParameter(Constants.NAME_USER)));
			Users.removeUser(req.getParameter(Constants.NAME_USER));
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * Nos devuelve los contenidos que ha valorado un usuario dado y su valoración.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getContentsFromUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			String nameOfUser= req.getParameter (Constants.IDENTIFIER);
			Long userId = Users.getUserId(nameOfUser);
			PrintWriter out =res.getWriter();
			if (userId != null){
				LOGGER.info("Los contenidos valorados por el usuario " + nameOfUser + " son:");
				PreferenceArray preferencesUser = dataModel.getPreferencesFromUser(userId);
				out.print("[");
				for (int i=0; i<preferencesUser.length(); i++){
					String content = Contents.getTitleOfContent(preferencesUser.getItemID(i));
					float value = preferencesUser.getValue(i);
					LOGGER.info("Contenido: "+content+". Valoración: " +value);
					out.print("{'nombre':'"+content);
					out.print("','id':'"+preferencesUser.getItemID(i));
					out.print("','video':'"+Contents.getVideoOfContent(preferencesUser.getItemID(i)));
					out.print("','captura':'"+Contents.getCaptureOfContent(preferencesUser.getItemID(i)));
					out.print("','fecha':'"+Contents.getDateOfContent(preferencesUser.getItemID(i)));
					out.print("','contenido':'"+Contents.getContent(preferencesUser.getItemID(i)));
					out.print("','autor':'"+Contents.getAuthorOfContent(preferencesUser.getItemID(i)));
					out.print("','valoración':'"+value);
					if (i + 1 != preferencesUser.length())
						out.print("'},");
				}
				out.print("'}]");
			} else {
				LOGGER.warning("Usuario no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * Nos devuelve la valoración que le ha dado el usuario un determinado contenido.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getRatingOfUserToContent (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			PrintWriter out =res.getWriter();
			String nameOfUser= req.getParameter (Constants.IDENTIFIER);
			String nameOfContent= req.getParameter (Constants.CONTENT);
			Long userId = Users.getUserId(nameOfUser);
			Long contentId = Contents.getContentId(nameOfContent);
			if (userId != null){
				if (Preference.userHaveContent(userId, contentId)){
					out.print("[");
					float rating = dataModel.getPreferenceValue(userId, contentId);
					LOGGER.info("La valoración al contenido " +nameOfContent+" por el usuario " + nameOfUser + " es:" +rating);
					out.print(rating);
					out.print("]");
				} else {
					LOGGER.warning("El usuario no ha valorado el contenido");
				}
			} else {
				LOGGER.warning("Usuario no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * Introduce datos de prueba en la base de datos
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	private void setData (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		Data.introduceData();
		try {
			dataModel.setPreference(1, 2, 4);
			dataModel.setPreference(1, 4, 5);
			dataModel.setPreference(1, 7, 2);
			dataModel.setPreference(1, 10, 4);
			dataModel.setPreference(1, 8, 3);
			dataModel.setPreference(2, 3, 3);
			dataModel.setPreference(2, 1, 4);
			dataModel.setPreference(2, 10, 2);
			dataModel.setPreference(2, 3, 5);
			dataModel.setPreference(3, 4, 1);
			dataModel.setPreference(3, 7, 5);
			dataModel.setPreference(3, 1, 4);
			dataModel.setPreference(4, 6, 5);
			dataModel.setPreference(4, 2, 2);
			dataModel.setPreference(4, 5, 4);
			dataModel.setPreference(4, 11, 5);
			dataModel.setPreference(4, 7, 4);
			dataModel.setPreference(5, 5, 5);
			dataModel.setPreference(5, 11, 5);
			dataModel.setPreference(5, 8, 3);
			dataModel.setPreference(6, 9, 5);
			dataModel.setPreference(6, 3, 4);
			dataModel.setPreference(6, 7, 4);
			dataModel.setPreference(6, 1, 3);
			dataModel.setPreference(7, 10, 3);
			dataModel.setPreference(7, 5, 2);
			dataModel.setPreference(7, 2, 5);
			dataModel.setPreference(7, 4, 4);
			dataModel.setPreference(7, 11, 3);
			dataModel.setPreference(8, 9, 4);
			dataModel.setPreference(8, 8, 5);
			dataModel.setPreference(8, 6, 5);
			dataModel.setPreference(8, 2, 3);
			
			LOGGER.info("Datos introducios");
		} catch (TasteException e) {
			LOGGER.warning("Ha ocurrido un error al introducir los datos");
			e.printStackTrace();
		}
	}

   /**
	 * Ejecuta la acción solicitada por el cliente.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Esperando respuesta");
		res.setCharacterEncoding("UTF-8");
		if (req.getParameter("action").equals("getRecommendation")) {
			getRecommendation(req, res);
		} else if (req.getParameter("action").equals("getRecommendationSocial")) {
			getRecommendationSocial(req, res);
  		} else if (req.getParameter("action").equals("getPopular")){
   			getPopular(req, res);
  		} else if (req.getParameter("action").equals("getParrilla")){
   			getParrilla(req, res);
  		} else if (req.getParameter("action").equals("getNews")){
   			getNews(req, res);
   		} else if (req.getParameter("action").equals("setPreference")) {
			setPreference(req, res);
		} else if (req.getParameter("action").equals("removePreference")) {
			removePreference(req,res);
		} else if (req.getParameter("action").equals("newUser")){
			newUser(req, res);
		} else if (req.getParameter("action").equals("removeUser")){
			removeUser(req, res);
		} else if (req.getParameter("action").equals("getRatingOfUserToContent")) {
			getRatingOfUserToContent(req, res);
		} else if (req.getParameter("action").equals("getContentsFromUser")) {
			getContentsFromUser(req, res);
  		} else if (req.getParameter("action").equals("setData")){
  			setData(req, res);
  		} 
		res.setStatus(HttpServletResponse.SC_OK);
   }

   /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   }

}
