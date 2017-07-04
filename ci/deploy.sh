#!/usr/bin/env sh

set -e -u

ln -fs $PWD/maven $HOME/.m2

cd chaos-lemur
./mvnw -q -Dmaven.test.skip=true deploy
