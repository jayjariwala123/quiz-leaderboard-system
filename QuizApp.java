import java.net.URI;
import java.net.http.*;
import java.util.*;

public class QuizApp {

    static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    static final String REG_NO = "RA2311043010005"; 

    public static void main(String[] args) {
        try {

            HttpClient client = HttpClient.newHttpClient();

            Set<String> seen = new HashSet<>();
            Map<String, Integer> scores = new HashMap<>();

            // Poll 10 times
            for (int i = 0; i < 10; i++) {

                System.out.println("Polling: " + i);

                String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + i;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());

                String body = response.body();

                //  Parse events manually
                List<Map<String, String>> events = parseEvents(body);

                for (Map<String, String> event : events) {

                    String roundId = event.get("roundId");
                    String participant = event.get("participant");
                    int score = Integer.parseInt(event.get("score"));

                    String key = roundId + "_" + participant;

                    //  Deduplication
                    if (!seen.contains(key)) {
                        seen.add(key);
                        scores.put(participant,
                                scores.getOrDefault(participant, 0) + score);
                    }
                }

                Thread.sleep(5000); // mandatory delay
            }

            // Sort leaderboard (descending)
            List<Map.Entry<String, Integer>> list = new ArrayList<>(scores.entrySet());
            list.sort((a, b) -> b.getValue() - a.getValue());

            // Build JSON correctly
            StringBuilder finalJson = new StringBuilder();

            finalJson.append("{");
            finalJson.append("\"regNo\":\"").append(REG_NO).append("\",");
            finalJson.append("\"leaderboard\":[");

            int i = 0;
            for (Map.Entry<String, Integer> entry : list) {

                finalJson.append("{");
                finalJson.append("\"participant\":\"").append(entry.getKey()).append("\",");
                finalJson.append("\"totalScore\":").append(entry.getValue());
                finalJson.append("}");

                if (i < list.size() - 1) {
                    finalJson.append(",");
                }
                i++;
            }

            finalJson.append("]}");

            // DEBUG (see what you're sending)
            System.out.println("\nFINAL JSON:");
            System.out.println(finalJson.toString());

            // POST submission
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/quiz/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(finalJson.toString()))
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest,
                    HttpResponse.BodyHandlers.ofString());

            // FINAL RESULT
            System.out.println("\nSERVER RESPONSE:");
            System.out.println(postResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Manual JSON parser (safe for this fixed format)
    static List<Map<String, String>> parseEvents(String json) {

        List<Map<String, String>> events = new ArrayList<>();

        try {
            String eventsPart = json.split("\"events\":\\[")[1].split("]")[0];

            String[] items = eventsPart.split("\\},\\{");

            for (String item : items) {

                item = item.replace("{", "").replace("}", "");

                Map<String, String> map = new HashMap<>();

                String[] fields = item.split(",");

                for (String field : fields) {
                    String[] kv = field.split(":");

                    String key = kv[0].replace("\"", "").trim();
                    String value = kv[1].replace("\"", "").trim();

                    map.put(key, value);
                }

                events.add(map);
            }

        } catch (Exception e) {
            System.out.println("Parsing issue (safe to ignore if minor)");
        }

        return events;
    }
}