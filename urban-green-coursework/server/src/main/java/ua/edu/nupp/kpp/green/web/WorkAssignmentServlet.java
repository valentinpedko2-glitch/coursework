package ua.edu.nupp.kpp.green.web;

import ua.edu.nupp.kpp.green.model.WorkAssignment;
import ua.edu.nupp.kpp.green.service.WorkAssignmentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// REST-ресурс /assignments: GET - список, POST - створення, PUT - редагування, DELETE?id= - видалення.
@WebServlet(urlPatterns = "/assignments")
public class WorkAssignmentServlet extends JsonServlet {
    private final WorkAssignmentService service = new WorkAssignmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, service::findAll);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_CREATED, () -> service.create(body(req, WorkAssignment.class)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, () -> service.update(body(req, WorkAssignment.class)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, () -> {
            service.delete(idParam(req));
            return Map.of("deleted", true);
        });
    }
}
