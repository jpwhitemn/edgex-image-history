package jpwhitemn.docker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class HistoryApp {

    private static Logger logger = LogManager.getLogger(HistoryApp.class);
    private static String CONFIG = "./config/config.properties";
	private static final Level LEVEL = Level.INFO;

    public static void main(String[] args) {
		Configurator.setRootLevel(LEVEL);
        Properties props = getConfig();
        executeTimer(Integer.parseInt(props.getProperty("interval")), props.getProperty("address"), props.getProperty("organization"));
    }
    
    private static void executeTimer(long interval, String address, String organization) {
    	TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
		        StatsPuller puller = new StatsPuller(address, organization);
		        puller.pullStats();				
			}
		};
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, interval);
    }

    private static Properties getConfig() {
        Properties props = new Properties();
        try {
            FileInputStream ip = new FileInputStream(CONFIG);
            props.load(ip);
        } catch (FileNotFoundException ex) {
        	logger.error("Configuration file not found", ex);
        } catch (IOException ioEx) {
            logger.error("Problem getting the configuration", ioEx);
        }
        return props;
    }

}
