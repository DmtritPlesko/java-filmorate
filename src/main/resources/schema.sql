DROP TABLE IF EXISTS genres,mpa,film_directors,directors,
                     films,film_genres,likes,status,
                     friends,review_likes,reviews,feeds,users;

create table if not EXISTS genres (
    genre_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    genre_name VARCHAR(255) NOT NULL,
    CONSTRAINT genre_id_pk PRIMARY KEY (genre_id)
);

CREATE TABLE if not EXISTS mpa (
    mpa_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    mpa_name VARCHAR(255),
    CONSTRAINT mpa_id_pk PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    director_name VARCHAR(255) NOT NULL,
    CONSTRAINT director_id_pk PRIMARY KEY (director_id)
);

create table IF NOT EXISTS users (
    user_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    age INTEGER,
    email VARCHAR(255) UNIQUE,
    login VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    birthday TIMESTAMP
);

create table if not EXISTS films (
    film_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    release_date TIMESTAMP,
    duration INTEGER,
    mpa_id INTEGER REFERENCES mpa (mpa_id),
    CONSTRAINT film_id_pk PRIMARY KEY (film_id)
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors(director_id) ON DELETE CASCADE,
    CONSTRAINT film_directors_pk PRIMARY KEY (film_id,director_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER NOT NULL REFERENCES films (film_id),
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id) ,
     CONSTRAINT film_genres_pk PRIMARY KEY (film_id, genre_id),
     CONSTRAINT fk_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
     CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
film_id INTEGER NOT NULL,
user_id INTEGER NOT NULL,
CONSTRAINT "likes_pk" PRIMARY KEY (user_id , film_id),
CONSTRAINT "like_film_fk" FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE,
CONSTRAINT "like_user_fk" FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS status (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    status_type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(255),
    CONSTRAINT id PRIMARY KEY (user_id,friend_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    content VARCHAR(1024) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    useful INTEGER,
    CONSTRAINT review_id_pk PRIMARY KEY (review_id)
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id INTEGER REFERENCES reviews(review_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    is_useful BOOLEAN,
    CONSTRAINT review_likes_pk PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS feeds (
event_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
entity_id integer ,
event_type varchar(200),
operation varchar(200),
timestamp BIGINT
);