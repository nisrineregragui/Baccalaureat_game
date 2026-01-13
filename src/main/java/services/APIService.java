package services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIService {
    private static final String API_KEY = "";
    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";


    public boolean verify_word(String word, String categoryName, char letter) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            //AI prompt
            String prompt = String.format(
                    "Tu es un arbitre EXPERT et STRICT du jeu Petit Bac (Baccalauréat).\n" +
                            "Analyse la proposition suivante :\n" +
                            "- Mot : \"%s\"\n" +
                            "- Catégorie : \"%s\"\n" +
                            "- Lettre imposée : \"%s\"\n\n" +
                            "Règles de validation (OBLIGATOIRES) :\n" +
                            "1. Le mot doit impérativement commencer par la lettre \"%s\".\n" +
                            "2. Le mot doit EXISTER réellement (aucun mot inventé, fictif ou approximatif).\n" +
                            "3. Le mot doit appartenir LOGIQUEMENT et CORRECTEMENT à la catégorie donnée.\n" +
                            "4. Pour les catégories :\n" +
                            "   - \"Ville\" : uniquement des villes réelles existantes.\n" +
                            "   - \"Pays\" : uniquement des pays reconnus.\n" +
                            "   - \"Prénom\" : uniquement des prénoms réellement utilisés.\n" +
                            "   - \"Célébrité\" : uniquement des personnes réelles et connues.\n" +
                            "5. Les noms propres ne sont acceptés QUE s’ils sont pertinents pour la catégorie.\n" +
                            "6. En cas de doute, d’erreur ou d’incohérence → réponds \"NON\".\n" +
                            "7. Réponds UNIQUEMENT par \"OUI\" ou \"NON\", sans aucune explication.",
                    word, categoryName, letter, letter
            );


            //Json
            JSONObject payload = new JSONObject();
            payload.put("model", "llama-3.1-8b-instant");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            payload.put("messages", messages);
            payload.put("temperature", 0.1);

            // request post
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //result
            if (response.statusCode() == 200) {
                System.out.println(response.statusCode());
                JSONObject jsonResponse = new JSONObject(response.body());
                String aiResponse = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return aiResponse.trim().toUpperCase().contains("OUI");
            }
            //debug
            if (response.statusCode() != 200) {
                System.out.println("Détails de l'erreur 400 : " + response.body());
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}