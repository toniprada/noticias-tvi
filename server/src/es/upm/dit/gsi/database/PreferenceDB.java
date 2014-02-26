package es.upm.dit.gsi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
//import es.upm.dit.gsi.h2.Configuration;
import es.upm.dit.gsi.logger.Logger;

public class PreferenceDB {
	private static DatabaseHandler conf = DatabaseHandler.getInstance();
	private static Connection con = conf.getDbCon();

	private static final Logger LOGGER = Logger.getLogger("jdbc.Preference");

	/**
	 * Nos indica si un usuario ha valorado un contenido.
	 * 
	 * @param userId
	 * @param contentId
	 * @return true or false
	 */
	public static boolean userHaveContent(Long userId, Long contentId) {
		try {
			String selectStatement = "SELECT preference FROM preferenceTable WHERE user_id = ? AND content_id = ?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, userId);
			prepStmt.setLong(2, contentId);
			ResultSet res = prepStmt.executeQuery();
			if (res.next()) {
				LOGGER.info("El usario ha puntuado este contenido");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("El usuario no ha puntado este contenido");
		return false;
	}

	/**
	 * Nos indica da la valoraci??n que un usuario ha dado a un contenido.
	 * 
	 * @param userId
	 * @param contentId
	 * @return true or false
	 */
	public static Integer userRateToContent(Long userId, Long contentId) {
		int preference = 0;
		try {
			String selectStatement = "SELECT preference FROM preferenceTable WHERE user_id = ? AND content_id = ?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, userId);
			prepStmt.setLong(2, contentId);
			ResultSet res = prepStmt.executeQuery();
			if (res.next()) {
				preference = res.getInt("preference");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preference;
	}

	/**
	 * Nos devuelve el n??mero de contenidos puntuados.
	 * 
	 * @return num_contents
	 */
	public static int numberOfRatedContents() {
		int num_contents = 0;
		try {
			String selectStatement = "SELECT DISTINCT content_id FROM preferenceTable";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			ResultSet res = prepStmt.executeQuery();
			while (res.next())
				num_contents++;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num_contents;
	}

	/**
	 * Nos devuelve el conjunto de contenidos puntuados por un usuario.
	 * 
	 * @return num_contents
	 */
	public static Vector<Long> RatedContentsOfUser(long userId) {
		Vector<Long> contentsRatedIds = new Vector<Long>();
		try {
			String selectStatement = "SELECT content_id FROM preferenceTable WHERE user_id=?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, userId);
			ResultSet res = prepStmt.executeQuery();
			while (res.next()) {
				contentsRatedIds.addElement(res.getLong("content_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentsRatedIds;
	}

	/**
	 * Nos devuelve los contenidos y su media de valoraci??n.
	 * 
	 * @return avRatings
	 */
	public static HashMap<Long, Float> averageRatings() {
		HashMap<Long, Float> avRatings = new HashMap<Long, Float>();
		try {
			String selectStatement = "SELECT DISTINCT content_id FROM preferenceTable";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			ResultSet res = prepStmt.executeQuery();
			while (res.next()) {
				long id = res.getLong("content_id");
				float averageItem = getAverageRating(id);
				System.out.println(id + "," + averageItem);
				avRatings.put(id, averageItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return avRatings;
	}

	/**
	 * Nos da la valoraci??n media de un contenido.
	 * 
	 * @param itemId
	 * @return average
	 */
	public static float getAverageRating(long contentId) {
		int times = 0;
		float totalRating = 0;
		try {
			String selectStatement = "SELECT preference FROM preferenceTable WHERE content_id=?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, contentId);
			ResultSet res = prepStmt.executeQuery();
			while (res.next()) {
				times++;
				totalRating += res.getFloat("preference");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		float average = totalRating / times;
		return average;
	}

	/**
	 * Nos devuelve el n??mero de votos que ha recibido un determinado
	 * contenido.
	 * 
	 * @param contentId
	 * @return numVotes
	 */
	public static int numVoteOfContent(long contentId) {
		int numVotes = 0;
		try {
			String selectStatement = "SELECT preference FROM preferenceTable WHERE content_id=?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, contentId);
			ResultSet res = prepStmt.executeQuery();
			while (res.next()) {
				numVotes++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numVotes;
	}

	/**
	 * Elimina las valoraciones que haya dado el usuario en los distintos
	 * contenidos.
	 * 
	 * @param contentId
	 */
	public static void removePreferencesUser(long userId) {
		try {
			String selectStatement = "DELETE FROM preferenceTable WHERE user_id=?";
			PreparedStatement prepStmt = (PreparedStatement) con
					.prepareStatement(selectStatement);
			prepStmt.setLong(1, userId);
			prepStmt.execute();
			LOGGER.info("Se han eliminado todas las valoraciones que hab??a realizado el usuario");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
