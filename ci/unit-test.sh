#!/usr/bin/env bash

set -e

pushd chaos-lemur
  ./mvnw -q package
popd
