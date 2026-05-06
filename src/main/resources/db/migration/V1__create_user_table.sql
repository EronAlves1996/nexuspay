create table app_user (deleted boolean not null, 
  created_at timestamp(6) with time zone not null, 
  updated_at timestamp(6) with time zone, 
  id uuid not null, 
  email varchar(255) not null unique, 
  name varchar(255) not null, 
  primary key (id));
comment on column app_user.deleted is 'Soft-delete indicator';
