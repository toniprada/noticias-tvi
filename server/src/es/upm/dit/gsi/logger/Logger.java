package es.upm.dit.gsi.logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Logger {

    /**
     * Nivel m??ximo de traza: no se traza nada.
     */
    public static final int OFF = Integer.MAX_VALUE;

    /**
     * Nivel de traza: WARNING &lt; SEVERE &lt; OFF.
     */
    public static final int SEVERE = 1000;

    /**
     * Nivel de traza: INFO &lt; WARNING &lt; SEVERE.
     */
    public static final int WARNING = 900;

    /**
     * Nivel de traza: CONFIG &lt; INFO &lt; WARNING.
     */
    public static final int INFO = 800;

    /**
     * Nivel de traza: FINE &lt; CONFIG &lt; INFO.
     */
    public static final int CONFIG = 700;

    /**
     * Nivel de traza: FINER &lt; FINE &lt; CONFIG.
     */
    public static final int FINE = 500;

    /**
     * Nivel de traza: FINEST &lt; FINER &lt; FINE.
     */
    public static final int FINER = 400;

    /**
     * Nivel de traza: ALL &lt; FINEST &lt; FINER.
     */
    public static final int FINEST = 300;

    /**
     * Nivel m??nimo de traza: se traza todo.
     */
    public static final int ALL = Integer.MIN_VALUE;

    private static final Map<String, Logger> loggers = new HashMap<String, Logger>();
    private static final Properties properties = new Properties();
    private static int default_console_level = INFO;
    private static PrintStream console = System.err;

    private String name;
    private int console_level;

    private static int getLevel(String code, int def) {
        if (code == null) {
            return def;
        }
        code = code.trim();
        // noinspection EmptyCatchBlock
        try {
            return Integer.parseInt(code);
        } catch (Exception e) {
        }
        if (code.equalsIgnoreCase("OFF")) {
            return OFF;
        }
        if (code.equalsIgnoreCase("SEVERE")) {
            return SEVERE;
        }
        if (code.equalsIgnoreCase("WARNING")) {
            return WARNING;
        }
        if (code.equalsIgnoreCase("INFO")) {
            return INFO;
        }
        if (code.equalsIgnoreCase("CONFIG")) {
            return CONFIG;
        }
        if (code.equalsIgnoreCase("FINE")) {
            return FINE;
        }
        if (code.equalsIgnoreCase("FINER")) {
            return FINER;
        }
        if (code.equalsIgnoreCase("FINEST")) {
            return FINEST;
        }
        if (code.equalsIgnoreCase("ALL")) {
            return ALL;
        }
        return INFO;
    }

    private static String getLevelString(int level) {
        if (level >= SEVERE) {
            return " [severe]: ";
        }
        if (level >= WARNING) {
            return " [warning]: ";
        }
        if (level >= INFO) {
            return " [info]: ";
        }
        if (level >= CONFIG) {
            return " [config]: ";
        }
        if (level >= FINE) {
            return " [fine]: ";
        }
        if (level >= FINER) {
            return " [finer]: ";
        }
        if (level >= FINEST) {
            return " [finest]: ";
        }
        return " []: ";
    }
    
    private int getMyLevel(String name) {
        String p = properties.getProperty(name);

        if (p != null) {
            return getLevel(p, INFO);
        }
        p = properties.getProperty(name + ".level");
        if (p != null) {
            return getLevel(p, INFO);
        }
        int dot = name.lastIndexOf('.');

        while (dot > 0) {
            name = name.substring(0, dot);
            p = properties.getProperty(name);
            if (p != null) {
                return getLevel(p, INFO);
            }
            p = properties.getProperty(name + ".level");
            if (p != null) {
                return getLevel(p, INFO);
            }
            dot = name.lastIndexOf('.');
        }
        return ALL;
    }

    /**
     * Crea un logger.
     * Se trata de que para cada nombre el logger sea ??nico.
     *
     * @param name Nombre del logger.
     * @return un objeto Logger
     */
    public static Logger getLogger(String name) {
        if (name == null || name.length() == 0) {
            name = "default";
        }
        Logger logger = loggers.get(name);

        if (logger == null) {
            logger = new Logger(name);
            loggers.put(name, logger);
        }
        return logger;
    }

    private Logger(String name) {
        this.name = name;
        int myLevel = getMyLevel(name);

        console_level = Math.max(myLevel, default_console_level);
    }

    /**
     * Marca un nivel de aceptaci??n de trazas.
     * Se imprimen por consola las trazas de nivel
     * mayor o igual que el marcado.
     *
     * @param level Nivel de traza.
     */
    public void acceptConsoleLevel(int level) {
        console_level = Math.min(console_level, level);
    }

    /**
     * Traza a nivel SEVERE.
     *
     * @param msg Mensaje de la traza.
     */
    public final void severe(String msg) {
        log(SEVERE, msg);
    }

    /**
     * Traza a nivel SEVERE.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void severe(String msg1, String msg2) {
        log(SEVERE, msg1, msg2);
    }

    /**
     * Traza a nivel WARNING.
     *
     * @param msg Mensaje de la traza.
     */
    public final void warning(String msg) {
        log(WARNING, msg);
    }

    /**
     * Traza a nivel WARNING.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void warning(String msg1, String msg2) {
        log(WARNING, msg1, msg2);
    }

    /**
     * Traza a nivel INFO.
     *
     * @param msg Mensaje de la traza.
     */
    public final void info(String msg) {
        log(INFO, msg);
    }

    /**
     * Traza a nivel INFO.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void info(String msg1, String msg2) {
        log(INFO, msg1, msg2);
    }

    /**
     * Traza a nivel CONFIG.
     *
     * @param msg Mensaje de la traza.
     */
    public final void config(String msg) {
        log(CONFIG, msg);
    }

    /**
     * Traza a nivel CONFIG.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void config(String msg1, String msg2) {
        log(CONFIG, msg1, msg2);
    }

    /**
     * Traza a nivel FINE.
     *
     * @param msg Mensaje de la traza.
     */
    public final void fine(String msg) {
        log(FINE, msg);
    }

    /**
     * Traza a nivel FINE.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void fine(String msg1, String msg2) {
        log(FINE, msg1, msg2);
    }

    /**
     * Traza a nivel FINER.
     *
     * @param msg Mensaje de la traza.
     */
    public final void finer(String msg) {
        log(FINER, msg);
    }

    /**
     * Traza a nivel FINER.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void finer(String msg1, String msg2) {
        log(FINER, msg1, msg2);
    }

    /**
     * Traza a nivel FINEST.
     *
     * @param msg Mensaje de la traza.
     */
    public final void finest(String msg) {
        log(FINEST, msg);
    }

    /**
     * Traza a nivel FINEST.
     *
     * @param msg1 Mensaje de la traza (parte 1).
     * @param msg2 Mensaje de la traza (parte 2).
     */
    public final void finest(String msg1, String msg2) {
        log(FINEST, msg1, msg2);
    }

    /**
     * Chequeo de si una traza de un cierto nivel aparecer??a en consola.
     *
     * @param level Nivel por el que se pregunta.
     * @return TRUE si aparecer??a en consola.
     */
    public final boolean inConsole(int level) {
        return level >= console_level;
    }

    /**
     * Genera una traza del nivel indicado.
     *
     * @param level Nivel de la traza.
     */
    public final void log(int level) {
        if (level >= console_level) {
            console.println(name);
        }
    }

    /**
     * Genera una traza del nivel indicado.
     *
     * @param level Nivel de la traza.
     * @param msg   Mensaje de la traza.
     */
    public final void log(int level, String msg) {
        if (level >= console_level) {
            console.print(name);
            console.print(getLevelString(level));
            console.println(msg);
        }
    }

    /**
     * Genera una traza del nivel indicado.
     *
     * @param level Nivel de la traza.
     * @param msg1  Mensaje de la traza (parte 1).
     * @param msg2  Mensaje de la traza (parte 2).
     */
    public final void log(int level, String msg1, String msg2) {
        if (level >= console_level) {
            console.print(name);
            console.print(getLevelString(level));
            console.print(msg1);
            console.print(' ');
            console.println(msg2);
        }
    }

    /**
     * Genera una traza del nivel indicado.
     *
     * @param level     Nivel de la traza.
     * @param msg       Mensaje de la traza.
     * @param throwable Excepci??n que se quiere trazar.
     */
    public final void log(int level, String msg, Throwable throwable) {
        if (level >= console_level) {
            console.print(name);
            console.print(getLevelString(level));
            console.println(msg);
            throwable.printStackTrace(console);
        }
    }
}