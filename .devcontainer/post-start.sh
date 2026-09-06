#!/usr/bin/env bash

set -euo pipefail

workspace_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
git config --global --replace-all safe.directory "$workspace_dir"

# This optional, ignored file uses Bash assignment syntax.
if [[ -f "$workspace_dir/.env.local" ]]; then
  source "$workspace_dir/.env.local"
fi

if [[ -n "${GIT_USER_NAME:-}" && -n "${GIT_USER_EMAIL:-}" ]]; then
  git config --global user.name "$GIT_USER_NAME"
  git config --global user.email "$GIT_USER_EMAIL"
else
  echo "Git identity unchanged: set GIT_USER_NAME and GIT_USER_EMAIL in .env.local."
fi
