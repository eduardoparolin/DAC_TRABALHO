#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    SELECT 'CREATE DATABASE client' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'client')\gexec
    SELECT 'CREATE DATABASE account_command' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'account_command')\gexec
    SELECT 'CREATE DATABASE account_query' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'account_query')\gexec
    SELECT 'CREATE DATABASE manager' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'manager')\gexec
    SELECT 'CREATE DATABASE orchestrator' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'orchestrator')\gexec
EOSQL
