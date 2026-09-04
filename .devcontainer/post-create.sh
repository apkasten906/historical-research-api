#!/usr/bin/env bash

set -e

sudo chown -R vscode:vscode /home/vscode/.codex /home/vscode/.m2
if [ -d /workspace/target ]; then
  sudo chown -R vscode:vscode /workspace/target
fi

git config --global core.autocrlf input
git config --global init.defaultBranch main
git config --global --replace-all safe.directory /workspace

echo "Development tool versions:"
java --version
mvn --version
git --version
codex --version

echo ""
echo "Codex CLI is installed."
echo "If this is the first time using this Dev Container, run 'codex' and choose Sign in with ChatGPT."
echo "Your Codex login state is stored in the persistent Docker volume mounted at /home/vscode/.codex."
