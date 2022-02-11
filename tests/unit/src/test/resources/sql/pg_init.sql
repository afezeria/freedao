create table "person"
(
    "id"          bigserial primary key,
    "name"        text,
    "create_date" date default now()
);
insert into "person"("name")
values ('a'),
       ('b');