create table "person"
(
    "id"          bigserial primary key,
    "name"        text,
    "create_date" timestamp default now()
);
insert into "person"("name")
values ('a'),
       ('b');