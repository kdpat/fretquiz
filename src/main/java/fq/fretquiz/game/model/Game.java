package fq.fretquiz.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fq.fretquiz.App.nowMillis;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User host;

    @Embedded
    private Settings settings;

    private Status status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Player> players;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Round> rounds;

    private Instant createdAt;

    public static Game create(User host) {
        var game = new Game();
        game.host = host;
        game.settings = Settings.createDefault();
        game.status = Status.INIT;
        game.createdAt = nowMillis();
        game.rounds = new ArrayList<>();

        var players = new ArrayList<Player>();
        var hostPlayer = Player.from(host);

        players.add(hostPlayer);
        game.players = players;

        return game;
    }

    public boolean userIsPlaying(Long userId) {
        return players.stream()
                .anyMatch(player -> player.user().id().equals(userId));
    }

    public void addPlayer(Player player) {
        if (status != Status.INIT) {
            throw new IllegalStateException("status must be INIT");
        }

        var players = new ArrayList<>(this.players);
        players.add(player);
        this.players = players;
    }

    public void addRound(Round round) {
        var rounds = new ArrayList<>(this.rounds);
        rounds.add(round);
        this.rounds = rounds;
    }

    public boolean roundsFull() {
        return rounds.size() >= settings.roundCount();
    }

    @JsonProperty
    public Optional<Round> currentRound() {
        if (rounds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rounds.getLast());
    }

    public Optional<Player> findPlayer(Long playerId) {
        return players.stream()
                .filter(p -> p.id().equals(playerId))
                .findFirst();
    }

    public Optional<Player> findPlayerByUserId(Long userId) {
        return players.stream()
                .filter(p -> p.user().id().equals(userId))
                .findFirst();
    }

    public int playerCount() {
        return players.size();
    }

    @JsonProperty
    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    public User host() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Settings settings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @JsonProperty
    public Status status() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty
    public List<Player> players() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Round> rounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", host=" + host +
                ", settings=" + settings +
                ", status=" + status +
                ", players=" + players +
                ", rounds=" + rounds +
                ", createdAt=" + createdAt +
                '}';
    }
}
