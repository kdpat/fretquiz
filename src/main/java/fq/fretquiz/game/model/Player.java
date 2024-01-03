package fq.fretquiz.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.user.User;
import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private int score;

    public static Player from(User user) {
        var player = new Player();
        player.user = user;
        player.score = 0;
        return player;
    }

    public Player incrementScore() {
        score++;
        return this;
    }

    @JsonProperty
    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User user() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty
    public int score() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", user=" + user +
                ", score=" + score +
                '}';
    }
}
