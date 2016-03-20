#!/usr/bin/env bash

set -e

pushd chaos-lemur
  ./mvnw -q -Dmaven.test.skip=true deploy
popd
