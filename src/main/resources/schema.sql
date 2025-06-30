drop table IF EXISTS MPA CASCADE;

create table IF NOT EXISTS MPA
(
    MPA_ID INTEGER auto_increment,
    MPA_NAME   CHARACTER VARYING(10),
    constraint MPA_PK
        primary key (MPA_ID)
);

drop table IF EXISTS FILMS CASCADE;

create table FILMS
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING(50)  not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    FILM_MPA          INTEGER                not null,
    constraint FILMS_PK
        primary key (ID),
    constraint FILMS_MPA_MPA_ID_FK
        foreign key (FILM_MPA) references MPA
);

drop table IF EXISTS GENRES CASCADE;

create table IF NOT EXISTS GENRES
(
    GENRES_G_ID INTEGER auto_increment,
    GENRES_NAME      CHARACTER VARYING(15) not null,
    constraint GENRES_PK
        primary key (GENRES_G_ID)
);

drop table IF EXISTS FILM_GENRES CASCADE;

create table IF NOT EXISTS FILM_GENRES
(
    FILM_GENRES_ID INTEGER not null,
    FILM_GENRES_G_ID INTEGER not null,
    constraint FILM_GENRES_PK
        primary key (FILM_GENRES_ID, FILM_GENRES_G_ID),
    constraint FILM_GENRES_FILMS_ID_FK
        foreign key (FILM_GENRES_ID) references FILMS,
    constraint FILM_GENRES_GENRES_GENRES_G_ID_FK
        foreign key (FILM_GENRES_G_ID) references GENRES
);

drop table IF EXISTS USERS CASCADE;

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID       INTEGER AUTO_INCREMENT,
    USER_EMAIL    CHARACTER VARYING(100) NOT NULL UNIQUE,
    USER_LOGIN    CHARACTER VARYING(20)  NOT NULL UNIQUE,
    USER_NAME     CHARACTER VARYING(20)  NOT NULL,
    USER_BIRTHDAY DATE                   NOT NULL,
    CONSTRAINT USERS_PK
    PRIMARY KEY (USER_ID)
);


drop table IF EXISTS FILM_LIKES CASCADE;

create table FILM_LIKES
(
    FILMS_LIKES_ID INTEGER not null,
    U_ID INTEGER not null,
    constraint FILM_LIKES_PK
        primary key (FILMS_LIKES_ID, U_ID),
    constraint FILM_LIKES_FILMS_FILM_ID_FK
        foreign key (FILMS_LIKES_ID) references FILMS,
    constraint FILM_LIKES_USERS_USER_ID_FK
        foreign key (U_ID) references USERS
);


drop table IF EXISTS FRIENDSHIP CASCADE;

create table FRIENDSHIP
(
    FRIENDSHIP_USER_ID INTEGER not null,
    FRIENDSHIP_FRIEND_ID INTEGER not null,
    constraint FRIENDSHIP_PK
        primary key (FRIENDSHIP_USER_ID, FRIENDSHIP_FRIEND_ID),
    constraint FRIENDSHIP_USERS_USER_ID_FK
        foreign key (FRIENDSHIP_USER_ID) references USERS,
    constraint FRIENDSHIP_USERS_USER_ID_FK_2
        foreign key (FRIENDSHIP_FRIEND_ID) references USERS
);