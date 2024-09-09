[![Build, Push and Deploy - Production](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd-prod.yml/badge.svg)](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd-prod.yml) [![Build, Push and Deploy - Staging](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd-staging.yml/badge.svg)](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd-staging.yml)

[![Quality Gate Status](https://sonarqube-compass.pm4.init-lab.ch/api/project_badges/measure?project=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR&metric=alert_status&token=sqb_e853b3dbfe820eb2a0ef7f2382504bf216db9cb5)](https://sonarqube-compass.pm4.init-lab.ch/dashboard?id=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR)
[![Bugs](https://sonarqube-compass.pm4.init-lab.ch/api/project_badges/measure?project=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR&metric=bugs&token=sqb_e853b3dbfe820eb2a0ef7f2382504bf216db9cb5)](https://sonarqube-compass.pm4.init-lab.ch/dashboard?id=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR)
[![Vulnerabilities](https://sonarqube-compass.pm4.init-lab.ch/api/project_badges/measure?project=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR&metric=vulnerabilities&token=sqb_e853b3dbfe820eb2a0ef7f2382504bf216db9cb5)](https://sonarqube-compass.pm4.init-lab.ch/dashboard?id=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR)
[![Lines of Code](https://sonarqube-compass.pm4.init-lab.ch/api/project_badges/measure?project=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR&metric=ncloc&token=sqb_e853b3dbfe820eb2a0ef7f2382504bf216db9cb5)](https://sonarqube-compass.pm4.init-lab.ch/dashboard?id=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR)
[![Security Rating](https://sonarqube-compass.pm4.init-lab.ch/api/project_badges/measure?project=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR&metric=security_rating&token=sqb_e853b3dbfe820eb2a0ef7f2382504bf216db9cb5)](https://sonarqube-compass.pm4.init-lab.ch/dashboard?id=ZHAW-PM4-Compass_compass_AY9s1cSxfbchvYxrftSR)
[![Coverage](https://codecov.io/gh/ZHAW-PM4-Compass/compass/graph/badge.svg?token=DN7OLQH1TA)](https://codecov.io/gh/ZHAW-PM4-Compass/compass)



# Compass 🧭
Compass is a web application for the Stadtmuur organization, which allows the participants to record their working hours, track their mood, track exceptional incidents, create daily reports and visualize this information.

For more information navigate to our [wiki](https://github.com/ZHAW-PM4-Compass/compass/wiki).

## Table of Contents
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Building the Project](#building-the-project)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Running the Project](#running-the-project)
  - [Backend](#backend-1)
  - [Frontend](#frontend-1)
- [Running Tests](#running-tests)
- [Additional Information](#additional-information)
- [Docker and Kubernetes](#docker-and-kubernetes)
- [Makefile](#makefile)

## Prerequisites
This project uses Node.js and Java. We recommend using [NVM (Node Version Manager)](https://github.com/nvm-sh/nvm) to manage your Node.js versions. You can switch to the correct Node.js version for this project by running `nvm use` in the project directory.

For Java, we recommend using a Java Development Kit (JDK) version 21 or above.

## Project Structure
```shell
.
.
├── README.md                       # Project README with general information
├── backend                         # Backend specific code
│   ├── src                         # Source code for the backend application
│   ├── build.gradle                # Gradle build script for the backend
│   └── settings.gradle             # Gradle settings for the project
├── frontend                        # Frontend specific code
│   ├── .github                     # GitHub workflows and actions configurations
│   ├── .vscode                     # VSCode specific settings
│   ├── migrations                  # Database migrations
│   ├── public                      # Public assets such as images and static files
│   ├── scripts                     # Scripts for building, deploying, etc.
│   ├── src                         # Source code for the frontend
│   │   ├── app                     # Next.js application setup and routing
│   │   ├── components              # Reusable React components
│   │   ├── libs                    # Third-party libraries configuration
│   │   ├── locales                 # Localization and internationalization files
│   │   ├── models                  # Models for structuring database data
│   │   ├── styles                  # CSS and styling files
│   │   ├── templates               # HTML or other templates
│   │   ├── types                   # TypeScript type definitions
│   │   ├── utils                   # Utility functions
│   │   └── validations             # Validation schemas for data input
│   ├── tests                       # Testing suite
│   │   ├── e2e                     # End-to-end testing, possibly including Monitoring as Code
│   │   └── integration             # Integration tests for interconnected components
│   ├── tailwind.config.js          # Tailwind CSS configuration
│   └── tsconfig.json               # TypeScript configuration

```

## Building the Project

### Backend
1. Navigate to the backend directory.
2. Run `./gradlew build` to build the project.

### Frontend
1. Navigate to the frontend directory.
2. Install the necessary dependencies by running `npm install`.
3. Build the project by running `npm run build`.

## Running the Project

### Backend
1. Navigate to the backend directory.
2. Start the server by running `./gradlew bootRun`.

### Frontend
1. Navigate to the frontend directory.
2. Start the server by running `npm run start`.

## Running Tests
This project uses Jest for unit testing and Playwright for integration and E2E testing. To run the tests, use the following commands:
- Unit tests: `npm run test`
- Integration & E2E tests: 
  ```shell
  npx playwright install
  npm run test:e2e
  ```
Refer to the code block from `frontend/README.md` lines 294-304 for more details.

## Additional Information
- [Tailwind CSS](https://tailwindcss.com/docs) for styling.
- [Sentry](https://sentry.io/for/nextjs/?utm_source=github&utm_medium=paid-community&utm_campaign=general-fy25q1-nextjs&utm_content=github-banner-nextjsboilerplate-logo) for error monitoring.
- [Auth0](https://auth0.com/) for authentication.


## Docker and Kubernetes
This project can be containerized using Docker and orchestrated with Kubernetes. However, specific instructions for Docker and Kubernetes will depend on your project setup and requirements. Please refer to Docker's [official documentation](https://docs.docker.com/) and Kubernetes' [official documentation](https://kubernetes.io/docs/home/) for more information.


## Makefile Usage

This project includes a Makefile with commands for building and running different components of the project using Docker. Here are some of the commands you might find useful:

### Building Docker Images

- `make build-backend`: Builds the Docker image for the backend part of the project.

- `make build-frontend`: Builds the Docker image for the frontend part of the project.

- `make build`: Builds the Docker images for both the frontend and backend parts of the project.

### Running the Project

- `make run`: Runs the Docker containers for the project using Docker Compose. This command starts up all the services defined in the `docker-compose.yml` file, including the frontend, backend, and any databases or other dependencies.

- `make stop`: Stops all running Docker containers associated with the project's Docker Compose configuration. This command brings down all the services that were started with `make run`.
