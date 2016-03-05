#!/usr/bin/env bash
set -x
#USAGE: bash run.sh {config file path} {additional mount commands}
docker run -d -v "$1:/usr/allowance-tracker/config.properties" $2 -p 4567:4567 --name allowance-tracker allowance-tracker
