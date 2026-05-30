#!/usr/bin/env bash
# =============================================================================
# deploy.sh — Docker build and deploy script for java-devops-project
# Usage: ./scripts/deploy.sh [build|push|run|stop|all]
# =============================================================================

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

IMAGE_NAME="${DOCKER_IMAGE:-java-devops-project}"
IMAGE_TAG="${DOCKER_TAG:-local}"
CONTAINER_NAME="java-devops-app"
DOCKER_REGISTRY="${DOCKER_REGISTRY:-}"

check_docker() {
    command -v docker &>/dev/null || error "Docker is not installed or not running"
    success "Docker found"
}

docker_build() {
    info "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
    # Build the fat JAR first
    chmod +x ./gradlew && ./gradlew fatJar --quiet
    docker build \
        -f docker/Dockerfile \
        -t "${IMAGE_NAME}:${IMAGE_TAG}" \
        -t "${IMAGE_NAME}:latest" \
        .
    success "Docker image built: ${IMAGE_NAME}:${IMAGE_TAG}"
}

docker_push() {
    [[ -z "$DOCKER_REGISTRY" ]] && error "DOCKER_REGISTRY is not set"
    info "Pushing image to registry: $DOCKER_REGISTRY"
    docker tag "${IMAGE_NAME}:${IMAGE_TAG}" "${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
    docker push "${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
    success "Image pushed to $DOCKER_REGISTRY"
}

docker_run() {
    info "Stopping existing container (if any)..."
    docker stop "$CONTAINER_NAME" 2>/dev/null || true
    docker rm   "$CONTAINER_NAME" 2>/dev/null || true

    info "Starting container: $CONTAINER_NAME"
    docker run -d \
        --name "$CONTAINER_NAME" \
        --restart unless-stopped \
        -p 8080:8080 \
        "${IMAGE_NAME}:latest"

    success "Container '$CONTAINER_NAME' is running"
    docker logs "$CONTAINER_NAME"
}

docker_stop() {
    info "Stopping container: $CONTAINER_NAME"
    docker stop "$CONTAINER_NAME" && docker rm "$CONTAINER_NAME"
    success "Container stopped and removed"
}

COMMAND="${1:-all}"
check_docker

case "$COMMAND" in
    build) docker_build ;;
    push)  docker_push  ;;
    run)   docker_run   ;;
    stop)  docker_stop  ;;
    all)   docker_build; docker_run ;;
    *)     error "Unknown command: $COMMAND. Use: build | push | run | stop | all" ;;
esac
