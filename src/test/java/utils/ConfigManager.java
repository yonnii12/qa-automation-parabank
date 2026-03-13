package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                // Aquí ya no ponemos defaults, dejamos que el usuario maneje el error si falta el archivo
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene una propiedad del archivo de configuración.
     * @param key La clave de la propiedad.
     * @return El valor de la propiedad, o null si no existe.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}