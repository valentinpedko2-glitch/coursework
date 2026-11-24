package ua.edu.nupp.kpp.green.service;

import ua.edu.nupp.kpp.green.dao.CrudDao;
import ua.edu.nupp.kpp.green.dao.InspectionDao;
import ua.edu.nupp.kpp.green.dao.TreeDao;
import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.rules.Validator;

import java.util.List;
import java.util.logging.Logger;

public class InspectionService {
    private static final Logger LOG = Logger.getLogger(InspectionService.class.getName());

    private final CrudDao<Inspection> inspections;
    private final CrudDao<Tree> trees;

    public InspectionService() {
        this(new InspectionDao(), new TreeDao());
    }

    public InspectionService(CrudDao<Inspection> inspections, CrudDao<Tree> trees) {
        this.inspections = inspections;
        this.trees = trees;
    }

    public List<Inspection> findAll() {
        return inspections.findAll();
    }

    public Inspection create(Inspection inspection) {
        Validator.validateInspection(inspection);
        requireTree(inspection.getTreeId());
        inspection.setId(null);
        Inspection saved = inspections.insert(inspection);
        LOG.info("Додано огляд id=" + saved.getId() + " для дерева id=" + saved.getTreeId());
        return saved;
    }

    public Inspection update(Inspection inspection) {
        Validator.validateInspection(inspection);
        ServiceSupport.requireId(inspection.getId());
        requireTree(inspection.getTreeId());
        inspections.update(inspection);
        LOG.info("Оновлено огляд id=" + inspection.getId());
        return inspection;
    }

    public void delete(int id) {
        inspections.delete(id);
        LOG.info("Видалено огляд id=" + id);
    }

    private void requireTree(Integer treeId) {
        if (trees.findById(treeId).isEmpty()) {
            throw new IllegalArgumentException("Дерево з id=" + treeId + " не знайдено");
        }
    }
}
