#!/bin/sh

echo "Running code quality checks before commit..." >&2

# 변경된 Kotlin 파일이 있는지 확인
files=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$\|\.kts$')

if [ -z "$files" ]; then
  echo "No Kotlin files staged. Skipping checks." >&2
  exit 0
fi

echo "Staged Kotlin files:" >&2
echo "$files" >&2
echo "" >&2

echo "Running ktlint..." >&2
./gradlew ktlintCheck
if [ $? -ne 0 ]; then
  echo "❌ ktlint found issues. Run './gradlew ktlintFormat' to fix formatting issues." >&2
  exit 1
fi
echo "✅ ktlint passed" >&2

echo "Running detekt..." >&2
./gradlew detekt
if [ $? -ne 0 ]; then
  echo "❌ detekt found issues. Please fix them before committing." >&2
  exit 1
fi
echo "✅ detekt passed" >&2

echo "🎉 All code quality checks passed!" >&2
exit 0
