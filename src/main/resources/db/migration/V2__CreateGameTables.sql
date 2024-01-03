CREATE SEQUENCE IF NOT EXISTS game_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS player_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS round_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE game
(
    id          BIGINT  NOT NULL,
    host_id     BIGINT,
    status      SMALLINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    round_count INTEGER NOT NULL,
    start_fret  INTEGER NOT NULL,
    end_fret    INTEGER NOT NULL,
    CONSTRAINT pk_game PRIMARY KEY (id)
);

CREATE TABLE game_accidentals_to_use
(
    game_id            BIGINT NOT NULL,
    accidentals_to_use SMALLINT
);

CREATE TABLE game_open_string_notes
(
    game_id    BIGINT NOT NULL,
    white_key  SMALLINT,
    accidental SMALLINT,
    octave     SMALLINT
);

CREATE TABLE game_players
(
    game_id    BIGINT NOT NULL,
    players_id BIGINT NOT NULL
);

CREATE TABLE game_rounds
(
    game_id   BIGINT NOT NULL,
    rounds_id BIGINT NOT NULL
);

CREATE TABLE game_strings_to_use
(
    game_id        BIGINT NOT NULL,
    strings_to_use INTEGER
);

CREATE TABLE player
(
    id      BIGINT  NOT NULL,
    user_id BIGINT,
    score   INTEGER NOT NULL,
    CONSTRAINT pk_player PRIMARY KEY (id)
);

CREATE TABLE round
(
    id         BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    white_key  SMALLINT,
    accidental SMALLINT,
    octave     SMALLINT,
    CONSTRAINT pk_round PRIMARY KEY (id)
);

CREATE TABLE round_correct_fret_coords
(
    round_id BIGINT NOT NULL,
    string   INTEGER,
    fret     INTEGER
);

CREATE TABLE round_guesses
(
    round_id   BIGINT NOT NULL,
    player_id  BIGINT,
    string     INTEGER,
    fret       INTEGER,
    is_correct BOOLEAN,
    created_at TIMESTAMP WITHOUT TIME ZONE
);

ALTER TABLE game_players
    ADD CONSTRAINT uc_game_players_players UNIQUE (players_id);

ALTER TABLE game_rounds
    ADD CONSTRAINT uc_game_rounds_rounds UNIQUE (rounds_id);

ALTER TABLE game
    ADD CONSTRAINT FK_GAME_ON_HOST FOREIGN KEY (host_id) REFERENCES app_user (id);

ALTER TABLE player
    ADD CONSTRAINT FK_PLAYER_ON_USER FOREIGN KEY (user_id) REFERENCES app_user (id);

ALTER TABLE game_accidentals_to_use
    ADD CONSTRAINT fk_game_accidentalstouse_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_open_string_notes
    ADD CONSTRAINT fk_game_openstringnotes_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_strings_to_use
    ADD CONSTRAINT fk_game_stringstouse_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_players
    ADD CONSTRAINT fk_gampla_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_players
    ADD CONSTRAINT fk_gampla_on_player FOREIGN KEY (players_id) REFERENCES player (id);

ALTER TABLE game_rounds
    ADD CONSTRAINT fk_gamrou_on_game FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_rounds
    ADD CONSTRAINT fk_gamrou_on_round FOREIGN KEY (rounds_id) REFERENCES round (id);

ALTER TABLE round_correct_fret_coords
    ADD CONSTRAINT fk_round_correctfretcoords_on_round FOREIGN KEY (round_id) REFERENCES round (id);

ALTER TABLE round_guesses
    ADD CONSTRAINT fk_round_guesses_on_round FOREIGN KEY (round_id) REFERENCES round (id);