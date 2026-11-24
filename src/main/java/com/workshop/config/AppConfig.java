package com.workshop.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    public static final String APP_NAME = "Dimpra Autocar";
    public static final String APP_VERSION = "v1.0.0 Enterprise";
    public static final String WORKSHOP_NAME = "DIMPRA AUTOCAR";
    public static final String WORKSHOP_ADDRESS = "Jl. Raya Otista No. 128, Jakarta Timur";
    public static final String WORKSHOP_PHONE = "(021) 8590-7788 / WhatsApp: 0812-8899-1100";
    public static final String WORKSHOP_EMAIL = "contact@dimpra-autocar.co.id";
    public static final String WORKSHOP_TAX_ID = "01.889.324.7-005.000 (PKP PPN 11%)";

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getResourceAsStream("/database.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Could not load database.properties: " + e.getMessage());
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
