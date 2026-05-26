package org.sopt.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(
        name = "uk_users_provider_provider_id",
        columnNames = {"provider", "provider_id"}
))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    private String providerId;

    private User(String nickname, String email, String password, Provider provider, String providerId) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static User local(String nickname, String email, String encodedPassword) {
        return new User(nickname, email, encodedPassword, Provider.LOCAL, null);
    }

    public static User oauth(String nickname, String email, Provider provider, String providerId) {
        return new User(nickname, email, null, provider, providerId);
    }
}
