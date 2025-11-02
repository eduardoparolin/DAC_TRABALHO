#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE client;
    CREATE DATABASE account_command;
    CREATE DATABASE account_query;
    CREATE DATABASE manager;
    CREATE DATABASE orchestrator;
EOSQL