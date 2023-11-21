package com.budgetplanner.BudgetPlanner.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;

    private String password;

    private String webhookUrl;

    @Builder
    public User(String account, String password, String webhookUrl) {
        this.account = account;
        this.password = password;
        this.webhookUrl = webhookUrl;
    }
}
