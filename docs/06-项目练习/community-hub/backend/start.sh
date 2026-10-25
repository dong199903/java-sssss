#!/bin/sh
set -e
cd "$(dirname "$0")"
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
exec mvn spring-boot:run
