#!/usr/bin/env bash
# ==============================================================================
# NIVARANFLOW - SIH 2026 AICTE Complaint Tracking System
# Team: DISASTER DECODED (Team ID: 198035) | Problem Statement: SIH26264
# ==============================================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=================================================================="
echo " Starting NIVARANFLOW - SIH 2026 AICTE Complaint Tracking System "
echo " Team: DISASTER DECODED (Team ID: 198035)                         "
echo " Problem Statement: SIH26264                                     "
echo "=================================================================="

# 1. Compile Java Backend
echo "Compiling Java Backend..."
mkdir -p bin
javac -d bin $(find backend/src -name "*.java")

# 2. Launch Server
PORT=${PORT:-8080}
echo "Launching Java Server on http://localhost:${PORT}..."
java -cp bin com.disasterdecoded.nivaranflow.Main "$PORT"
