package fq.fretquiz.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

import static fq.fretquiz.App.nowMillis;

@Entity
@Table(name = "app_user")
public class User {

    public static final String DEFAULT_NAME = "anon";

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Instant createdAt;

    public static User create() {
        var user = new User();
        user.name = DEFAULT_NAME;
        user.createdAt = nowMillis();
        return user;
    }

    public Long id() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
