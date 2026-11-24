package ua.edu.nupp.kpp.green.model;

// Сутність із цілочисельним ідентифікатором (потрібно для універсального CrudDao).
public interface Identifiable {
    Integer getId();

    void setId(Integer id);
}
