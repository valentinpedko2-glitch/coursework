package ua.edu.nupp.kpp.green.web;

import ua.edu.nupp.kpp.green.model.RequestState;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.service.WorkRequestService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// REST-ресурс /requests: GET, POST, PUT, DELETE?id=, а також
// PUT /requests?action=transition&id=..&state=VERIFIED - зміна стану заявки.
@WebServlet(urlPatterns = "/requests")
public class WorkRequestServlet extends JsonServlet {
    private final WorkRequestService service = new WorkRequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, service::findAll);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_CREATED, () -> service.create(body(req, WorkRequest.class)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, () -> {
            if ("transition".equals(req.getParameter("action"))) {
                return service.transition(idParam(req), RequestState.parse(req.getParameter("state")));
            }
            return service.update(body(req, WorkRequest.class));
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        respond(resp, HttpServletResponse.SC_OK, () -> {
            service.delete(idParam(req));
            return Map.of("deleted", true);
        });
    }
}
