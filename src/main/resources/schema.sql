DROP TABLE if EXISTS applicants_credit_info;

CREATE TABLE applicants_credit_info (
                                        id INT NOT NULL,
                                        personal_code VARCHAR(50) NOT NULL,
                                        credit_modifier INT,
                                        is_debtor BOOLEAN
);
