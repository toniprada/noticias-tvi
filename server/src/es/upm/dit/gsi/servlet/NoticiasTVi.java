package es.upm.dit.gsi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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

import com.google.gson.Gson;

import contentDiscriminator.*;

import es.upm.dit.gsi.logger.Logger;
//import es.upm.dit.gsi.social.InfoFacebook;
//import es.upm.dit.gsi.social.InfoTwitter;
import es.upm.dit.gsi.jdbc.Contents;
import es.upm.dit.gsi.jdbc.Preference;
import es.upm.dit.gsi.jdbc.Users;
import es.upm.dit.gsi.auxiliar.Conjunto;
import es.upm.dit.gsi.auxiliar.Noticia;
import es.upm.dit.gsi.auxiliar.Usuario;
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
	
	public Gson gson;
	public Conjunto conj;
	public List<Noticia> noticias = new ArrayList<Noticia>();
	public String have = "0";

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
		gson = new Gson();
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
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			LOGGER.info("El usuario es: " +Users.getnameOfUser(userId));
			Integer max = Constants.NUM_RESULTS;
			noticias.clear();
			if (userId !=null){
				List<RecommendedItem> recommendations = recommender.recommend(userId, max);
				if (recommendations.size() == 0){	// Cuando no haya recomendaciones para el usuario no recomendamnos nada, darle lo más popular cuando no sabemos que recomendarte.
					LOGGER.info("No hay recomendaciones para el usuario, le recomendamos lo más popular");
					getPopular(req,res);
					return;
				}
				for (int i = 0; i < recommendations.size(); i++) {
					RecommendedItem recommendation = recommendations.get(i);
					long contentId = recommendation.getItemID();
					float estimation = recommender.estimatePreference(userId, contentId);
				
					Noticia not = new Noticia(Long.toString(contentId),
							Contents.getTitleOfContent(contentId),
							Contents.getVideoOfContent(contentId),
							Contents.getCaptureOfContent(contentId),
							Contents.getDateOfContent(contentId),
							Contents.getContent(contentId),
							Contents.getAuthorOfContent(contentId),
							have,Float.toString(estimation),
							Integer.toString(Contents.getSocial(contentId)));
				
					noticias.add(not);
				}
				conj = new Conjunto(noticias);
				String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
				out.print(ans);
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
		/*HashMap<Long, Float> recommendationSocial = new HashMap<Long, Float>();
		float estimationSocial;
		float estimation;
		noticias.clear();*/
		try {
			PrintWriter out =res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			Integer results = Constants.NUM_RESULTS;
			if (userId !=null){
				Vector<Long> contentsids = Contents.getContentsIds();
				long[] id = new long[results];
				float[] value = new float[results];
				for (int j=0; j<results; j++){
					value[j]=0;
				}
				for (int i=0; i<contentsids.size(); i++){
					for (int k=0; k<results; k++){
						if (Contents.getSocial(contentsids.elementAt(i))>value[k]){
							for (int h=results-1; h>k; h--){
								id[h]=id[h-1];
								value[h]=value[h-1];
							}
							id[k]=contentsids.get(i);
							value[k]=Contents.getSocial(contentsids.elementAt(i));
							break;
						}
					}
				}
				/*for (int i = 0; i < Contents.getNumContents(); i++){
					long contentId = Contents.getContentsIds().elementAt(i);
					String title = Contents.getTitleOfContent(contentId);
					estimationSocial = InfoFacebook.getLikesTot(title) + InfoTwitter.getPopularityTwitter(title);
					estimation = recommender.estimatePreference(userId, contentId) + estimationSocial/10; // La recomendaciones social le suma un 10% de sus valores obternidos
					estimation = InfoTwitter.getPopularityTwitter(title);
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
				}*/
				if (results > contentsids.size()) results = contentsids.size();
				for (int m = 0; m < results ; m++) {
					if (dataModel.getPreferenceValue(userId, id[m])!=null)
						have="1";
					
					Noticia not = new Noticia(Long.toString(id[m]),
							Contents.getTitleOfContent(id[m]),
							Contents.getVideoOfContent(id[m]),
							Contents.getCaptureOfContent(id[m]),
							Contents.getDateOfContent(id[m]),
							Contents.getContent(id[m]),
							Contents.getAuthorOfContent(id[m]),
							have,"",Integer.toString(Contents.getSocial(id[m])));
					
					noticias.add(not);
					have = "0";
				}
				conj = new Conjunto(noticias);
				String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
				out.print(ans);			
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
			PrintWriter out = res.getWriter();
			noticias.clear();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			int popular = Constants.NUM_RESULTS;
			HashMap<Long, Float> averageRatings = Preference.averageRatings();
			Vector<Long> contentsids = Contents.getContentsIds();
			long[] id = new long[popular];
			float[] value = new float[popular];
			for (int j=0; j<popular; j++){
				value[j]=0;
			}
			boolean NoPopular = true;
			for (int i=0; i<contentsids.size(); i++){
				for (int k=0; k<popular; k++){
					if (Preference.numVoteOfContent(contentsids.get(i)) > Constants.MIN_NUM_VOTE){
						NoPopular = false;
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
			if (NoPopular){	
				LOGGER.info("No hay nada popular, recomendamos los más reciente");
				getNews(req,res);
				return;
			}
			for (int j=0; j<popular; j++){
				if (dataModel.getPreferenceValue(userId, id[j])!= null)
					have="1";
				
				Noticia not = new Noticia(Long.toString(id[j]),
						Contents.getTitleOfContent(id[j]),
						Contents.getVideoOfContent(id[j]),
						Contents.getCaptureOfContent(id[j]),
						Contents.getDateOfContent(id[j]),
						Contents.getContent(id[j]),
						Contents.getAuthorOfContent(id[j]),
						have,"",Integer.toString(Contents.getSocial(id[j])));
				
				noticias.add(not);
				have="0";
			}
			conj = new Conjunto(noticias);
			String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
			out.print(ans);
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
		noticias.clear();
		HashMap<String, Content> offer = new HashMap<String, Content>();
		HashMap<String, Integer> preferences = new HashMap<String, Integer>();
		Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
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
		if (parrilla.size()==0){
			getPopular(req, res);
			return;
		}
		for (int k=0; k<parrilla.size(); k++){
			long contentId = Long.parseLong(parrilla.get(k).getContentID());
			
			Noticia not = new Noticia(Long.toString(contentId),
					Contents.getTitleOfContent(contentId),
					Contents.getVideoOfContent(contentId),
					Contents.getCaptureOfContent(contentId),
					Contents.getDateOfContent(contentId),
					Contents.getContent(contentId),
					Contents.getAuthorOfContent(contentId),
					have,"",Integer.toString(Contents.getSocial(contentId)));
			
			noticias.add(not);
		}
		conj = new Conjunto(noticias);
		String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
		out.print(ans);
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
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			long [] ids = Contents.getMoreRecients(Constants.NUM_RESULTS);
			noticias.clear();
			for (int k=0; k<ids.length; k++){
				if (dataModel.getPreferenceValue(userId, ids[k])!= null)
					have="1";
				
				Noticia not = new Noticia(Long.toString(ids[k]),
						Contents.getTitleOfContent(ids[k]),
						Contents.getVideoOfContent(ids[k]),
						Contents.getCaptureOfContent(ids[k]),
						Contents.getDateOfContent(ids[k]),
						Contents.getContent(ids[k]),
						Contents.getAuthorOfContent(ids[k]),
						have,"",Integer.toString(Contents.getSocial(ids[k])));
				
				noticias.add(not);
				have="0";
			}
			conj = new Conjunto(noticias);
			String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
			out.print(ans);
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
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = Contents.getTitleOfContent(contentId);
			float preference = new Float(req.getParameter(Constants.PREFERENCE_PARAM));
			if (Preference.userHaveContent(userId, contentId)){
				dataModel.removePreference(contentId, userId);
				LOGGER.info("El usuario " + Users.getnameOfUser(userId)  + " ha modificado la valoración del contenido " + nameOfContent + " a " +preference);
			} else {
				LOGGER.info("El usuario " + Users.getnameOfUser(userId)  + " añade un valoración de " + preference + " al contenido " + nameOfContent);
			}
			dataModel.setPreference(contentId, userId, preference);
			out.print("ok");
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
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = Contents.getTitleOfContent(contentId);
			if (userId != null && contentId != null){
				if (Preference.userHaveContent(userId, contentId)){
					dataModel.removePreference(contentId, userId);
					LOGGER.info("Eliminamos la valoración del usuario " + Users.getnameOfUser(userId) + " para el contenido " + nameOfContent);
				}
			} else {
				LOGGER.warning("Usuario o objeto no encontrado");
			}
			out.print("ok");
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
			PrintWriter out =res.getWriter();
			String name = req.getParameter(Constants.NAME_USER);
			Users.introduceUser(name);
			long id = Users.getUserId(name);
			Usuario u = new Usuario(name, Long.toString(id));
			out.print(gson.toJson(u));
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
			PrintWriter out =res.getWriter();
			Preference.removePreferencesUser(Long.parseLong(req.getParameter(Constants.IDENTIFIER)));
			Users.removeUser(Long.parseLong(req.getParameter(Constants.IDENTIFIER)));
			out.print("ok");
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
	private void getFavorites(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			noticias.clear();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			PrintWriter out =res.getWriter();
			if (userId != null){
				LOGGER.info("Los contenidos valorados por el usuario " + Users.getnameOfUser(userId) + " son:");
				PreferenceArray preferencesUser = dataModel.getPreferencesFromUser(userId);
				for (int i=0; i<preferencesUser.length(); i++){
					have = "1";
					String content = Contents.getTitleOfContent(preferencesUser.getItemID(i));
					float value = preferencesUser.getValue(i);
					
					Noticia not = new Noticia(Long.toString(Contents.getContentId(content)),
							content,
							Contents.getVideoOfContent(preferencesUser.getItemID(i)),
							Contents.getCaptureOfContent(preferencesUser.getItemID(i)),
							Contents.getDateOfContent(preferencesUser.getItemID(i)),
							Contents.getContent(preferencesUser.getItemID(i)),
							Contents.getAuthorOfContent(preferencesUser.getItemID(i)),
							have,Float.toString(value),
							Integer.toString(Contents.getSocial(preferencesUser.getItemID(i))));
					
					noticias.add(not);
					
				}
				conj = new Conjunto(noticias);
				out.print(gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1));
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
	private void getRatingOfContent (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try{
			PrintWriter out =res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			if (userId != null){
				if (Preference.userHaveContent(userId, contentId)){
					out.print("[");
					float rating = dataModel.getPreferenceValue(userId, contentId);
					LOGGER.info("La valoración al contenido " +Contents.getTitleOfContent(contentId)+" por el usuario " + Users.getnameOfUser(userId) + " es:" +rating);
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
	 * Nos devuelve las noticias de la base de datos de forma aleatoria.
	 * 
	 * @param req,HttpServletRequest
	 * @param res,HttpServletResponse
	 * @throws ServletException si se produce algún error
	 * @throws IOException si se produce algún error
	 */
	private void getRandom (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		String rate = "0";
		try{
			Random rnd = new Random();
			long contentId;
			noticias.clear();
			int num = Contents.getNumContents();
			PrintWriter out =res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.IDENTIFIER));
			for (int k=0; k<Constants.NUM_RESULTS; k++){
				contentId = (long)(rnd.nextDouble()*num);
				while (Contents.getTitleOfContent(contentId) == ""){
					contentId = (long)(rnd.nextDouble()*num);
				}
				if (dataModel.getPreferenceValue(userId, contentId)!= null)
					rate = "1";
				
				Noticia not = new Noticia(Long.toString(contentId),
						Contents.getTitleOfContent(contentId),
						Contents.getVideoOfContent(contentId),
						Contents.getCaptureOfContent(contentId),
						Contents.getDateOfContent(contentId),
						Contents.getContent(contentId),
						Contents.getAuthorOfContent(contentId),
						rate,"",Integer.toString(Contents.getSocial(contentId)));
				
				noticias.add(not);
				
			}
			conj = new Conjunto(noticias);
			String ans = (String) gson.toJson(conj).subSequence(12, gson.toJson(conj).length()-1);
			out.print(ans);
		} catch (Exception e) {
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
		} else if (req.getParameter("action").equals("getRatingOfContent")) {
			getRatingOfContent(req, res);
		} else if (req.getParameter("action").equals("getFavorites")) {
			getFavorites(req, res);
  		} else if (req.getParameter("action").equals("getRandom")){
  			getRandom(req, res);
  		} 
		res.setStatus(HttpServletResponse.SC_OK);
   }

   /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   }

}
