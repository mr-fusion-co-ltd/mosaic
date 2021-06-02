-- LANGUAGES
CREATE TABLE "languages"
(
    "id"         TEXT        NOT NULL PRIMARY KEY,
    "name"       TEXT        NOT NULL UNIQUE,
    "votes"      INTEGER     NOT NULL,
    "created_on" TIMESTAMPTZ NOT NULL
);
