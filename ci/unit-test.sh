#!/usr/bin/env sh

set -e -u

M2=$PWD/m2

cd chaos-lemur
./mvnw -q -Dmaven.repo.local=$M2/repository -Dmaven.user.home=$M2 package
