#!/bin/bash
set -eu

cd "$(dirname "$0")"

./mvnw -q package
exec java -jar target/PPSee-jar-with-dependencies.jar "$@"
