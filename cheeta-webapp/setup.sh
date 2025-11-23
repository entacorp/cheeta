#!/bin/bash

# Setup script for Cheeta Webapp Development
# This script prepares the environment and creates necessary symlinks for icons/assets

set -e

echo "🐆 Setting up Cheeta Webapp Development Environment..."

# Check Java version
echo "✓ Checking Java..."
java -version 2>&1 | head -1

# Make gradlew executable
chmod +x gradlew

# Create symlink to server-core assets (icons, images, etc.)
echo "✓ Setting up asset symlinks..."

if [ -d "../server-core/src/main/resources/assets" ]; then
    echo "  → Linking server-core assets..."
    mkdir -p src/main/resources/assets
    ln -sf ../../../server-core/src/main/resources/assets/* src/main/resources/assets/ 2>/dev/null || true
else
    echo "  ⚠ server-core assets not found - you can reuse them later"
fi

# Create necessary directories
echo "✓ Creating directory structure..."
mkdir -p src/main/kotlin/io/cheeta/webapp/{api,ui/{view,layout,component,theme}}
mkdir -p src/main/resources/{templates,assets/{icons,images,fonts}}
mkdir -p src/test/kotlin/io/cheeta/webapp

# Download gradle wrapper dependencies
echo "✓ Downloading Gradle wrapper..."
./gradlew --version > /dev/null 2>&1

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "  1. Run: ./gradlew bootRun"
echo "  2. Open: http://localhost:8080"
echo "  3. Check QUICK_START.md for more details"
echo ""
