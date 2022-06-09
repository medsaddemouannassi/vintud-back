package com.thp.project.vintud.entity.impl;

import com.thp.project.vintud.entity.Category;

import javax.persistence.*;
import java.io.Serializable;

@Entity @Table(name = "category")
public class CategoryImpl implements Category, Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
    private String name;
    private String description;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CategoryImpl{" +
                "category_id=" + category_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
