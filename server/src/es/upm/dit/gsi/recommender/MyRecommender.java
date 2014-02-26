package es.upm.dit.gsi.recommender;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
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

import es.upm.dit.gsi.database.DatabaseHandler;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.util.Constants;

public class MyRecommender {
	
	
	private static final Logger LOGGER = Logger.getLogger("Recommmender");

	
	public DatabaseHandler databaseHandler;

	private JDBCDataModel dataModel;
	// User based
	private UserNeighborhood userNeighborhood;
	private UserSimilarity userSimilarity;
	private Recommender userBasedrecommender;
	// Content based
	private ItemSimilarity itemSimilarity;
	private Recommender contentBasedrecommender;

	public MyRecommender() {
		databaseHandler = DatabaseHandler.getInstance();
		dataModel = new MySQLJDBCDataModel(databaseHandler.getDataSource(), "preferenceTable", "user_id", "content_id", "preference", null);
		LOGGER.info("datamodel es " + dataModel);
		try {
			// User based
			userSimilarity = new PearsonCorrelationSimilarity(dataModel);
			userNeighborhood = new NearestNUserNeighborhood(2, userSimilarity, dataModel);
			userBasedrecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
			// Content based
			itemSimilarity = new TanimotoCoefficientSimilarity(dataModel);
			contentBasedrecommender = new GenericBooleanPrefItemBasedRecommender(dataModel, itemSimilarity);
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
	
	public List<RecommendedItem> getRecommendations(long userId) throws TasteException {
		if (shouldUseUserBased(userId)) {
			return userBasedrecommender.recommend(userId, Constants.NUM_RESULTS);
		} else {
			return contentBasedrecommender.recommend(userId, Constants.NUM_RESULTS);
		}
	}
	
	public float estimatePreference(long userId, long contentId) throws TasteException {
		if (shouldUseUserBased(userId)) {
			return userBasedrecommender.estimatePreference(userId, contentId);
		} else {
			return contentBasedrecommender.estimatePreference(userId, contentId);
		}
	}
	
	public Float getPreferenceValue(long userId, long id) throws TasteException {
		return dataModel.getPreferenceValue(userId, id);
	}

	
	private boolean shouldUseUserBased(long userId) {
		boolean should = userId%2==0;
		System.out.println("Using user based content: " + should);
		return should;
	}
	
	public void removePreference(Long contentId, Long userId) throws TasteException {
		dataModel.removePreference(contentId, userId);
	}
	
	public void setPreference(Long userId, Long contentId, float preference) throws TasteException {
		dataModel.setPreference(userId, contentId, preference);
	}
	
	public PreferenceArray getPreferencesFromUser(Long userId) throws TasteException {
		return dataModel.getPreferencesFromUser(userId);
	}


	
	
	
	
}
