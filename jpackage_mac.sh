"$JAVA_HOME/bin/jpackage" \
  --name "$APP_NAME" \
  --type app-image \
  --app-version "$APP_VERSION" \
  --dest "$DEST" \
  --input "$INPUT" \
  --main-class "$MAIN_CLASS" \
  --main-jar "$MAIN_JAR" \
  --resource-dir "$RESOURCE_DIR"