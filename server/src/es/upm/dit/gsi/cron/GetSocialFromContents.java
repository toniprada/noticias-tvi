package es.upm.dit.gsi.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import es.upm.dit.gsi.jdbc.Contents;
import es.upm.dit.gsi.logger.Logger;
import es.upm.dit.gsi.social.InfoTwitter;

public class GetSocialFromContents implements Job{

	private static final Logger LOGGER = Logger.getLogger("cron.GetSocialFromContents");

	public void execute(JobExecutionContext context) throws JobExecutionException {
		long contentId;
		int rateSocial;
		try {
			int contAct = Contents.getNumContents();
			while (contAct > 100) contAct = contAct - 10;
			for (int i = 0; i < Contents.getNumContents(); i++){
				contentId = Contents.getContentsIds().elementAt(i);
				String title = Contents.getTitleOfContent(contentId);
				//rateSocial = InfoFacebook.getLikesTot(title) + InfoTwitter.getPopularityTwitter(title);
				rateSocial = InfoTwitter.getPopularityTwitter(title);
				LOGGER.info("El número total de likes mas tweet de la noticia "+contentId+" es de "+rateSocial);
				Contents.introduceSocial(contentId, rateSocial);
			}
			LOGGER.info("Se ha actualizado toda la información social");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}


