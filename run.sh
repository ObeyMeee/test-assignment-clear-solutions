#!/bin/bash

export DB_URL=jdbc:postgresql://localhost:5432/test-assignment-clear-solutions
export DB_USERNAME=postgres
export DB_PASSWORD=root

mvn clean install
java -jar target/test-assignment-0.0.1-SNAPSHOT.jar