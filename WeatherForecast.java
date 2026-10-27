import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 5-day weather forecast for any city.
 * Uses the free Open-Meteo API (no API key needed). Requires Java 11+.
 *
 * Compile: javac WeatherForecast.java
 * Run:     java WeatherForecast Pune
 */
public class WeatherForecast {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Server returned HTTP " + response.statusCode());
        }
        return response.body();
    }

    // Pulls a single number like "latitude":18.5 out of the JSON text.
    private static double readNumber(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?[0-9.]+)").matcher(json);
        if (!m.find()) throw new RuntimeException("City not found");
        return Double.parseDouble(m.group(1));
    }

    // Pulls an array like "temperature_2m_max":[30.1,31.2] out of the JSON text.
    private static List<String> readArray(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)\\]").matcher(json);
        List<String> items = new ArrayList<>();
        if (m.find()) {
            for (String part : m.group(1).split(",")) {
                items.add(part.replace("\"", "").trim());
            }
        }
        return items;
    }

    // WMO weather codes -> plain English.
    private static String describe(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77) return "Snow";
        if (code >= 80 && code <= 82) return "Rain showers";
        if (code >= 95) return "Thunderstorm";
        return "Unknown";
    }

    public static void main(String[] args) {
        String city = args.length > 0 ? String.join(" ", args) : "Pune";

        try {
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?count=1&name="
                    + URLEncoder.encode(city, StandardCharsets.UTF_8);
            String geo = get(geoUrl);
            double lat = readNumber(geo, "latitude");
            double lon = readNumber(geo, "longitude");

            String forecastUrl = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + lat + "&longitude=" + lon
                    + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum"
                    + "&timezone=auto&forecast_days=5";
            String json = get(forecastUrl);

            List<String> dates = readArray(json, "time");
            List<String> codes = readArray(json, "weather_code");
            List<String> highs = readArray(json, "temperature_2m_max");
            List<String> lows = readArray(json, "temperature_2m_min");
            List<String> rain = readArray(json, "precipitation_sum");

            System.out.println("5-day forecast for " + city);
            System.out.println("-".repeat(62));
            System.out.printf("%-12s %-16s %8s %8s %10s%n", "Date", "Conditions", "High C", "Low C", "Rain mm");
            System.out.println("-".repeat(62));

            for (int i = 0; i < dates.size(); i++) {
                int code = (int) Double.parseDouble(codes.get(i));
                System.out.printf("%-12s %-16s %8s %8s %10s%n",
                        dates.get(i), describe(code), highs.get(i), lows.get(i), rain.get(i));
            }
        } catch (Exception e) {
            System.out.println("Could not get the forecast: " + e.getMessage());
        }
    }
}
