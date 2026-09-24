# Dockerfile — multi-stage build for the Library Inventory Management System.
#
# NOTE (documented honestly in DEVELOPER_DOCUMENTATION.md, section
# "Containerized Deployment"): this Dockerfile was written to the standard
# multi-stage Java pattern but could NOT be build-tested in the sandbox
# environment used to prepare this submission (no Docker daemon / no access
# to Docker Hub to pull base images there). Verify it with `docker build .`
# in a normal environment before relying on it. Everything else in this
# submission (the JAR itself, build.sh, run.sh) WAS actually built and run.

# ---- Stage 1: build ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY src ./src
COPY deploy/build.sh ./deploy/build.sh
RUN chmod +x deploy/build.sh && ./deploy/build.sh --skip-tests

# ---- Stage 2: runtime ----
# A JRE-only base image is used for the final image so the shipped
# container doesn't carry the full JDK (compiler, javadoc, etc.) it no
# longer needs at runtime — smaller image, smaller attack surface.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/dist/library-inventory.jar ./library-inventory.jar

# The application is an interactive CLI (it reads from stdin), so the
# container must be run with -i (interactive) to be usable:
#   docker run -i library-inventory
ENTRYPOINT ["java", "-jar", "library-inventory.jar"]
