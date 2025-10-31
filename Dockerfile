# Dockerfile
FROM eclipse-temurin:17-jdk

SHELL ["/bin/bash", "-lc"]

RUN apt-get update && apt-get install -y --no-install-recommends git \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

RUN git clone https://github.com/tudo-aqua/stars-combinatorial-testing.git

WORKDIR /workspace/stars-combinatorial-testing
RUN chmod +x gradlew && ./gradlew --no-daemon --version

# Nuke any inherited entrypoint from parent layers
ENTRYPOINT []

# Default to a shell if you run it without a command
CMD ["bash"]
