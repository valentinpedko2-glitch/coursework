package ua.edu.nupp.kpp.green.service;

import ua.edu.nupp.kpp.green.dao.CrudDao;
import ua.edu.nupp.kpp.green.dao.TreeDao;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.rules.Validator;

import java.util.List;
import java.util.logging.Logger;

public class TreeService {
    private static final Logger LOG = Logger.getLogger(TreeService.class.getName());

    private final CrudDao<Tree> trees;

    public TreeService() {
        this(new TreeDao());
    }

    public TreeService(CrudDao<Tree> trees) {
        this.trees = trees;
    }

    public List<Tree> findAll() {
        return trees.findAll();
    }

    public Tree create(Tree tree) {
        Validator.validateTree(tree);
        tree.setId(null);
        Tree saved = trees.insert(tree);
        LOG.info("Додано дерево id=" + saved.getId() + ", код=" + saved.getCode());
        return saved;
    }

    public Tree update(Tree tree) {
        Validator.validateTree(tree);
        ServiceSupport.requireId(tree.getId());
        trees.update(tree);
        LOG.info("Оновлено дерево id=" + tree.getId());
        return tree;
    }

    public void delete(int id) {
        trees.delete(id);
        LOG.info("Видалено дерево id=" + id + " (разом із пов'язаними записами)");
    }
}
