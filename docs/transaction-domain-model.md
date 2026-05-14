# Transaction Domain Model

This documents states the concept, designs and implementation steps of transaction domain model.

## Concepts

Therefore, we need to have a clear mental model of the interactions between the players to properly implement this domain.
First of all, a transaction is a interaction between two players of unidirectional resource transfer. That way, is a zero-sum operation.
As stated, we need two players to make a transaction, and we have a bunchfull of ways to register this transaction. The more accepted way in the financial world is using a double entry bookeeping system.

Even the transaction is always a unidirectional interaction, the transaction always will incur in two effects:
1. A withdrawal of the resource on the source side;
2. A deposit of the resource on the target side.

That way, the balance of the resources is always maintained, consisting of the rough mathematical formulae bellow:

`(X - Y) = (Z + Y)`

Where:
- X is the initial amount of the resource on the source side;
- Z is the initial amount of the resource on the target side;
- Y is the amount of resource being transfered in the transaction.

The double entry bookeeping will model this mathematical relation as-is, introducing two registers for a specific transaction, stating that a Debit is the reduction of the resource,
and Credit is the addition of the resource.

That way, the source side always suffer a Debit and target side always suffer a Credit.

The main axiom behind this is that resources always are finit on the world, therefore, if some player have a greater amount of the resource, this will imply that others players will
have lower amounts of the resource, resulting in a zero-sum relation.

By the way, in a payment system we are always talking about money as the main resource, and money is always governed by some entitled entity, can it be the Central Bank and/or the National Treasure.

This entity can decide, by some echonomical criteria, to emit more or to retain and destroy money, and the main way this money can be made available for the society is through the banking system. 

Banks manages money by having some solvency factor behind all of deposits in it, and every transaction they made (withdrawal, deposit, transfer between banks) can alter this solvency factor. Eventually, it should be ammended, depending on the regulatory system, by realizing the transfer of the actual resources between banks or requesting/paying more resources from the entitled entity.

## System modelling 

By the way the concepts are well understood, we need to model this zero sum relationship on our system, first of all, by creating a basic double entry bookeeping. That way, the `transaction` table will be the repository for all the double entry transactions we need.

By means of abstraction, the transactions always will be executed from the source side, because all the mechanisms of concurrency can be greater simplified. Credit operations will always add to the balance, and normally accounts don't have any upper limit, but minimum limit. 

This minimum limit can be bellow 0,00, by means of credit concession from the main system.

### Internal transactions 

Internal transactions are the easier to model. We can define this as a payment (transfer) between users, and this simple operation can be exemplified with the relation bellow:

| Relation | Operation | Amount |
|----------|-----------|--------|
| Source   | Debit     | 200,00 |
| Target   | Credit    | 200,00 |

That way, the transfer between users is the withdrawal from the source user, with the deposit for the target user.

### External transactions 

External transactions can be made the same way as internal transactions, but, since the resource is coming from (or going to) external banking systems or players, they need a pseudo-account for control and to effectively make the double entry bookeeping work.

Since this pseudo-account is not a real account and need to be controlled, they can have an undefined minimum limit. That way we can control the compensation needed when this need to take place.

Example: the user have an account from Nexus Pay and need to make a deposit of paper money for us. Eventually, we need to first, provide this money for the user, but the National Treasure owes us for this custody.

| Relation | Operation | Account           | Amount |
|----------|-----------|-------------------|--------|
| Source   | Debit     | National Treasure | 200,00 |
| Target   | Credit    | User              | 200,00 |


The final balance after this operation will be as follows:

- **National Treasure**: - 200,00
- **User**: 200,00

From times to times, we need the compensation from National Treasure for this money. That way, the custody will be transfered from the National Treasure for Nexus Pay, producing the following operation:

| Relation | Operation | Account           | Amount |
|----------|-----------|-------------------|--------|
| Source   | Debit     | Nexus Pay         | 200,00 |
| Target   | Credit    | National Treasure | 200,00 |

This operation will zero the national treasure account and transfer the custody of obligation from National Treasure to Nexus Pay. That way, Nexus Pay owes 200,00 to the user. The balance of each player will be as follows:

- **National Treasure**: 0,00
- **Nexus Pay**: - 200,00
- **User**: 200,00

Note that, the sum of every operation is always zero.

## Design

### Data

For the tables, we already have a wallet table, and users can have many wallets. Each wallet, actually, cannot be bellow some amount, and we need an easy way to track the balance.
That way, the wallet table will have two more columns:

- balance: Decimal(10,2) => This will be a materialized balance that will be updated from times to times;
- last_processed_transaction_id: BigInt => This will set which operations in the transaction set need to be processed to updated balance or to give the updated balance position;

For transaction, the data model need to properly reflect the double entry bookkeeping, with the amounts, the source and target wallet and the description of the operation. That way, this new table will have the following fields:

- id: BigInt (serial) => The transaction id (PK)
- wallet_id: UUID => The wallet were the operation takes place 
- operation_timestamp: timestamp => The timestamp for the operation 
- operation: enum(debit, credit) => The type of operation that will take place 
- description: varchar(255) => How this operation is described (eventually this will appear on the extract)
- amount: Decimal(10,2) => The amount this operation will increase or decreased on the wallet 

Since each registry of this table is immutable, they will not be deleted ever. They don't need any flag for soft delete.

### Application logic 

#### Balance 

The balance will be denoted by the following formulae:

B = W - D + C 

Where:

- B = Current balance position 
- W = Actual processed balance on the wallet 
- D = The sum of debits since the last_processed_operation on the transaction table 
- C = The sum of credits since the last_processed_operation on the transaction table 

#### Transaction 

For a transaction to take place they need:

1. Inspect the balance on the wallet table. This inspection will lock the row using `SELECT... FOR UPDATE`. This row locking will prevent concurrent source transactions and assert correctness;
2. Fetch the opreations since last_processed_operation. This table doesn't need any lock and transactions can flow to target wallets freely;
3. Produce the current balance position, as explained in the immediatelly former section;
4. Check if the transaction will cause the balance to go bellow the min limit on the table. If yes, the transaction is immediatelly aborted;
5. Produce the actual double entry bookkeeping registries, registering on the transaction table.

#### Compensations 

Compensations will be run every night. The wallets that need compensation will always be created under some specific dummy user. That way, the following algorithm will take place:

1. Fetch all the wallets from this user;
2. Iterate all these wallets;
3. For each wallet, check if the balance is positive or negative;
4. Produce the compensatory transaction against the custody wallet on the root user.

The custody wallet is a root owned wallet where the compensations with external players can be counter balanced and exists to ensure the zero-sum nature resources.

#### Balance updates 

The balance on wallet need to be updated from time to time to ensure that current balance position doesn't need to be calculated expensively. Every night the balance gonna be updated, and to ensure that concurrency aspect will be preserved, the algorithm will be as follows.

1. Read all wallets available on the application. This operation will be outside any transaction. Every wallet created from here will be updated on the next update cycle;
2. For every wallet update, we gonna start a new transaction into a repeatable read isolation level
3. Read the wallet position with `SELECT... FOR UPDATE`
4. Aggregate the debits and credits, calculating the current balance position
5. Save the balance position on the wallet, with the last_processed_operation, which can be the greater value between the last debit or credit.
