#!/usr/bin/env sh

set -e -u

ln -fs $PWD/maven ~/.m2

cd chaos-lemur
./mvnw -q package
