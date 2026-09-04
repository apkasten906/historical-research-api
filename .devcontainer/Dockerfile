FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

RUN rm -f /etc/apt/sources.list.d/yarn.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        git \
        jq \
        less \
        maven \
        nano \
        netcat-openbsd \
        nodejs \
        npm \
        postgresql-client \
        unzip \
        vim \
        wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g @openai/codex

RUN mkdir -p /home/vscode/.codex /home/vscode/.m2 \
    && chown -R vscode:vscode /home/vscode/.codex /home/vscode/.m2
