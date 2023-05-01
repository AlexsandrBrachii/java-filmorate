DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;

CREATE TABLE IF NOT EXISTS genres (
  genre_id IDENTITY PRIMARY KEY,
  genre_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id IDENTITY PRIMARY KEY,
  mpa_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  user_id IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  login VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  film_id IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  rate INTEGER,
  releaseDate DATE NOT NULL,
  duration INTEGER NOT NULL,
  mpa_id INTEGER,
  FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INTEGER,
  genre_id INTEGER,
  FOREIGN KEY (film_id) REFERENCES films (film_id),
  FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INTEGER NOT NULL,
  friend_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (friend_id) REFERENCES users (user_id),
  PRIMARY KEY (user_id, friend_id)
);
create table if not exists feed
(
    event_id   int auto_increment,
    user_id    int,
    timestamp  long        not null,
    event_type varchar(10) not null,
    operation  varchar(10) not null,
    entity_id  int         not null,
