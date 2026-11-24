package ua.edu.nupp.kpp.green.service;

import ua.edu.nupp.kpp.green.dao.CrudDao;
import ua.edu.nupp.kpp.green.dao.WorkAssignmentDao;
import ua.edu.nupp.kpp.green.dao.WorkRequestDao;
import ua.edu.nupp.kpp.green.model.RequestState;
import ua.edu.nupp.kpp.green.model.WorkAssignment;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.rules.Validator;

import java.util.List;
import java.util.logging.Logger;

public class WorkAssignmentService {
    private static final Logger LOG = Logger.getLogger(WorkAssignmentService.class.getName());

    private final CrudDao<WorkAssignment> assignments;
    private final CrudDao<WorkRequest> requests;

    public WorkAssignmentService() {
        this(new WorkAssignmentDao(), new WorkRequestDao());
    }

    public WorkAssignmentService(CrudDao<WorkAssignment> assignments, CrudDao<WorkRequest> requests) {
        this.assignments = assignments;
        this.requests = requests;
    }

    public List<WorkAssignment> findAll() {
        return assignments.findAll();
    }

    public WorkAssignment create(WorkAssignment assignment) {
        Validator.validateAssignment(assignment);
        requireOpenRequest(assignment.getRequestId());
        assignment.setId(null);
        WorkAssignment saved = assignments.insert(assignment);
        LOG.info("Додано наряд id=" + saved.getId() + " для заявки id=" + saved.getRequestId());
        return saved;
    }

    public WorkAssignment update(WorkAssignment assignment) {
        Validator.validateAssignment(assignment);
        ServiceSupport.requireId(assignment.getId());
        requireOpenRequest(assignment.getRequestId());
        assignments.update(assignment);
        LOG.info("Оновлено наряд id=" + assignment.getId());
        return assignment;
    }

    public void delete(int id) {
        assignments.delete(id);
        LOG.info("Видалено наряд id=" + id);
    }

    private void requireOpenRequest(Integer requestId) {
        WorkRequest request = requests.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException("Заявку з id=" + requestId + " не знайдено"));
        if (request.getState() == RequestState.DONE) {
            throw new IllegalArgumentException("Заявку вже виконано, наряд створити не можна");
        }
    }
}
