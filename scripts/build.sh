#!/usr/bin/env bash
# =============================================================================
# build.sh — Local build script for java-devops-project
# Usage: ./scripts/build.sh [clean|test|package|all]
# =============================================================================

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

COMMAND="${1:-all}"

check_java() {
    if ! command -v java &>/dev/null; then
        error "Java not found. Please install JDK 17+"
    fi
    JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    info "Java version detected: $JAVA_VER"
    [[ "$JAVA_VER" -lt 17 ]] && error "Java 17+ is required (found $JAVA_VER)"
    success "Java check passed"
}

make_executable() {
    chmod +x ./gradlew
    info "gradlew is executable"
}

run_clean() {
    info "Cleaning build directory..."
    ./gradlew clean
    success "Clean complete"
}

run_build() {
    info "Compiling source..."
    ./gradlew compileJava
    success "Compilation complete"
}

run_tests() {
    info "Running unit tests..."
    ./gradlew test
    info "Generating JaCoCo coverage report..."
    ./gradlew jacocoTestReport
    success "Tests passed — report in build/reports/jacoco/"
}

run_package() {
    info "Packaging application..."
    ./gradlew fatJar
    JAR_PATH=$(find build/libs -name "*-all.jar" | head -1)
    success "Fat JAR built: $JAR_PATH"
}

run_all() {
    check_java
    make_executable
    run_clean
    run_build
    run_tests
    run_package
    echo ""
    success "================================================"
    success " Full build pipeline completed successfully! ✅  "
    success "================================================"
}

case "$COMMAND" in
    clean)   check_java; make_executable; run_clean   ;;
    test)    check_java; make_executable; run_tests   ;;
    package) check_java; make_executable; run_package ;;
    all)     run_all ;;
    *)       error "Unknown command: $COMMAND. Use: clean | test | package | all" ;;
esac
