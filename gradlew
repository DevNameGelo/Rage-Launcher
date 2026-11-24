#!/usr/bin/env sh
# Lightweight Gradle bootstrap for this project. GitHub Actions provisions Gradle directly.
set -eu
VERSION="9.6.0"
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/rage-keys-bootstrap"
DIST="$BASE/gradle-$VERSION"
if [ ! -x "$DIST/bin/gradle" ]; then
  mkdir -p "$BASE"
  ZIP="$BASE/gradle-$VERSION-bin.zip"
  if ! command -v curl >/dev/null 2>&1 || ! command -v unzip >/dev/null 2>&1; then
    echo "Gradle is not installed. Install Gradle $VERSION, or install curl and unzip to use this bootstrap." >&2
    exit 1
  fi
  curl -fL "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ZIP"
  unzip -q -o "$ZIP" -d "$BASE"
  rm -f "$ZIP"
fi
exec "$DIST/bin/gradle" "$@"
