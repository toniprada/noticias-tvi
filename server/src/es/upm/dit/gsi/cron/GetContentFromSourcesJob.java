package es.upm.dit.gsi.cron;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import es.upm.dit.gsi.database.ContentDB;
import es.upm.dit.gsi.logger.Logger;

/**
 * Creates a list of videos from rss feeds.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class GetContentFromSourcesJob implements Job {

	private static final String URL_ELPAIS = "http://ep00.epimg.net/rss/tags/o_video.xml";
	private static final Logger LOGGER = Logger.getLogger("cron.GetContentFromSourcesJob");

	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("STARTING feed cron job");
		try {
			URL feedUrl = new URL(URL_ELPAIS);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();
			for (SyndEntry entry : entries) {
				String author = entry.getAuthor().toString();
				String title = entry.getTitle();
				Date date = entry.getPublishedDate();
				long time = date.getTime();
				String content = entry.getDescription().getValue();
				String capture = "";
				String video = "";
				@SuppressWarnings("unchecked")
				List<SyndEnclosureImpl> enclosures = entry.getEnclosures();
				for (SyndEnclosureImpl enclosure: enclosures) {
					String url = enclosure.getUrl();
					if (url.endsWith(".jpg")) {
						capture = url;
					} else if (url.endsWith(".mp4")) {
						video = url;
					}
				}
				if (!video.equals("") && !capture.equals("")) {
					ContentDB.introduceContent(title, video, capture, time, content, author);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
