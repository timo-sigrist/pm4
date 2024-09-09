# Compass Backend Applikation

Das Backend von Compass ist als eine "Spring Boot mit Java SE" Applikation umgesetzt.

# Requirements
- JDK 21
- Gradle 8.6

# Docker commands

Create a Docker Image with the tag 'compass-backend'
```
docker image build -t compass-backend .
```

To list images and find Image ID
```
docker images
```

```
docker run -p 8080:8080 ${IMAGE_ID}
```

OR
```
docker run -p 8080:8080 compass-backend
```
