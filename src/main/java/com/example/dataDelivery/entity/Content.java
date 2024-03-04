package com.example.dataDelivery.entity;

import com.example.dataDelivery.temporary.IdGenerator;



public class Content {
    private Long id;
    private String title;

    private String substance;
    private int likesCount = 0;
    private int dislikesCount = 0;


    public Content(Long id, String title, String substance, int likesCount, int dislikesCount) {
        this.id = id;
        this.title = title;
        this.substance = substance;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Content(){
        this.id = IdGenerator.generatedRandomLong();
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", substance='" + substance + '\'' +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                '}';
    }
}
