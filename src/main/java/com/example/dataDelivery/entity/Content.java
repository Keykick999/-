package com.example.dataDelivery.entity;

import com.example.dataDelivery.temporary.IdGenerator;

public class Content {
    private Long id;
    private String title;

    private String substance;


    public Content(){
        this.id = IdGenerator.generatedRandomLong();
    }

    public Content(Long id, String title, String substance) {
        this.id = id;
        this.title = title;
        this.substance = substance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", substance='" + substance + '\'' +
                '}';
    }
}
