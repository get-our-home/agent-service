PROJECT_NAME=agent-service
DOCKER_IMAGE_NAME=agent-service:latest

# Default target
.PHONY: all
all: build docker

# Build the project using Gradle
.PHONY: build
build:
	./gradlew build

# Build the Docker image
.PHONY: docker
docker:
	docker build -t $(DOCKER_IMAGE_NAME) .

# Clean the build
.PHONY: clean
clean:
	./gradlew clean

# Run the application using Docker Compose
.PHONY: up
up:
	docker-compose up --build

# Stop the application
.PHONY: down
down:
	docker-compose down

# Remove Docker image
.PHONY: rmi
rmi:
	docker rmi $(DOCKER_IMAGE_NAME)
