ALTER TABLE app_user DROP CONSTRAINT app_user_email_key;
CREATE UNIQUE INDEX app_user_email_key ON app_user (email) WHERE deleted = false;

ALTER TABLE wallet DROP CONSTRAINT wallet_name_user_id_key;
CREATE UNIQUE INDEX wallet_name_user_id_key ON wallet (name, user_id) WHERE deleted = false;
