package es.upm.dit.gsi.cron;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import es.upm.dit.gsi.database.DatabaseContent;
import es.upm.dit.gsi.logger.Logger;

/**
 * Creates a list of videos from rss feeds.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class GetContentFromSourcesJob implements Job {

	private static final String URL_ELMUNDO = "http://estaticos.elmundo.es/elmundo/rss/videos.xml";
	private static final Logger LOGGER = Logger.getLogger("cron.GetContentFromSourcesJob");

	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("STARTING feed cron job");
		try {
			URL feedUrl = new URL(URL_ELMUNDO);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();
			for (SyndEntry entry : entries) {
				MediaEntryModule m = (MediaEntryModule) entry.getModule(MediaEntryModule.URI);
				if (m != null && m.getMediaContents().length > 0) {
					for (int i = 0; i < m.getMediaContents().length; i++) {
						MediaContent mc = m.getMediaContents()[i];
						if (mc.getType() != null && mc.getType().equals("video/x-flv")) {
							LOGGER.info(mc.getReference().toString());

							// TODO meter videos obtenidos en BBDD

							// Aqu?? ya solo est??n las urls con videos, sacadas
							// de la portada de el mundo aunque
							// se puede iterar con mas URLs de aqui:
							// http://rss.elmundo.es/rss/

							// La URL en .flv del video: mc.getReference()
							// Los dem??s campos del feed, est??n en entry, por
							// ejemplo entry.getAuthor()

							String author = entry.getAuthor().toString();
							String title = entry.getTitle();
							Date date = entry.getPublishedDate();
							long time = date.getTime();
							String video = mc.getReference().toString();
							String capture = video.replace("cachevideos", "estaticos");
							capture = capture.replace(".flv", "_4.jpg");
							String content = entry.getDescription().getValue();
							content = content.substring(0, content.indexOf("&#160"));

							DatabaseContent.introduceContent(title, video, capture, time, content, author);

							// Ejemplo para verlo en accion en
							// http://localhost:8080/Recommender/getcontentfromsources:
							LOGGER.info("Author: " + entry.getAuthor() + "\nTitle: " + entry.getTitle() + "\nContent: "
									+ content + "\nVideo: " + mc.getReference() + "\n\n");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
