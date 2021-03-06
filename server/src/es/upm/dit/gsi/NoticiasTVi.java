package es.upm.dit.gsi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.MySQLJDBCIDMigrator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.gson.Gson;

import contentDiscriminator.CDConfiguration;
import contentDiscriminator.Content;
import contentDiscriminator.ContentDiscriminator;
import es.upm.dit.gsi.database.ContentDB;
import es.upm.dit.gsi.database.DatabaseHandler;
import es.upm.dit.gsi.database.PreferenceDB;
import es.upm.dit.gsi.database.UserDB;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.model.Conjunto;
import es.upm.dit.gsi.model.Noticia;
import es.upm.dit.gsi.model.Usuario;
import es.upm.dit.gsi.recommender.MyRecommender;
import es.upm.dit.gsi.util.Constants;

public class NoticiasTVi extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger("servlet.Mahout");


	public MyRecommender myRecommender;
	
	public Gson gson;
	public Conjunto conjunto;
	public List<Noticia> noticiasList = new ArrayList<Noticia>();
	public String have = "0";
	
	public void init() throws ServletException {
		super.init();
		gson = new Gson();
		myRecommender = new MyRecommender();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Esperando respuesta");
		res.setCharacterEncoding("UTF-8");
		if (req.getParameter("action").equals("getRecommendation")) {
			getRecommendation(req, res);
		} else if (req.getParameter("action").equals("getPopular")) {
			getPopular(req, res);
		} else if (req.getParameter("action").equals("getNews")) {
			getNews(req, res);
		} else if (req.getParameter("action").equals("setPreference")) {
			setPreference(req, res);
		} else if (req.getParameter("action").equals("removePreference")) {
			removePreference(req, res);
		} else if (req.getParameter("action").equals("newUser")) {
			newUser(req, res);
		} else if (req.getParameter("action").equals("removeUser")) {
			removeUser(req, res);
		} else if (req.getParameter("action").equals("getRatingOfContent")) {
			getRatingOfContent(req, res);
		} else if (req.getParameter("action").equals("getFavorites")) {
			getFavorites(req, res);
		} else if (req.getParameter("action").equals("getRandom")) {
			getRandom(req, res);
		}
		res.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * Genera la recomendación para un determinado usuario del servicio.
	 */
	private void getRecommendation(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			LOGGER.info("El usuario es: " + UserDB.getnameOfUser(userId));
			noticiasList.clear();
			if (userId != null) {
				List<RecommendedItem> recommendations = myRecommender.getRecommendations(userId);
				if (recommendations.size() == 0) {
					LOGGER.info("No hay recomendaciones para el usuario, le recomendamos lo más popular");
					getPopular(req, res);
				} else {
					for (int i = 0; i < recommendations.size(); i++) {
						RecommendedItem recommendation = recommendations.get(i);
						long contentId = recommendation.getItemID();
						float estimation = myRecommender.estimatePreference(userId, contentId);
						Noticia noticia = new Noticia(Long.toString(contentId), ContentDB.getTitleOfContent(contentId),
								ContentDB.getVideoOfContent(contentId), ContentDB.getCaptureOfContent(contentId),
								ContentDB.getDateOfContent(contentId), ContentDB.getContent(contentId),
								ContentDB.getAuthorOfContent(contentId), have, Float.toString(estimation));
						noticiasList.add(noticia);
					}
					conjunto = new Conjunto(noticiasList);
					String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
					out.print(ans);
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
	 * Devuelve los contenidos más populares (los mejores votados) por los
	 * clientes del servicio
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 *             si se produce algún error
	 * @throws IOException
	 *             si se produce algún error
	 */
	private void getPopular(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			noticiasList.clear();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			int popular = Constants.NUM_RESULTS;
			HashMap<Long, Float> averageRatings = PreferenceDB.averageRatings();
			Object[] idsContents = averageRatings.keySet().toArray();
			if (averageRatings.size() < popular) {
				popular = averageRatings.size();
				for (int j = 0; j < averageRatings.size(); j++) {
					if (PreferenceDB.numVoteOfContent((Long) idsContents[j]) < Constants.MIN_NUM_VOTE)
						popular = popular - 1;
				}
			}

			long[] id = new long[popular];
			float[] value = new float[popular];
			for (int j = 0; j < popular; j++) {
				value[j] = 0;
			}
			boolean noPopular = true;

			for (int i = 0; i < idsContents.length; i++) {
				for (int k = 0; k < popular; k++) {
					if (PreferenceDB.numVoteOfContent((Long) idsContents[i]) >= Constants.MIN_NUM_VOTE) {
						noPopular = false;
						if ((averageRatings.get((Long) idsContents[i])) != null) {
							if (averageRatings.get((Long) idsContents[i]) > value[k]) {
								for (int h = popular - 1; h > k; h--) {
									id[h] = id[h - 1];
									value[h] = value[h - 1];
								}
								id[k] = (Long) idsContents[i];
								value[k] = averageRatings.get((Long) idsContents[i]);
								break;
							}
							if (averageRatings.get((Long) idsContents[i]) == value[k]
									&& PreferenceDB.numVoteOfContent((Long) idsContents[i]) > PreferenceDB
											.numVoteOfContent(id[k])) {
								for (int h = popular - 1; h > k; h--) {
									id[h] = id[h - 1];
									value[h] = value[h - 1];
								}
								id[k] = (Long) idsContents[i];
								value[k] = averageRatings.get((Long) idsContents[i]);
								break;
							}
						}
					}
				}
			}
			if (noPopular) {
				LOGGER.info("No hay nada popular, recomendamos los más reciente");
				getNews(req, res);
				return;
			}
			for (int j = 0; j < popular; j++) {
				if (myRecommender.getPreferenceValue(userId, id[j]) != null)
					have = "1";

				Noticia not = new Noticia(Long.toString(id[j]), ContentDB.getTitleOfContent(id[j]),
						ContentDB.getVideoOfContent(id[j]), ContentDB.getCaptureOfContent(id[j]),
						ContentDB.getDateOfContent(id[j]), ContentDB.getContent(id[j]),
						ContentDB.getAuthorOfContent(id[j]), have, "");

				noticiasList.add(not);
				have = "0";
			}
			conjunto = new Conjunto(noticiasList);
			String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
			out.print(ans);
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Nos devuelve las noticias más recientes en el tiempo.
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 *             si se produce algún error
	 * @throws IOException
	 *             si se produce algún error
	 */
	private void getNews(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			long[] ids = ContentDB.getMoreRecients(Constants.NUM_RESULTS);
			noticiasList.clear();
			for (int k = 0; k < ids.length; k++) {
				if (myRecommender.getPreferenceValue(userId, ids[k]) != null)
					have = "1";

				Noticia not = new Noticia(Long.toString(ids[k]), ContentDB.getTitleOfContent(ids[k]),
						ContentDB.getVideoOfContent(ids[k]), ContentDB.getCaptureOfContent(ids[k]),
						ContentDB.getDateOfContent(ids[k]), ContentDB.getContent(ids[k]),
						ContentDB.getAuthorOfContent(ids[k]), have, "");

				noticiasList.add(not);
				have = "0";
			}
			conjunto = new Conjunto(noticiasList);
			String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
			out.print(ans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pone en la base de datos la preferencia de un cierto usuario asociada a
	 * un contenido.
	 * 
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void setPreference(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = ContentDB.getTitleOfContent(contentId);
			float preference = new Float(req.getParameter(Constants.PREFERENCE_PARAM));
			if (PreferenceDB.userHaveContent(userId, contentId)) {
				myRecommender.removePreference(contentId, userId);
				LOGGER.info("El usuario " + UserDB.getnameOfUser(userId)
						+ " ha modificado la valoración del contenido " + nameOfContent + " a " + preference);
			} else {
				LOGGER.info("El usuario " + UserDB.getnameOfUser(userId) + " añade un valoración de " + preference
						+ " al contenido " + nameOfContent);
			}
			myRecommender.setPreference(userId, contentId, preference);
			out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

	}

	/**
	 * Elimina la valoraci??n a un contenido de la base de datos.
	 * 
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void removePreference(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			String nameOfContent = ContentDB.getTitleOfContent(contentId);
			if (userId != null && contentId != null) {
				if (PreferenceDB.userHaveContent(userId, contentId)) {
					myRecommender.removePreference(contentId, userId);
					LOGGER.info("Eliminamos la valoración del usuario " + UserDB.getnameOfUser(userId)
							+ " para el contenido " + nameOfContent);
				}
			} else {
				LOGGER.warning("Usuario u objeto no encontrado");
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
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void newUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			String name = req.getParameter(Constants.NAME_USER);
			UserDB.introduceUser(name);
			long id = UserDB.getUserId(name);
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
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void removeUser(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			PrintWriter out = res.getWriter();
			PreferenceDB.removePreferencesUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER)));
			if (UserDB.getnameOfUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER))) == null)
				out.print("ok");
			UserDB.removeUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER)));
			out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Nos devuelve los contenidos que ha valorado un usuario dado y su
	 * valoraci??n.
	 * 
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void getFavorites(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			noticiasList.clear();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			PrintWriter out = res.getWriter();
			if (userId != null) {
				LOGGER.info("Los contenidos valorados por el usuario " + UserDB.getnameOfUser(userId) + " son:");
				PreferenceArray preferencesUser = myRecommender.getPreferencesFromUser(userId);
				for (int i = 0; i < preferencesUser.length(); i++) {
					have = "1";
					String content = ContentDB.getTitleOfContent(preferencesUser.getItemID(i));
					float value = preferencesUser.getValue(i);

					Noticia not = new Noticia(Long.toString(ContentDB.getContentId(content)), content,
							ContentDB.getVideoOfContent(preferencesUser.getItemID(i)),
							ContentDB.getCaptureOfContent(preferencesUser.getItemID(i)),
							ContentDB.getDateOfContent(preferencesUser.getItemID(i)),
							ContentDB.getContent(preferencesUser.getItemID(i)),
							ContentDB.getAuthorOfContent(preferencesUser.getItemID(i)), have, Float.toString(value));

					noticiasList.add(not);

				}
				conjunto = new Conjunto(noticiasList);
				out.print(gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1));
			} else {
				LOGGER.warning("Usuario no encontrado");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Nos devuelve la valoraci??n que le ha dado el usuario un determinado
	 * contenido.
	 * 
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void getRatingOfContent(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			Long contentId = Long.parseLong(req.getParameter(Constants.CONTENT));
			if (userId != null) {
				if (PreferenceDB.userHaveContent(userId, contentId)) {
					out.print("[");
					float rating = myRecommender.getPreferenceValue(userId, contentId);
					LOGGER.info("La valoraci??n al contenido " + ContentDB.getTitleOfContent(contentId)
							+ " por el usuario " + UserDB.getnameOfUser(userId) + " es:" + rating);
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
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce alg??n error
	 * @throws IOException
	 *             si se produce alg??n error
	 */
	private void getRandom(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String rate = "0";
		try {
			Random random = new Random();
			long contentId;
			noticiasList.clear();
			int num = ContentDB.getNumContents();
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			for (int k = Constants.NUM_RESULTS - 50; k < Constants.NUM_RESULTS; k++) {
				contentId = (long) (random.nextDouble() * num);
				while (ContentDB.getTitleOfContent(contentId) == "") {
					contentId = (long) (random.nextDouble() * num);
				}
				if (myRecommender.getPreferenceValue(userId, contentId) != null)
					rate = "1";

				Noticia not = new Noticia(Long.toString(contentId), ContentDB.getTitleOfContent(contentId),
						ContentDB.getVideoOfContent(contentId), ContentDB.getCaptureOfContent(contentId),
						ContentDB.getDateOfContent(contentId), ContentDB.getContent(contentId),
						ContentDB.getAuthorOfContent(contentId), rate, "");

				noticiasList.add(not);

			}
			conjunto = new Conjunto(noticiasList);
			String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
			out.print(ans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
