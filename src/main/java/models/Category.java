package models;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    //constructors
    public Category() {}
    public Category(String name) {this.name = name;}

    //getters&setters
    public int getId() {return id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}