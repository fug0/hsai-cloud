FROM mysql:latest

COPY db_create.sql /docker-entrypoint-initdb.d/

VOLUME /var/lib/mysql/data