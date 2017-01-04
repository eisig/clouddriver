#!/bin/bash
d=$(dirname "$0")
cd "$d"
LOG_DIR=${LOG_DIR:-../logs}

DEF_SYS_PROPERTIES="-Dspring.config.location='/Users/eisig/develop/spinnaker/spinnaker/config/,/Users/eisig/.spinnaker/'"
bash -c "(./gradlew $DEF_SYS_PROPERTIES $@ > '$LOG_DIR/clouddriver.log') 2>&1 | tee -a '$LOG_DIR/clouddriver.log' >& '$LOG_DIR/clouddriver.err' &"
