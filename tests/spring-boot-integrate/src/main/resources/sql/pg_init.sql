create table "person"
(
    "id"           bigserial primary key,
    "name"         text,
    "active"       bool,
    "when_created" date default now()
);
insert into "person"("name", "active")
values ('a', true),
       ('b', true);