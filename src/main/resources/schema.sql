create table banned_words (
    id INTEGER PRIMARY KEY,
    name VARCHAR(200) not null,
    description VARCHAR(255) null
);

create table documents (
    id INTEGER PRIMARY KEY,
    name VARCHAR(200) not null,
    type VARCHAR(200) not null,
    size INTEGER not null
);