#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ "${1:-}" == "learn" ]]; then
  shift
fi
./mvnw -q compile exec:java -Dexec.mainClass=org.example.learn.Learn -Dexec.args="${*:-}"
