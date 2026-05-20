CREATE TYPE transaction_operation_type AS ENUM ('CREDIT', 'DEBIT');
create table app_transaction (
  id serial primary key,
  wallet_id uuid not null references wallet(id),
  created_at timestamp(6) with time zone not null,
  operation transaction_operation_type not null,
  description varchar(255) not null,
  amount decimal(10, 2) not null
)
