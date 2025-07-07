#!/bin/sh
set -e

echo "🔍 Running full code quality checks before push…" >&2

echo "→ ktlintCheck" >&2
./gradlew -q ktlintCheck || {
  echo "❌ ktlint found issues. Run './gradlew ktlintFormat' to fix formatting issues." >&2
  exit 1
}
echo "✅ ktlint passed" >&2

echo "→ detekt" >&2
./gradlew -q detekt || {
  echo "❌ detekt found issues. Please fix them before pushing." >&2
  exit 1
}
echo "✅ detekt passed" >&2

echo "🎉 All code quality checks passed. Ready to push!" >&2
exit 0
