#!/usr/bin/env bash

echo "All Unit Tests"
./gradlew cleanTest test

echo "Run Contract Tests"
./gradlew contracts:test

echo "Run Flow Tests"
./gradlew workflows:test

echo "Run Integration Tests"
./gradlew workflows:integrationTest

echo "Run Contract Tests"
./gradlew contracts:test
