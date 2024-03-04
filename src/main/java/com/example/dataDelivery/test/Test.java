package com.example.dataDelivery.test;


import com.example.dataDelivery.entity.PasswordResetToken;
import com.example.dataDelivery.repository.PasswordResetTokenRepository;
import email.EmailSender;

import java.time.LocalDateTime;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        int i = 0;
        while(i < 5) {
            String token = UUID.randomUUID().toString();
            System.out.println(token);
            i++;
        }
    }
}
