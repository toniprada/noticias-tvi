//   Copyright 2011 UPM-GSI: Group of Intelligent Systems -
//   - Universidad Politécnica de Madrid (UPM)
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package es.upm.dit.gsi.noticiastvi.gtv.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.upm.dit.gsi.noticiastvi.gtv.item.Video;
import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

/**
 * Get the list of videos
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public abstract class GetItemsThread extends Thread {

	private static final boolean TEST = Constant.TEST;
	public static final int RESULT_OK = 20;
	public static final int RESULT_ERROR = 21;

	private Handler handler;

	public GetItemsThread(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		ArrayList<Video> videos;
		try {
			if (TEST) {
				videos = getTestVideos();
				handler.sendMessage(Message.obtain(handler, RESULT_OK, videos));
			} else {
				InputStream source = retrieveStream();
				if (source != null) {
					Gson gson = new Gson();
					Reader reader = new InputStreamReader(source);
					Type type = new TypeToken<ArrayList<Video>>() {
					}.getType();
					videos = gson.fromJson(reader, type);
					handler.sendMessage(Message.obtain(handler, RESULT_OK, videos));
				} else {
					handler.sendEmptyMessage(RESULT_ERROR);
				}
			}
		} catch (Exception e) {
			handler.sendEmptyMessage(RESULT_ERROR);
			Log.e("ERROR", e.getMessage());
		}
	}

	private InputStream retrieveStream() {

		DefaultHttpClient client = new DefaultHttpClient();

		HttpGet getRequest = getRequest();

		try {

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode);
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		} catch (IOException e) {
			getRequest.abort();
			Log.w(getClass().getSimpleName(), "Error", e);
		}

		return null;

	}

	public abstract HttpGet getRequest();

	private ArrayList<Video> getTestVideos() {
		// TODO Get videos:
		ArrayList<Video> videos = new ArrayList<Video>();
		videos
		.add(new Video(
				"Melancholia",
				"elmundo.es",
				"Melancholia es una película de ciencia ficción sobre la destrucción total del mundo, realizada por el director danés Lars von Trier, que se estrenó en 2011 en el Festival de Cannes de ese año.",
				"http://es.clip-1.filmtrailer.com/7092_27265_a_5.flv?log_var=272|341100078-1",
				"http://1.bp.blogspot.com/-4jnJ3RgL9TY/TofK8UoZJVI/AAAAAAAAEVY/YHtglxBYpOM/s1600/cinemaniablog_melancholia_01.jpg",
				2.5));
//		videos
//		.add(new PieceOfNews(
//				"Los rinocerontes negros voladores de Sudáfrica",
//				"El Mundo - Pablo Pardo | Washington",
//				"Los rinocerontes vuelan en Sudáfrica. O, al menos, 19 de ellos han volado durante más de 10 minutos, anestesiados y suspendidos por los tobillos de un helicóptero. Una operación tan espectacular como eficaz para trasladar a estos animales, que pesan entre 800 y 1.400 kilos, de una zona inaccesible por carretera en la provincia de Eastern Cape, en el extremo meridional de Sudáfrica, a la de Northern Limpopo, justo en la otra punta del país.\n\n Tras el paseo aéreo los animales fueron colocados en camiones y trasladados a su nuevo hogar, un área establecida por la organización World Wildlife Fund en la que los propietarios de granjas y terrenos de cultivo se comprometen a tener rinocerontes. \n\nLa idea de aerotransportar colgados por las patas a estos animales suena espectacular pero, según los responsables de la operación, es de hecho menos dañina para los rinocerontes que llevarlos en camión por carreteras infames o meterlos en redes que son colgadas de helicópteros. El proyecto también ha recibido una formidable publicidad gracias a las espectaculares imágenes y vídeos colgados en Internet por la empresa sudafricana de producción audiovisual de defensa del medio ambiente Green Rennaisance.\n\n Sudáfrica tiene aproximadamente 9.500 rinocerontes, es decir, alrededor del 80% de los que quedan en África. De ellos, alrededor de 7.000 son rinocerontes blancos, aproximadamente el doble de grandes que los negros que han sido protragonistas de este ‘puente aéreo’. En lo que va de año han sido cazados ilegalmente 341 animales de las dos especies en ese país. Eso supone que ya se ha superado la cifra de 2010 (333 animales).",
//				"http://www.elmundo.es/elmundo/2011/11/13/natura/1321183435.html",
//				"http://img.irtve.es/imagenes/rinocerontes-negros-voladores-sudafrica/1321283251050.jpg",
//				2.5));
		videos
		.add(new Video(
				"The Amazing Spiderman",
				"RTVE",
				"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
						+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
						+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
				"http://www.tools4movies.com/dvd_catalyst_profile_samples/The%20Amazing%20Spiderman%20bionic%20fast.mp4",
				"http://www.tools4movies.com/dvd_catalyst_profile_samples/The%20Amazing%20Spiderman%20bionic%20fast.jpg",
				2.5));
		videos.add(new Video("Title1", "subtitle1", "no reproduce",
				"http://www.atreestory.com/movies/Old-Growth-Series.mpg",
				"http://a.images.blip.tv/Contenidistas-LaProporcinUrea934.gif",
				2.5));
		videos
				.add(new Video(
						"Title2",
						"subtitle2",
						"320x180",
						"http://www.yo-yo.org/mp4/yu3.mp4",
						"http://www.ponkrocks.com/mskav/propos.jpg",
						2.5));
		videos
				.add(new Video(
						"Title3",
						"subtitle3",
						"720x360",
						"http://www.yo-yo.ohttp://138.4.3.224:8080/Recommender/mahout?action=getRecommendation&identifier=toni&max_recom=3rg/mp4/yu5.mp4",
						"http://sp8.fotolog.com/photo/8/58/67/leonzy_13/1236712894358_f.jpg",
						2.5));
		videos
				.add(new Video(
						"Title4",
						"Estreno el 4 de noviembre",
						"Lars von trieeeeeeeeeeeeeeeeeeeeeeeeeeeeer",
						"http://estaticos.elmundo.es/elmundo/videos/2011/11/10/valencia/1320910557_extras_video.flv",
						"http://1.bp.blogspot.com/-8dXKTvo0bKs/TccQMGLDVFI/AAAAAAAAC7Y/C65EeWNzDuU/s1600/PabloPicasso-Bulls-Head-1943.jpg",
						2.5));
		videos
				.add(new Video(
						"Video de prueba",
						"Esto es un video de prueba, ni mas ni menos",
						"Descripcion: aqui va un texto largo. Lorem ipsum dolor sit amet, "
								+ "consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
								+ "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
								+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
								+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
						"http://www.yo-yo.org/mp4/yu3.mp4",
						"http://almamagazine.com/images/biblioteca/fotoreportajes/world_press_photo_09/fr_world_press_photo.jpg",
						2.5));
		videos
				.add(new Video(
						"The Amazing Spiderman [HQ]",
						"Agencia EFE",
						"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
								+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
								+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
						"http://www.tools4movies.com/dvd_catalyst_profile_samples/The%20Amazing%20Spiderman%20tablet%20hq2.mp4",
						"http://www.tools4movies.com/dvd_catalyst_profile_samples/The%20Amazing%20Spiderman%20tablet%20hq2.jpg",
						2.5));
		// Simulate some network delay:
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return videos;
	}
}
