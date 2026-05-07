create table wallet (
  id uuid not null,
  name varchar(255) not null,
  user_id uuid not null references app_user(id),
  min_limit decimal(10,2) not null,
  deleted boolean not null, 
  created_at timestamp(6) with time zone not null, 
  updated_at timestamp(6) with time zone, 
  primary key (id),
  unique (name, id));
comment on column wallet.deleted is 'Soft-delete indicator';

