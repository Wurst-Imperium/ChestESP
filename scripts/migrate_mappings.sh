if [ -z "$1" ]; then
	echo "Usage: $0 <yarn_mappings>"
	exit 1
fi

YARN_MAPPINGS="$1"

./gradlew migrateMappings --mappings "$YARN_MAPPINGS" --input "src/main/java" --output "src/main/java_" --warning-mode all
rm -rf ./src/main/java
mv ./src/main/java_ ./src/main/java

./gradlew migrateMappings --mappings "$YARN_MAPPINGS" --input "src/gametest/java" --output "src/gametest/java_" --warning-mode all
rm -rf ./src/gametest/java
mv ./src/gametest/java_ ./src/gametest/java
