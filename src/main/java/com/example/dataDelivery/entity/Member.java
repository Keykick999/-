package com.example.dataDelivery.entity;


import com.example.dataDelivery.temporary.IdGenerator;

import java.util.Objects;

public class Member {
    private String id;

    private String name;

    private String email;

    private String phone;
    private String password;

    public Member(){

    }


    public Member(String id, String name, String email, String phone,String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) && Objects.equals(password, member.password);
    }

     public String getId() { return id; }
     public void setId(String id) {this.id = id; }
     public String getName() { return name; }
     public void setName(String name) {this.name = name; }
     public String getEmail() { return email; }
     public void setEmail(String email) {this.email = email; }
     public String getPhone() { return phone; }
     public void setPhone(String phone) {this.phone = phone; }

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
