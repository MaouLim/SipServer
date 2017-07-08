package bupt.networks.sip;

import bupt.util.Configuration;

import java.util.Properties;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipConfigBuilder {

    public static final String PROPERTY_STACK_NAME  = "javax.sip.STACK_NAME";
    public static final String PROPERTY_IP_ADDRESS  = "javax.sip.IP_ADDRESS";
    public static final String PROPERTY_TRACE_LEVEL = "gov.nist.javax.sip.TRACE_LEVEL";
    public static final String PROPERTY_SERVER_LOG  = "gov.nist.javax.sip.SERVER_LOG";
    public static final String PROPERTY_DEBUG_LOG   = "gov.nist.javax.sip.DEBUG_LOG";

    public static Properties readFrom(Configuration config) {
        Properties properties = new Properties();

        if (config.containKey(PROPERTY_STACK_NAME)) {
            properties.setProperty(
                PROPERTY_STACK_NAME,
                (String) config.get(PROPERTY_STACK_NAME)
            );
        }

        if (config.containKey(PROPERTY_IP_ADDRESS)) {
            properties.setProperty(
                PROPERTY_IP_ADDRESS,
                (String) config.get(PROPERTY_IP_ADDRESS)
            );
        }

        if (config.containKey(PROPERTY_TRACE_LEVEL)) {
            properties.setProperty(
                PROPERTY_TRACE_LEVEL,
                (String) config.get(PROPERTY_TRACE_LEVEL)
            );
        }

        if (config.containKey(PROPERTY_SERVER_LOG)) {
            properties.setProperty(
                PROPERTY_SERVER_LOG,
                (String) config.get(PROPERTY_SERVER_LOG)
            );
        }

        if (config.containKey(PROPERTY_DEBUG_LOG)) {
            properties.setProperty(
                PROPERTY_DEBUG_LOG,
                (String) config.get(PROPERTY_DEBUG_LOG)
            );
        }

        return properties;
    }
}
