package com.example.dataDelivery.entity;

import java.util.Date;

public class Comment {
    private Long id;  //회원 아이디
    private String author;
    private String text;
    private Date date;

    public Comment(Long id, String author, String text, Date date) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
