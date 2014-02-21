package es.upm.dit.gsi.logger;

import org.apache.log4j.Logger;

import es.upm.dit.gsi.logger.LoggerService;


public class LoggerService {
	
	/**
	 * Crea una traza con nivel debug. Recibe como par??metros el objeto
	 * que genera la traza y el mensaje a tracear: debug(this, "mensaje")  
	 * @param clazz
	 * @param message
	 */
	@SuppressWarnings("rawtypes")
	public void debug(Class clazz, Object message){
		Logger.getLogger(clazz).debug(message);
	}

	@SuppressWarnings("rawtypes")
	public void debug(Class clazz, String message, Throwable exception){
		Logger.getLogger(clazz).debug(message);
		Logger.getLogger(clazz).debug(exception);
	}
	/**
	 * Crea una traza con nivel info. Recibe como par??metros el objeto
	 * que genera la traza y el mensaje a tracear: info(this, "mensaje")  
	 * @param clazz
	 * @param message
	 */
	@SuppressWarnings("rawtypes")
	public void info(Class clazz, Object message) {
		Logger.getLogger(clazz).info(message);
	}
	
	@SuppressWarnings("rawtypes")
	public void info(Class clazz, String message, Throwable exception) {
		Logger.getLogger(clazz).info(message);
		Logger.getLogger(clazz).info(exception);
	}

	/**
	 * Crea una traza con nivel warn. Recibe como par??metros el objeto
	 * que genera la traza y el mensaje a tracear: warn(this, "mensaje")  
	 * @param clazz
	 * @param message
	 */
	@SuppressWarnings("rawtypes")
	public void warn(Class clazz, Object message){
		Logger.getLogger(clazz).warn(message);
	}
	@SuppressWarnings("rawtypes")
	public void warn(Class clazz, String message, Throwable exception){
		Logger.getLogger(clazz).warn(message);
		Logger.getLogger(clazz).warn(exception);
	}
	/**
	 * Crea una traza con nivel error. Recibe como par??metros el objeto
	 * que genera la traza y el mensaje a tracear: error(this, "mensaje")  
	 * @param clazz
	 * @param message
	 */
	@SuppressWarnings("rawtypes")
	public void error(Class clazz, Object message){	
		if (message instanceof Throwable)
		{
			Logger.getLogger(clazz).error(((Throwable)message).getMessage(), (Throwable)message);
		}
		else
		{
			Logger.getLogger(clazz).error(message);
		}
	}
	@SuppressWarnings("rawtypes")
	public void error(Class clazz, String message, Throwable exception){	
		Logger.getLogger(clazz).error(message);
		Logger.getLogger(clazz).error(exception);
	}
	/**
	 * Crea una traza con nivel fatal. Recibe como par??metros el objeto
	 * que genera la traza y el mensaje a tracear: fatal(this, "mensaje")  
	 * @param clazz
	 * @param message
	 */
	@SuppressWarnings("rawtypes")
	public void fatal(Class clazz, Object message){
		Logger.getLogger(clazz).fatal(message);
	}

	@SuppressWarnings("rawtypes")
	public void fatal(Class clazz, String message, Throwable exception){
		Logger.getLogger(clazz).fatal(message);
		Logger.getLogger(clazz).fatal(exception);
	}
}
