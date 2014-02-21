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
import es.upm.dit.gsi.model.Conjunto;
import es.upm.dit.gsi.model.Noticia;
import es.upm.dit.gsi.model.Usuario;
import es.upm.dit.gsi.util.Constants;
import es.upm.dit.gsi.database.DatabaseHandler;
import es.upm.dit.gsi.database.DatabaseContent;
import es.upm.dit.gsi.database.DatabasePreference;
import es.upm.dit.gsi.database.DatabaseUser;

public class NoticiasTVi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static CDConfiguration cDConfiguration;
	private JDBCDataModel jDBCDataModel;
	private UserNeighborhood neighborhood;
	private UserSimilarity similarity;
	private Recommender recommender;
	public static DatabaseHandler conf;
	private static final Logger LOGGER = Logger.getLogger("servlet.Mahout");

	public Gson gson;
	public Conjunto conjunto;
	public List<Noticia> noticiasList = new ArrayList<Noticia>();
	public String have = "0";

	/**
	 * init del servlet
	 * 
	 * @throws ServletException
	 *             si se produce un error
	 */
	public void init() throws ServletException {
		super.init();
		conf = DatabaseHandler.getConfiguration();
		jDBCDataModel = new MySQLJDBCDataModel(conf.getDataSource(), "preferenceTable", "user_id", "content_id",
				"preference");
		LOGGER.info("datamodel es " + jDBCDataModel);
		try {
			similarity = new PearsonCorrelationSimilarity(jDBCDataModel);
		} catch (TasteException e) {
			e.printStackTrace();
		}
		neighborhood = new NearestNUserNeighborhood(2, similarity, jDBCDataModel);
		recommender = new GenericUserBasedRecommender(jDBCDataModel, neighborhood, similarity);
		gson = new Gson();
	}

	/**
	 * Genera la recomendación para un determinado usuario del servicio.
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 *             si se produce algún error
	 * @throws IOException
	 *             si se produce algún error
	 */
	private void getRecommendation(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException {
		try {
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			LOGGER.info("El usuario es: " + DatabaseUser.getnameOfUser(userId));
			Integer max = Constants.NUM_RESULTS;
			noticiasList.clear();
			if (userId != null) {
				List<RecommendedItem> recommendations = recommender.recommend(userId, max);
				if (recommendations.size() == 0) {
					// Cuando no haya recomendaciones para el usuario no
					// recomendamnos nada, darle lo más popular cuando no
					// sabemos que recomendarte.
					LOGGER.info("No hay recomendaciones para el usuario, le recomendamos lo más popular");
					getPopular(req, res);
					return;
				}
				for (int i = 0; i < recommendations.size(); i++) {
					RecommendedItem recommendation = recommendations.get(i);
					long contentId = recommendation.getItemID();
					float estimation = recommender.estimatePreference(userId, contentId);

					Noticia noticia = new Noticia(Long.toString(contentId), DatabaseContent.getTitleOfContent(contentId),
							DatabaseContent.getVideoOfContent(contentId), DatabaseContent.getCaptureOfContent(contentId),
							DatabaseContent.getDateOfContent(contentId), DatabaseContent.getContent(contentId),
							DatabaseContent.getAuthorOfContent(contentId), have, Float.toString(estimation));

					noticiasList.add(noticia);
				}
				conjunto = new Conjunto(noticiasList);
				String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
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
			HashMap<Long, Float> averageRatings = DatabasePreference.averageRatings();
			Object[] idsContents = averageRatings.keySet().toArray();
			if (averageRatings.size() < popular) {
				popular = averageRatings.size();
				for (int j = 0; j < averageRatings.size(); j++) {
					if (DatabasePreference.numVoteOfContent((Long) idsContents[j]) < Constants.MIN_NUM_VOTE)
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
					if (DatabasePreference.numVoteOfContent((Long) idsContents[i]) >= Constants.MIN_NUM_VOTE) {
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
									&& DatabasePreference.numVoteOfContent((Long) idsContents[i]) > DatabasePreference
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
				if (jDBCDataModel.getPreferenceValue(userId, id[j]) != null)
					have = "1";

				Noticia not = new Noticia(Long.toString(id[j]), DatabaseContent.getTitleOfContent(id[j]),
						DatabaseContent.getVideoOfContent(id[j]), DatabaseContent.getCaptureOfContent(id[j]),
						DatabaseContent.getDateOfContent(id[j]), DatabaseContent.getContent(id[j]),
						DatabaseContent.getAuthorOfContent(id[j]), have, "");

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
	 * Devuelve un canal personalizado de contenidos al cliente.
	 * 
	 * @param req
	 *            ,HttpServletRequest
	 * @param res
	 *            ,HttpServletResponse
	 * @throws ServletException
	 *             si se produce algún error
	 * @throws IOException
	 *             si se produce algún error
	 */
	private void getParrilla(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		noticiasList.clear();
		HashMap<String, Content> offer = new HashMap<String, Content>();
		HashMap<String, Integer> preferences = new HashMap<String, Integer>();
		Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
		Vector<Long> contentsId = DatabaseContent.getContentsIds();
		for (int i = 0; i < contentsId.size(); i++) {
			String id = contentsId.get(i).toString();
			Content cnt = new Content();
			cnt.setContentID(id);
			cnt.setSourceChannel("NA");
			offer.put(id, cnt);
		}
		for (int j = 0; j < offer.size(); j++) {
			preferences.put(contentsId.get(j).toString(), DatabasePreference.userRateToContent(userId, contentsId.get(j)));
		}
		cDConfiguration = new CDConfiguration();
		cDConfiguration.setMaxSlots(Constants.SLOTS_PARRILLA);
		cDConfiguration.setMaxPopulation(Constants.POPULATION_PARILLA);
		ContentDiscriminator object = new ContentDiscriminator();
		List<Content> parrilla = object.launch(offer, preferences, cDConfiguration);
		if (parrilla.size() == 0) {
			getPopular(req, res);
			return;
		}
		for (int k = 0; k < parrilla.size(); k++) {
			long contentId = Long.parseLong(parrilla.get(k).getContentID());

			Noticia not = new Noticia(Long.toString(contentId), DatabaseContent.getTitleOfContent(contentId),
					DatabaseContent.getVideoOfContent(contentId), DatabaseContent.getCaptureOfContent(contentId),
					DatabaseContent.getDateOfContent(contentId), DatabaseContent.getContent(contentId),
					DatabaseContent.getAuthorOfContent(contentId), have, "");

			noticiasList.add(not);
		}
		conjunto = new Conjunto(noticiasList);
		String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
		out.print(ans);
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
			long[] ids = DatabaseContent.getMoreRecients(Constants.NUM_RESULTS);
			noticiasList.clear();
			for (int k = 0; k < ids.length; k++) {
				if (jDBCDataModel.getPreferenceValue(userId, ids[k]) != null)
					have = "1";

				Noticia not = new Noticia(Long.toString(ids[k]), DatabaseContent.getTitleOfContent(ids[k]),
						DatabaseContent.getVideoOfContent(ids[k]), DatabaseContent.getCaptureOfContent(ids[k]),
						DatabaseContent.getDateOfContent(ids[k]), DatabaseContent.getContent(ids[k]),
						DatabaseContent.getAuthorOfContent(ids[k]), have, "");

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
			String nameOfContent = DatabaseContent.getTitleOfContent(contentId);
			float preference = new Float(req.getParameter(Constants.PREFERENCE_PARAM));
			if (DatabasePreference.userHaveContent(userId, contentId)) {
				jDBCDataModel.removePreference(contentId, userId);
				LOGGER.info("El usuario " + DatabaseUser.getnameOfUser(userId)
						+ " ha modificado la valoraci??n del contenido " + nameOfContent + " a " + preference);
			} else {
				LOGGER.info("El usuario " + DatabaseUser.getnameOfUser(userId) + " a??ade un valoraci??n de " + preference
						+ " al contenido " + nameOfContent);
			}
			jDBCDataModel.setPreference(contentId, userId, preference);
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
			String nameOfContent = DatabaseContent.getTitleOfContent(contentId);
			if (userId != null && contentId != null) {
				if (DatabasePreference.userHaveContent(userId, contentId)) {
					jDBCDataModel.removePreference(contentId, userId);
					LOGGER.info("Eliminamos la valoraci??n del usuario " + DatabaseUser.getnameOfUser(userId)
							+ " para el contenido " + nameOfContent);
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
			DatabaseUser.introduceUser(name);
			long id = DatabaseUser.getUserId(name);
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
			DatabasePreference.removePreferencesUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER)));
			if (DatabaseUser.getnameOfUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER))) == null)
				out.print("ok");
			DatabaseUser.removeUser(Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER)));
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
				LOGGER.info("Los contenidos valorados por el usuario " + DatabaseUser.getnameOfUser(userId) + " son:");
				PreferenceArray preferencesUser = jDBCDataModel.getPreferencesFromUser(userId);
				for (int i = 0; i < preferencesUser.length(); i++) {
					have = "1";
					String content = DatabaseContent.getTitleOfContent(preferencesUser.getItemID(i));
					float value = preferencesUser.getValue(i);

					Noticia not = new Noticia(Long.toString(DatabaseContent.getContentId(content)), content,
							DatabaseContent.getVideoOfContent(preferencesUser.getItemID(i)),
							DatabaseContent.getCaptureOfContent(preferencesUser.getItemID(i)),
							DatabaseContent.getDateOfContent(preferencesUser.getItemID(i)),
							DatabaseContent.getContent(preferencesUser.getItemID(i)),
							DatabaseContent.getAuthorOfContent(preferencesUser.getItemID(i)), have, Float.toString(value));

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
				if (DatabasePreference.userHaveContent(userId, contentId)) {
					out.print("[");
					float rating = jDBCDataModel.getPreferenceValue(userId, contentId);
					LOGGER.info("La valoraci??n al contenido " + DatabaseContent.getTitleOfContent(contentId)
							+ " por el usuario " + DatabaseUser.getnameOfUser(userId) + " es:" + rating);
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
			int num = DatabaseContent.getNumContents();
			PrintWriter out = res.getWriter();
			Long userId = Long.parseLong(req.getParameter(Constants.USER_IDENTIFIER));
			for (int k = Constants.NUM_RESULTS - 50; k < Constants.NUM_RESULTS; k++) {
				contentId = (long) (random.nextDouble() * num);
				while (DatabaseContent.getTitleOfContent(contentId) == "") {
					contentId = (long) (random.nextDouble() * num);
				}
				if (jDBCDataModel.getPreferenceValue(userId, contentId) != null)
					rate = "1";

				Noticia not = new Noticia(Long.toString(contentId), DatabaseContent.getTitleOfContent(contentId),
						DatabaseContent.getVideoOfContent(contentId), DatabaseContent.getCaptureOfContent(contentId),
						DatabaseContent.getDateOfContent(contentId), DatabaseContent.getContent(contentId),
						DatabaseContent.getAuthorOfContent(contentId), rate, "");

				noticiasList.add(not);

			}
			conjunto = new Conjunto(noticiasList);
			String ans = (String) gson.toJson(conjunto).subSequence(12, gson.toJson(conjunto).length() - 1);
			out.print(ans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ejecuta la acción solicitada por el cliente.
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 *             si se produce algún error
	 * @throws IOException
	 *             si se produce algún error
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Esperando respuesta");
		res.setCharacterEncoding("UTF-8");
		if (req.getParameter("action").equals("getRecommendation")) {
			getRecommendation(req, res);
		} else if (req.getParameter("action").equals("getPopular")) {
			getPopular(req, res);
		} else if (req.getParameter("action").equals("getParrilla")) {
			getParrilla(req, res);
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
	
}
