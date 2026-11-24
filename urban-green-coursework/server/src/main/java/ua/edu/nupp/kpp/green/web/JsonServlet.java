package ua.edu.nupp.kpp.green.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Спільна поведінка HTTP-контролерів: JSON на вході й виході та єдина обробка помилок.
// Servlet не містить SQL і предметної логіки: він лише викликає Service.
public abstract class JsonServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(JsonServlet.class.getName());
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @FunctionalInterface
    protected interface Action {
        Object run() throws Exception;
    }

    // Виконує дію; помилки даних -> 400 із зрозумілим повідомленням, решта -> 500 без технічних деталей.
    protected void respond(HttpServletResponse resp, int okStatus, Action action) throws IOException {
        try {
            write(resp, okStatus, action.run());
        } catch (IllegalArgumentException e) {
            LOG.warning("Помилка запиту: " + e.getMessage());
            write(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", String.valueOf(e.getMessage())));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Внутрішня помилка сервера", e);
            write(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Внутрішня помилка сервера"));
        }
    }

    private void write(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), body);
    }

    protected <T> T body(HttpServletRequest req, Class<T> type) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            return MAPPER.readValue(req.getInputStream(), type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Некоректний JSON у тілі запиту");
        }
    }

    protected int idParam(HttpServletRequest req) {
        try {
            return Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр id обов'язковий і має бути цілим числом");
        }
    }
}
