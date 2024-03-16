package com.example.dataDelivery.entity;

import com.example.dataDelivery.temporary.IdGenerator;

import java.sql.Date;

public class Comment {
    private Long id; // pk
    private Long contentId;
    private String writer;
    private String text;
    private Date dates;
    private int likesCount = 0;
    private int dislikesCount = 0;

    public Comment() {
        this.id = IdGenerator.generatedRandomLong();
        this.dates = new Date(System.currentTimeMillis());
    }

    public Comment(Long id, Long contentId, String writer, String text, Date dates, int likesCount, int dislikesCount) {
        this.id = id;
        this.contentId = contentId;
        this.writer = writer;
        this.text = text;
        this.dates = dates;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDates() {
        return dates;
    }

    public void setDates(Date dates) {
        this.dates = dates;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public synchronized void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public synchronized void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", contentId=" + contentId +
                ", writer='" + writer + '\'' +
                ", text='" + text + '\'' +
                ", dates=" + dates +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                '}';
    }
}
