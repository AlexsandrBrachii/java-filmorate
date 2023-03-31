CREATE TABLE IF NOT EXISTS genres (
  genre_id IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  user_id IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  login VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS likes (
  like_id IDENTITY PRIMARY KEY,
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS films (
  film_id IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  rating VARCHAR(255),
  releaseDate DATE NOT NULL,
  duration INTEGER NOT NULL,
  genre_id INTEGER,
  mpa_id INTEGER,
  likes_id INTEGER,
  FOREIGN KEY (genre_id) REFERENCES genres (genre_id),
  FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id),
  FOREIGN KEY (likes_id) REFERENCES likes (like_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INTEGER NOT NULL,
  friend_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (friend_id) REFERENCES users (user_id),
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INTEGER NOT NULL,
  genre_id INTEGER NOT NULL,
  FOREIGN KEY (film_id) REFERENCES films (film_id),
  FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

