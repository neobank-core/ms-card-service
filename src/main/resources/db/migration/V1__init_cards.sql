CREATE TABLE cards (
                       id UUID PRIMARY KEY,
                       account_id UUID NOT NULL,
                       user_id VARCHAR(255) NOT NULL,
                       card_number_masked VARCHAR(32) NOT NULL,
                       card_holder_name VARCHAR(255) NOT NULL,
                       expiry_month INT NOT NULL,
                       expiry_year INT NOT NULL,
                       cvv_hash VARCHAR(255),
                       card_type VARCHAR(20) NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL
);

CREATE TABLE three_ds_sessions (
                                   id UUID PRIMARY KEY,
                                   card_id UUID NOT NULL REFERENCES cards(id),
                                   session_token VARCHAR(255) UNIQUE NOT NULL,
                                   otp_code VARCHAR(20) NOT NULL,
                                   verified BOOLEAN NOT NULL,
                                   expires_at TIMESTAMP NOT NULL
);
