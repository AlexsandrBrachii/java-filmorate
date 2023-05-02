DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS films_directors CASCADE;
DROP TABLE IF EXISTS directors CASCADE;


DROP TABLE IF EXISTS reviews CASCADE;

CREATE TABLE IF NOT EXISTS directors
(
    id   identity PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   identity PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id   identity PRIMARY KEY,
    mpa_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  identity PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    birthday DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id     identity PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    rate        INTEGER,
    releasedate DATE         NOT NULL,
    duration    INTEGER      NOT NULL,
    mpa_id      INTEGER,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER,
    genre_id INTEGER,
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS films_directors
(
    film_id     INTEGER,
    director_id INTEGER,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
  review_id IDENTITY PRIMARY KEY,
  content VARCHAR(255) NOT NULL,
  is_positive BOOLEAN NOT NULL,
  rating_useful INTEGER,
  user_id INTEGER NOT NULL,
  film_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (film_id) REFERENCES films (film_id)
);
CREATE TABLE IF NOT EXISTS feed (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    timestamp long NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    entity_id INT NOT NULL
);
