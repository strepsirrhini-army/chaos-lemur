#!/usr/bin/env sh

set -e

cd chaos-lemur
./mvnw -q -Dmaven.test.skip=true deploy
