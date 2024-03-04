package com.example.dataDelivery.test;


import com.example.dataDelivery.entity.Content;


public class Test {
    public static void main(String[] args) {
        Content content = new Content(1L,"title","subst",10,5);
        content.setId(2L);
        System.out.println(content);
    }
}
