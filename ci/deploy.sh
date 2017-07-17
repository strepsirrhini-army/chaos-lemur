#!/usr/bin/env sh

set -e -u

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

cd chaos-lemur
./mvnw -q -Dmaven.test.skip=true deploy
