package ua.edu.nupp.kpp.green.client.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.green.client.model.Inspection;
import ua.edu.nupp.kpp.green.client.model.RequestState;
import ua.edu.nupp.kpp.green.client.model.Tree;
import ua.edu.nupp.kpp.green.client.model.WorkAssignment;
import ua.edu.nupp.kpp.green.client.model.WorkRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

// Єдина точка зв'язку JavaFX-клієнта із сервером: HTTP + JSON. SQL і JDBC на клієнті відсутні.
// Адреса сервера: змінна середовища GREEN_API_URL (за замовчуванням http://localhost:8080/green-server).
public class ApiClient {
    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ApiClient() {
        String env = System.getenv("GREEN_API_URL");
        this.baseUrl = env == null || env.isBlank() ? "http://localhost:8080/green-server" : env;
    }

    // ---- Дерева ----
    public List<Tree> getTrees() {
        return list("/trees", Tree.class);
    }

    public Tree addTree(Tree t) {
        return send("POST", "/trees", t, Tree.class);
    }

    public Tree updateTree(Tree t) {
        return send("PUT", "/trees", t, Tree.class);
    }

    public void deleteTree(int id) {
        call("DELETE", "/trees?id=" + id, null);
    }

    // ---- Огляди ----
    public List<Inspection> getInspections() {
        return list("/inspections", Inspection.class);
    }

    public Inspection addInspection(Inspection i) {
        return send("POST", "/inspections", i, Inspection.class);
    }

    public Inspection updateInspection(Inspection i) {
        return send("PUT", "/inspections", i, Inspection.class);
    }

    public void deleteInspection(int id) {
        call("DELETE", "/inspections?id=" + id, null);
    }

    // ---- Заявки ----
    public List<WorkRequest> getRequests() {
        return list("/requests", WorkRequest.class);
    }

    public WorkRequest addRequest(WorkRequest r) {
        return send("POST", "/requests", r, WorkRequest.class);
    }

    public WorkRequest updateRequest(WorkRequest r) {
        return send("PUT", "/requests", r, WorkRequest.class);
    }

    public void deleteRequest(int id) {
        call("DELETE", "/requests?id=" + id, null);
    }

    public WorkRequest changeRequestState(int id, RequestState state) {
        String json = call("PUT", "/requests?action=transition&id=" + id + "&state=" + state.name(), null);
        return parse(json, WorkRequest.class);
    }

    // ---- Наряди ----
    public List<WorkAssignment> getAssignments() {
        return list("/assignments", WorkAssignment.class);
    }

    public WorkAssignment addAssignment(WorkAssignment a) {
        return send("POST", "/assignments", a, WorkAssignment.class);
    }

    public WorkAssignment updateAssignment(WorkAssignment a) {
        return send("PUT", "/assignments", a, WorkAssignment.class);
    }

    public void deleteAssignment(int id) {
        call("DELETE", "/assignments?id=" + id, null);
    }

    // ---- Внутрішні методи ----
    private <T> List<T> list(String path, Class<T> type) {
        String json = call("GET", path, null);
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new ApiException("Некоректна відповідь сервера");
        }
    }

    private <T> T send(String method, String path, Object body, Class<T> type) {
        return parse(call(method, path, body), type);
    }

    private <T> T parse(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new ApiException("Некоректна відповідь сервера");
        }
    }

    // Виконує HTTP-запит і повертає тіло відповіді; для кодів 4xx/5xx кидає ApiException.
    private String call(String method, String path, Object body) {
        try {
            HttpRequest.BodyPublisher publisher = body == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .method(method, publisher)
                    .build();
            HttpResponse<String> response = http.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                throw new ApiException(errorMessage(response.body(), response.statusCode()));
            }
            return response.body();
        } catch (IOException e) {
            throw new ApiException("Сервер недоступний: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Запит перервано");
        }
    }

    private String errorMessage(String body, int status) {
        try {
            String message = mapper.readTree(body).path("error").asText("");
            if (!message.isBlank()) {
                return message;
            }
        } catch (IOException ignored) {
            // тіло не є JSON - повертаємо загальне повідомлення нижче
        }
        return "Помилка сервера (HTTP " + status + ")";
    }
}
