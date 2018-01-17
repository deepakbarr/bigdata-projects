package com.demo.commons;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import redis.clients.jedis.Jedis;

/**
 * Created by dbarr on 12/22/17.
 */
public class WeatherUtil {

    private static final String DEFAULT_WEATHER = "NO WEATHER DATA";
    private static Jedis redisClient = new Jedis("localhost");
    private static final String REDIS_KEY_PREFIX = "WT.";
    private static final int REDIS_KEY_EXPIRY_SEC = 3600; //1 hour
    private static final OWM owm = new OWM("0b62fa8b773bccccff509e96aadd031a");
    private static final int GEO_PRECISION = 6;

    static {
        owm.setUnit(OWM.Unit.METRIC);
        String redisHost = System.getenv("REDIS_HOST");
        String redisPort = System.getenv("REDIS_PORT");

        if (null == redisHost || null == redisPort) {
            System.out.println("Environment variable REDIS.HOST and REDIS.PORT should be defined.");
            System.exit(1);
        }
        redisClient = new Jedis(redisHost, Integer.parseInt(redisPort));
    }

    public static String getWeatherInfo(String geoHash) {
        LatLong latLong = GeoHash.decodeHash(geoHash);
        return getWeatherInfo(latLong.getLat(), latLong.getLon());
    }

    public static String getWeatherInfo(double lat, double lon) {
        String geoHash = GeoHash.encodeHash(lat, lon, GEO_PRECISION);
        String weatherInfo = readWeatherFromCache(geoHash);

        if (null == weatherInfo) {
            String currentWeather = getCurrentWeather(lat, lon);
            if (currentWeather != null) {
                weatherInfo = currentWeather;
                writeWeatherToCache(geoHash, weatherInfo);
            } else weatherInfo = DEFAULT_WEATHER;
        }
        return weatherInfo;
    }

    private static String readWeatherFromCache(String key) {
        try {
            return redisClient.get(REDIS_KEY_PREFIX.concat(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String writeWeatherToCache(String key, String value) {
        try {
            return redisClient.setex(REDIS_KEY_PREFIX.concat(key), REDIS_KEY_EXPIRY_SEC, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String getCurrentWeather(double lat, double lon) {
        try {
            CurrentWeather cwd = owm.currentWeatherByCoords(lat, lon);
            if (cwd.hasRespCode() && cwd.getRespCode() == 200) {
                return extractInfo(cwd);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractInfo(CurrentWeather cwd) {

        StringBuilder sb = new StringBuilder();
//        System.out.println("cwd = " + cwd);

        if (cwd.hasMainData() && cwd.getMainData().hasTempMax() && cwd.getMainData().hasTempMin()) {
            // printing the max./min. temperature
            sb.append("T : "+cwd.getMainData().getTemp() + "\'C");

//            if (cwd.hasWindData()) {
//                double windSpeed = cwd.getWindData().getSpeed();
//                System.out.println("windSpeed = " + windSpeed);
//            }
            if (cwd.getMainData().hasHumidity()) {
                int humidity = cwd.getMainData().getHumidity();
                sb.append(", H : "+cwd.getMainData().getHumidity() + "%");
                System.out.println("Humidity = " + humidity);
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }


    public static void main(String[] args) {
        String geohash = "dr5rv6";
        System.out.println("weather = " + getWeatherInfo(geohash));
    }

//    public static void main(String[] args) throws APIException, InterruptedException {
//
//        // declaring object of "OWM" class
//        OWM owm = new OWM("0b62fa8b773bccccff509e96aadd031a");
//
//        owm.setUnit(OWM.Unit.METRIC);
//
//        int counter = 0;
//        double lat = 12.972442, lon = 77.580643;
//        while (true) {
//            // getting current weather data for the "London" city
//
//            lat += 0.01;
//            lon += 0.01;
//            CurrentWeather cwd = owm.currentWeatherByCoords(lat, lon);
//
//            System.out.println("cwd = " + cwd);
//            System.out.println("-----------------counter = " + counter++);
//            // checking data retrieval was successful or not
//            if (cwd.hasRespCode() && cwd.getRespCode() == 200) {
//
//                // checking if city name is available
//                if (cwd.hasCityName()) {
//                    //printing city name from the retrieved data
//                    System.out.println("City: " + cwd.getCityName());
//                }
//
//                // checking if max. temp. and min. temp. is available
//                if (cwd.hasMainData() && cwd.getMainData().hasTempMax() && cwd.getMainData().hasTempMin()) {
//                    // printing the max./min. temperature
//                    System.out.println("Temperature: " + cwd.getMainData().getTemp() + " deg. C");
//
//                    if (cwd.hasWindData()) {
//                        double windSpeed = cwd.getWindData().getSpeed();
//                        System.out.println("windSpeed = " + windSpeed);
//                    }
//                    if (cwd.getMainData().hasHumidity()) {
//                        int humidity = cwd.getMainData().getHumidity();
//                        System.out.println("humidity = " + humidity);
//                    }
//
//                }
//            }
//        }
}
