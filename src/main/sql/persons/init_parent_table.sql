
/*
 * initializes the parent table with all known parents
 */

create table if not exists parent (
  id varchar(255) not null,
  landline_phone varchar(255) default null,
  work_phone varchar(255) default null,
  primary key (id),
  foreign key (id) references person(id)
) character set utf8;

alter table person
add column parent bit(1) default false;

insert into parent (id, landline_phone, work_phone)
select distinct parent, null, null
from parents
where parent not in (select id from parent);

update person
set parent=true
where id in (select parent from parents);
