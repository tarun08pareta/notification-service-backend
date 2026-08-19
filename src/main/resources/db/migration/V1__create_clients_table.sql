CREATE TABLE clients (
                         id UUID PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         slug VARCHAR(100) NOT NULL UNIQUE,
                         email VARCHAR(255) NOT NULL,
                         status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);