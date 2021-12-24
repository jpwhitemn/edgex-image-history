package jpwhitemn.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import jpwhitemn.docker.model.DockerHubResult;

public class StatsPuller {

	private String address;
	private String organization;

	private static final String METHOD = "GET";
	private static final String NEXT = "next";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String STARS = "star_count";
	private static final String PULLS = "pull_count";
	private static final String ARCHIVED = "ARCHIVED";

	private static Logger logger = LogManager.getLogger(StatsPuller.class);

	public StatsPuller(String address, String organization) {
		this.address = address;
		this.organization = organization;
	}

	public void pullStats() {
		request(address + organization);
		logger.info("Docker Hub Stats capture complete");
	}

	private void request(String url) {
		logger.debug("Calling Docker Hub with: " + url);
		String next = extract(callDockerHub(url));
		if (next != null)
			request(next);
	}

	private String callDockerHub(String address) {
		try {
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(METHOD);
			int status = con.getResponseCode();
			if (status == 200) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
					return in.readLine();
				}
			} else {
				logger.error("Response code from stats service is not HTTP OK.  Code returned is: " + status);
			}
		} catch (IOException e) {
			logger.error("Unable to make request to stats service", e);
		}
		return null;
	}

	private String extract(String json) {
		if (json != null) {
			JSONObject obj = new JSONObject(json);
			deserialize(obj.getJSONArray("results"));
			if (obj.isNull(NEXT))
				return null;
			else
				return obj.getString(NEXT);
		}
		logger.error("Sent a null JSON string to deserialize");
		return null;
	}

	private void deserialize(JSONArray array) {
		DockerHubResult result;
		for (Object o : array) {
			JSONObject obj = (JSONObject) o;
			result = new DockerHubResult(obj.getString(NAME), obj.getString(DESCRIPTION), obj.getLong(STARS),
					obj.getLong(PULLS));
			if (result.getDescription().contains(ARCHIVED)) {
				result.setArchived(true);
				logger.debug("Saving stats for: " + result.getName() + "(archived) : " + result.getPullCount());
			} else {
				logger.debug("Saving stats for: " + result.getName() + " : " + result.getPullCount());
			}
			persistResult(result);
		}

	}

	private void persistResult(DockerHubResult result) {
		// TODO BATCH
		InfluxDBClient influxDB = InfluxDBClientFactory.create("http://192.168.0.39:8086",
				"T1KaXLC_5lCiy8-nUHX7WLB7ra-hFc5y43kPVCS7AR6xGopkMuEcoZRtfi96jBsIT92OAdMIBT_5BUy3xggtWg=="
						.toCharArray(),
				"test", "edgexdockerstats");
		WriteApiBlocking writeApi = influxDB.getWriteApiBlocking();
		Point point = Point.measurement("stat").addTag("name", result.getName()).addTag(ARCHIVED, Boolean.toString(result.isArchived()))
				.addField("count", result.getPullCount()).addField("stars", result.getStars())
				.time(Instant.now(), WritePrecision.S);
		writeApi.writePoint(point);
		influxDB.close();
	}
}
