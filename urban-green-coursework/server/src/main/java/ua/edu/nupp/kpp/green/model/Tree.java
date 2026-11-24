package ua.edu.nupp.kpp.green.model;

// Дерево зеленої зони міста.
public class Tree implements Identifiable {
    private Integer id;
    private String code;
    private String species;
    private String location;
    private Integer plantingYear;
    private Integer pedestriansPerHour;

    public Tree() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPlantingYear() {
        return plantingYear;
    }

    public void setPlantingYear(Integer plantingYear) {
        this.plantingYear = plantingYear;
    }

    public Integer getPedestriansPerHour() {
        return pedestriansPerHour;
    }

    public void setPedestriansPerHour(Integer pedestriansPerHour) {
        this.pedestriansPerHour = pedestriansPerHour;
    }
}
