#!/usr/bin/env bash
mvn -f .. clean install dependency:copy-dependencies
rm -rf libs
mkdir libs
cp ../target/dependency/*  libs
cp ../target/allowance-tracker-1.0.jar libs
tar -czf allowance-tracker.tar Dockerfile run.sh build.sh libs

