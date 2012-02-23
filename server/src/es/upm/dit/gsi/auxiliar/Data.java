package es.upm.dit.gsi.auxiliar;

import es.upm.dit.gsi.jdbc.Users;
import es.upm.dit.gsi.logger.Logger;

public class Data {
	private static final Logger LOGGER = Logger.getLogger("jdbc.Data");
	
	public static final void introduceData(){
		/*Contents.introduceContent("Las preguntas, con megáfonos","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/espana/1320878161_extras_video.flv","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/espana/1320878161_extras_video_7.jpg","10/11/2011 08:30","El PSOE denuncia en un nuevo vídeo que Mariano Rajoy no contesta a nada","Manuel Sánchez");
		Contents.introduceContent("Miradas al fresco","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/ocio/1320843697_extras_video.flv","http://estaticos.elmundo.es/elmundo/imagenes/2011/11/09/ocio/1320843697_g_1.jpg","10/11/2011 05:05","Adentrarse en la exposición 'El Barroco. Teatro de las pasiones' no es sólo ver un 'ribera', un 'giordano' o un 'reni'. Unas obras, además, que habitualmente están encerradas en un palacio privado de Venecia, el mismo que acogió en su día el taller de Tiziano","Virginia Hernández");
		Contents.introduceContent("Prohíben el acceso","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/espana/1320862573_extras_video.flv","http://estaticos.elmundo.es/elmundo/imagenes/2011/11/09/espana/1320862573_g_0.jpg","09/11/2011 20:05","Las calas de Tacorón y Puerto Naos, al sur de la isla del Hierro han sido cerradas al público. El Plan de Protección Civil por Riesgo Volcánico de Canarias (Pevolca) ha tomado la decisión tras recibir los informes científicos pertinentes","Luigi Benedicto Borges");
		Contents.introduceContent("Romario","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/espana/1320862573_extras_video.flv","http://estaticos03.cache.el-mundo.net/america/imagenes/2011/10/18/deportes/1318964294_0.jpg","18/10/2011 14:58","El ex futbolista brasileño Romario dijo este martes que el argentino Lionel Messi, jugador del Barcelona español, puede aprender algunas cosas si ve los vídeos que le prometió Pelé.","Efe");
		Contents.introduceContent("Tuiteando su propia boda","http://www.youtube.com/embed/mLXD1vjAXw4","","07/11/2011 17:20 ","Podría haber salido de una película, pero probablemente lo habríamos considerado demasiado exagerado: la novia se saca el móvil del escote y empieza a escribir mensajes mientras se está casando.","Pablo Pardo");
		Contents.introduceContent("Los mejores bármanes de España se citan en Valencia","http://estaticos.elmundo.es/elmundo/videos/2011/11/10/valencia/1320910557_extras_video.flv","http://estaticos.elmundo.es/elmundo/imagenes/2011/11/10/valencia/1320910557_g_0.jpg","10/11/2011 08:48 ","El Campeonato Nacional de Coctelería reunirá a 16 jefes de bar y a otros tantos jóvenes bármanes de varias comunidades, que presentarán sus creaciones en el marco del 58 Congreso Nacional de Coctelería, donde los expertos analizarán y darán a conocer las nuevas tendencias del mercado de este tipo de bebida.","Efe");
		Contents.introduceContent("Ray Loriga se adentra en la literatura juvenil con El Bebedor de Lágrimas","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320868014_extras_video.flv","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320868014_extras_video_8.jpg","09/11/2011 20:55","Todo comienza cuando Adela, una estudiante becada, llega a la universidad de Carnwell, deseosa de tomar las riendas de su vida y llegar a ser la persona que siempre quiso ser. Lo que se encontrará allí, sin embargo, será una leyenda y una maldición extrañas que terminarán por atraparla en una trama de asesinatos que deberá investigar el agente Warden pero que tienen bastante de sobrenatural.","Beatriz Rucabado");
		Contents.introduceContent("Los mejores videojuegos del año, premiados en Bilbao","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320856990_extras_video.flv","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320856990_extras_video_6.jpg","09/11/2011 17:50","La gala de los mejores videojuegos del año se celebra estos días en Bilbao. Es el Fan & Serious Game Festival, que premia a los juegos de ocio y a los formativos más importantes del momento.","Atlas");
		Contents.introduceContent("Las instituciones unifican el sistema de evaluación de daños por las lluvias","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320851978_extras_video.flv","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/paisvasco/1320851978_extras_video_7.jpg","09/11/2011 17:09 ","El Gobierno Vasco, la Diputación de Gipuzkoa y los municipios afectados por las inundaciones del pasado fin de semana han unificado este miércoles el sistema de información y evaluación de daños, con el fin de disponer de una valoración concreta y global de los efectos de las riadas.","Atlas");
		Contents.introduceContent("La Policía salva la vida de una familia a punto de fallecer por 'muerte dulce'","http://estaticos.elmundo.es/elmundo/videos/2011/11/08/madrid/1320742027_extras_video.flv","http://estaticos.elmundo.es/elmundo/videos/2011/11/08/madrid/1320742027_extras_video_3.jpg","08/11/2011 17:30","La Policía salva la vida de una familia a punto de fallecer por 'muerte dulce'","Efe");
		Contents.introduceContent("Morante se fuma un puro","http://estaticos.elmundo.es/elmundo/videos/2011/11/09/andalucia_sevilla/1320869289_extras_video.flv","http://estaticos.elmundo.es/elmundo/imagenes/2011/11/10/andalucia_sevilla/1320869289_g_0.jpg","10/11/2011 08:26","Hay pocas cosas más estéticas que todo cuanto rodea al mundo del toro. Del campo a la plaza, pasando por el mito que acarrea la última generación de toreros-héroes de masas, todo es plástica en un mundo que, en muchas ocasiones, justifica en esta cuestión parte de su existencia.","Efe");
		*/
		Users.introduceUser("adri");
		Users.introduceUser("Carlos");
		Users.introduceUser("Toni");
		Users.introduceUser("Jose");
		Users.introduceUser("Lourdes");
		Users.introduceUser("Tamara");
		Users.introduceUser("Aida");
		Users.introduceUser("Ivan");
		
		LOGGER.info("Datos de contenidos y usuarios introducidos en la base de datos");
	}	

}











