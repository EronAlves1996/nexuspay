CREATE TYPE balance_side_enum AS enum ('PASSIVE', 'ACTIVE');
create table account_classification (
  id bigserial primary key,
  balance_side balance_side_enum not null,
  description varchar(255)
);
INSERT INTO account_classification (balance_side, description) VALUES 
(/* 1 */'ACTIVE', 'Demand Deposits'),
(/* 2 */'ACTIVE', 'Pending Transfers'),
(/* 3 */'PASSIVE', 'User Deposits');
