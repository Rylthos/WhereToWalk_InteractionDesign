package WhereToWalk.weather;

import WhereToWalk.JsonReader;

import java.net.*;
import java.text.*;
import java.io.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

import WhereToWalk.loc.LocationFinder;
import org.json.*;

public class WeatherForecast {
    // The furthest forecast we will make, in days.
    public static final int FORECAST_FURTHEST = 7;

    private JSONObject raw_data;
    // We avoid java.util.Date as most parts of it is deprecated.
    // Here we employ java.time.Instant from JDK 1.8.
    private HashMap<Instant, Weather> weathers;

    private double score;

    /*
     * This follows ISO8601, i.e. YYYY-MM-DDTHH:MM
     * where T is the letter 'T'
     */
    private Instant parseTime(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(str).toInstant();
        } catch (ParseException ex) {
            System.out.println("Parse exception for OpenMeteo");
            return null;
        }
    }

    /*
     * Return the current weather at the specified location at the current time
     */
    public Weather getWeatherNow() {
        return getWeatherAt(Instant.now());
    }

    /*
     * Get the weather forecast nearest, while after, the specified time.
     * Returns null if the time is too far from current.
     */
    public Weather getWeatherAt(Instant time) {
        Instant untruncated = time;
        time = time.truncatedTo(ChronoUnit.HOURS);

        // In case time is exactly aligned to an hour, we don't need to add anything
        if (!time.equals(untruncated)) {
            time = time.plusSeconds(3600);
        }

        if (!weathers.containsKey(time)) {
            return null;
        }

        return weathers.get(time);
    }

    /*
     * Return the score of the current weather
     */
    public double getScore() {
        return score;
    }

    /*
     * Calculate the score of the weather based on a metric that
     * depends on all the current weather condition based on how good the
     * weather is for hill walking
     */
    private double calcScore(Instant time) {
        double res = 0;
        for (int i = 0; i < 24; i++) { // current day score
            Weather weather = getWeatherAt(time.plusSeconds(i * 3600));
            double cc = weather.getCloudCoverage() / 100.;
            double p = weather.getPrecipitation();
            double t = weather.getTemperature();
            double ws = weather.getWindSpeed();
            res += (0.25 * Math.exp(-0.01 * (Math.pow((t - 17.5), 2))) +
                    0.25 * Math.exp(-0.1 * p) +
                    0.1 * Math.exp(-0.05 * ws) +
                    0.4 * Math.exp(-5 * cc));
        }
        res = res / 24;

        return res;
    }

    /*
     * Create a new WeatherForecast and get the weather located at 'lat' and 'lon'
     */
    @SuppressWarnings("deprecation")
    public WeatherForecast(double lat, double lon) {
        weathers = new HashMap<>();

        try {
            // Build the URL for Web API request
            // Documentation of OpenMeteo is at https://open-meteo.com/en/docs
            StringBuilder sb = new StringBuilder(String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&forecast_hours=%d&hourly=",
                    lat, lon, 24 * FORECAST_FURTHEST));

            for (String s : Weather.metrics) {
                sb.append(s);
                sb.append(",");
            }

            // Delete the final comma
            sb.deleteCharAt(sb.length() - 1);

            URL url = new URL(sb.toString());
            InputStream is = url.openStream();

            /**
             * Json format:
             * {
             * "hourly": {
             * "time": [...],
             * "precipitation": [...],
             * <!-- other metrics -->
             * }
             * }
             */
            // Retrieve the weather responce from the weather API
            raw_data = JsonReader.readJsonFromInputStream(is);
            JSONObject hourly = raw_data.getJSONObject("hourly");
            JSONArray time = hourly.getJSONArray("time");

            for (int i = 0; i < time.length(); i++) {
                HashMap<String, Double> map = new HashMap<>();
                for (String s : Weather.metrics) {
                    map.put(s, hourly.getJSONArray(s).getDouble(i));
                }

                Instant instant = parseTime(time.getString(i));
                weathers.put(instant, new Weather(map));
            }

            // Calculate scores according to weather

            score = calcScore(Instant.now());

        } catch (java.io.IOException e) {
            System.out.println("Failed to load weather");
        } catch (Exception ex) {
            System.out.println(ex.getClass().getName());
        }
    }
}
