BACKEND_IMAGE_NAME=compass_backend
FRONTEND_IMAGE_NAME=compass_frontend
LANDING_IMAGE_NAME=compass_landing

build-backend:
	docker build --no-cache -f docker/Dockerfile.backend -t $(BACKEND_IMAGE_NAME) .

build-frontend:
	docker build --no-cache -f docker/Dockerfile.frontend -t $(FRONTEND_IMAGE_NAME) .

build-landing:
	docker build --no-cache -f docker/Dockerfile.landing -t $(LANDING_IMAGE_NAME) .

build: build-backend build-frontend build-landing

run:
	docker-compose -f docker/docker-compose.yml up

stop:
	docker-compose -f docker/docker-compose.yml down
