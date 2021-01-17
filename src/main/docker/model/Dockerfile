FROM postgres:alpine
ENV POSTGRES_PASSWORD=30002299
WORKDIR /sql
ADD /sql /sql
COPY sql/database.sql /docker-entrypoint-initdb.d/