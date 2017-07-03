#!/usr/bin/env sh

set -e -u

cd chaos-lemur
./mvnw -q -Dmaven.repo.local=../m2/repository -Dmaven.user.home=../m2 package
