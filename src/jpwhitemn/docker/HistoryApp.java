package jpwhitemn.docker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HistoryApp {

    private static Logger logger = LogManager.getLogger(Log4jExample.class);

    public static void main(String[] args) {
        Properties props = getConfig();
        // get configuration
        // on schedule - get docker stats
        // puts stats in db
        System.out.println("hello");
    }

    private static Properties getConfig() {
        Properties props = new Properties();
        try {
            FileInputStream ip = new FileInputStream("./config/config.properties");
            props.load(ip);
        } catch (FileNotFoundException ex) {
            System.out.println("configuration file not found");
        } catch (IOException ioEx) {
            System.out.println("problem getting the configuration");
        }
        return props;
    }

}