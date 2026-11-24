package ua.edu.nupp.kpp.green.service;

import ua.edu.nupp.kpp.green.dao.CrudDao;
import ua.edu.nupp.kpp.green.dao.InspectionDao;
import ua.edu.nupp.kpp.green.dao.TreeDao;
import ua.edu.nupp.kpp.green.dao.WorkRequestDao;
import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.RequestState;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.rules.PriorityCalculator;
import ua.edu.nupp.kpp.green.rules.Validator;

import java.util.List;
import java.util.logging.Logger;

// Заявка на роботи: фактори F1-F3 та індекс I обчислює сервер, користувач їх не вводить.
public class WorkRequestService {
    private static final Logger LOG = Logger.getLogger(WorkRequestService.class.getName());

    private final CrudDao<WorkRequest> requests;
    private final CrudDao<Tree> trees;
    private final CrudDao<Inspection> inspections;

    public WorkRequestService() {
        this(new WorkRequestDao(), new TreeDao(), new InspectionDao());
    }

    public WorkRequestService(CrudDao<WorkRequest> requests, CrudDao<Tree> trees,
                              CrudDao<Inspection> inspections) {
        this.requests = requests;
        this.trees = trees;
        this.inspections = inspections;
    }

    public List<WorkRequest> findAll() {
        return requests.findAll();
    }

    public WorkRequest create(WorkRequest request) {
        Validator.validateRequest(request);
        request.setId(null);
        request.setState(RequestState.CREATED);
        calculate(request);
        WorkRequest saved = requests.insert(request);
        LOG.info("Створено заявку id=" + saved.getId() + ", I=" + saved.getPriorityIndex());
        return saved;
    }

    // Редагувати можна лише заявку у стані СТВОРЕНО; фактори перераховуються.
    public WorkRequest update(WorkRequest request) {
        Validator.validateRequest(request);
        ServiceSupport.requireId(request.getId());
        WorkRequest current = find(request.getId());
        if (current.getState() != RequestState.CREATED) {
            throw new IllegalArgumentException("Редагувати можна лише заявку у стані СТВОРЕНО");
        }
        request.setState(current.getState());
        calculate(request);
        requests.update(request);
        LOG.info("Оновлено заявку id=" + request.getId() + ", I=" + request.getPriorityIndex());
        return request;
    }

    // Єдине місце, де перевіряється допустимість переходу між станами.
    public WorkRequest transition(int id, RequestState target) {
        WorkRequest current = find(id);
        RequestState from = current.getState();
        if (!from.canMoveTo(target)) {
            String to = target == null ? "?" : target.getLabel();
            throw new IllegalArgumentException(
                    "Перехід " + from.getLabel() + " → " + to + " недопустимий");
        }
        current.setState(target);
        requests.update(current);
        LOG.info("Заявка id=" + id + ": " + from.getLabel() + " → " + target.getLabel());
        return current;
    }

    public void delete(int id) {
        requests.delete(id);
        LOG.info("Видалено заявку id=" + id);
    }

    private WorkRequest find(int id) {
        return requests.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявку з id=" + id + " не знайдено"));
    }

    private void calculate(WorkRequest r) {
        Inspection inspection = inspections.findById(r.getInspectionId()).orElseThrow(
                () -> new IllegalArgumentException("Огляд з id=" + r.getInspectionId() + " не знайдено"));
        Tree tree = trees.findById(r.getTreeId()).orElseThrow(
                () -> new IllegalArgumentException("Дерево з id=" + r.getTreeId() + " не знайдено"));
        if (!tree.getId().equals(inspection.getTreeId())) {
            throw new IllegalArgumentException("Огляд належить іншому дереву");
        }
        double f1 = PriorityCalculator.f1(inspection.getHealthScore());
        double f2 = PriorityCalculator.f2(tree.getPedestriansPerHour());
        double f3 = PriorityCalculator.f3(r.getDeadlineDays());
        r.setF1(f1);
        r.setF2(f2);
        r.setF3(f3);
        r.setPriorityIndex(PriorityCalculator.index(f1, f2, f3));
    }
}
