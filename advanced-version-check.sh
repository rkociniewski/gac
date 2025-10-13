#!/bin/bash
# Advanced version check for Android projects
# Validates semantic versioning and ensures proper version bumps

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
BUILD_FILE=""
if [ -f "app/build.gradle.kts" ]; then
    BUILD_FILE="app/build.gradle.kts"
elif [ -f "app/build.gradle" ]; then
    BUILD_FILE="app/build.gradle"
else
    echo -e "${RED}❌ No build.gradle file found${NC}"
    exit 1
fi

# Function to extract version info
get_version_info() {
    local file=$1
    local version_code=$(grep -E 'versionCode\s*=\s*[0-9]+' "$file" | grep -oE '[0-9]+' | head -1)
    local version_name=$(grep -E 'versionName\s*=\s*["\047][^"\047]+["\047]' "$file" | grep -oE '["\047][^"\047]+["\047]' | tr -d '"' | tr -d "'" | head -1)
    echo "$version_code|$version_name"
}

# Function to validate semantic versioning
validate_semver() {
    local version=$1
    if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.]+)?(\+[a-zA-Z0-9.]+)?$ ]]; then
        return 1
    fi
    return 0
}

# Function to parse semantic version
parse_semver() {
    local version=$1
    # Remove pre-release and build metadata
    version=$(echo "$version" | sed 's/-.*$//' | sed 's/+.*$//')

    IFS='.' read -r major minor patch <<< "$version"
    echo "$major|$minor|$patch"
}

# Function to compare versions
compare_versions() {
    local v1=$1
    local v2=$2

    IFS='|' read -r major1 minor1 patch1 <<< "$(parse_semver "$v1")"
    IFS='|' read -r major2 minor2 patch2 <<< "$(parse_semver "$v2")"

    # Compare major
    if [ "$major1" -gt "$major2" ]; then
        echo "major"
        return 0
    elif [ "$major1" -lt "$major2" ]; then
        echo "downgrade"
        return 1
    fi

    # Compare minor
    if [ "$minor1" -gt "$minor2" ]; then
        echo "minor"
        return 0
    elif [ "$minor1" -lt "$minor2" ]; then
        echo "downgrade"
        return 1
    fi

    # Compare patch
    if [ "$patch1" -gt "$patch2" ]; then
        echo "patch"
        return 0
    elif [ "$patch1" -lt "$patch2" ]; then
        echo "downgrade"
        return 1
    fi

    echo "same"
    return 0
}

# Function to suggest version bump based on branch
suggest_version_bump() {
    local current_version=$1
    local branch_name=$2

    IFS='|' read -r major minor patch <<< "$(parse_semver "$current_version")"

    if [[ $branch_name =~ ^hotfix/ ]]; then
        patch=$((patch + 1))
        echo "${major}.${minor}.${patch}"
    elif [[ $branch_name =~ ^release/ ]]; then
        minor=$((minor + 1))
        echo "${major}.${minor}.0"
    elif [[ $branch_name =~ ^feature/ ]]; then
        minor=$((minor + 1))
        echo "${major}.${minor}.0"
    else
        # Default: bump patch
        patch=$((patch + 1))
        echo "${major}.${minor}.${patch}"
    fi
}

# Main execution
echo -e "${BLUE}🔍 Checking version information...${NC}"
echo ""

# Get current branch
BRANCH_NAME=$(git symbolic-ref --short HEAD 2>/dev/null || echo "detached")

# Get current version
IFS='|' read -r CURRENT_CODE CURRENT_NAME <<< "$(get_version_info "$BUILD_FILE")"

echo "📦 Current Version:"
echo "   versionCode: $CURRENT_CODE"
echo "   versionName: $CURRENT_NAME"
echo "   Branch: $BRANCH_NAME"
echo ""

# Validate semantic versioning
if ! validate_semver "$CURRENT_NAME"; then
    echo -e "${YELLOW}⚠️  Warning: versionName '$CURRENT_NAME' is not valid semantic versioning${NC}"
    echo "   Expected format: MAJOR.MINOR.PATCH (e.g., 1.2.3)"
    echo "   Optional: -prerelease+buildmetadata (e.g., 1.2.3-beta.1+20241010)"
    echo ""
fi

# Check if this is a push to important branch
if [[ $BRANCH_NAME =~ ^(main|master|release/) ]]; then
    echo -e "${YELLOW}⚠️  Pushing to protected branch: $BRANCH_NAME${NC}"

    # Try to get remote version
    if git rev-parse "origin/$BRANCH_NAME" >/dev/null 2>&1; then
        REMOTE_BUILD_FILE_CONTENT=$(git show "origin/$BRANCH_NAME:$BUILD_FILE" 2>/dev/null || echo "")

        if [ -n "$REMOTE_BUILD_FILE_CONTENT" ]; then
            # Create temp file for remote version
            TEMP_FILE=$(mktemp)
            echo "$REMOTE_BUILD_FILE_CONTENT" > "$TEMP_FILE"

            IFS='|' read -r REMOTE_CODE REMOTE_NAME <<< "$(get_version_info "$TEMP_FILE")"
            rm "$TEMP_FILE"

            echo "📦 Remote Version:"
            echo "   versionCode: $REMOTE_CODE"
            echo "   versionName: $REMOTE_NAME"
            echo ""

            # Check versionCode
            if [ "$CURRENT_CODE" -le "$REMOTE_CODE" ]; then
                echo -e "${RED}❌ ERROR: versionCode was not incremented!${NC}"
                echo "   Remote: $REMOTE_CODE"
                echo "   Local:  $CURRENT_CODE"
                echo ""
                echo "   New versionCode must be: $((REMOTE_CODE + 1)) or higher"
                echo ""
                exit 1
            else
                CODE_DIFF=$((CURRENT_CODE - REMOTE_CODE))
                echo -e "${GREEN}✅ versionCode incremented by $CODE_DIFF${NC}"
            fi

            # Check versionName
            if [ "$CURRENT_NAME" == "$REMOTE_NAME" ]; then
                echo -e "${RED}❌ ERROR: versionName was not changed!${NC}"
                echo "   Version: $CURRENT_NAME"
                echo ""

                SUGGESTED=$(suggest_version_bump "$REMOTE_NAME" "$BRANCH_NAME")
                echo "   Suggested version: $SUGGESTED"
                echo ""
                exit 1
            else
                # Compare versions
                BUMP_TYPE=$(compare_versions "$CURRENT_NAME" "$REMOTE_NAME")

                if [ "$BUMP_TYPE" == "downgrade" ]; then
                    echo -e "${RED}❌ ERROR: Version downgrade detected!${NC}"
                    echo "   Remote: $REMOTE_NAME"
                    echo "   Local:  $CURRENT_NAME"
                    echo ""
                    exit 1
                elif [ "$BUMP_TYPE" == "same" ]; then
                    echo -e "${YELLOW}⚠️  Warning: Version appears the same${NC}"
                else
                    echo -e "${GREEN}✅ versionName bumped ($BUMP_TYPE)${NC}"
                    echo "   $REMOTE_NAME → $CURRENT_NAME"
                fi
            fi

            # Additional checks based on branch type
            if [[ $BRANCH_NAME =~ ^hotfix/ ]]; then
                IFS='|' read -r r_major r_minor r_patch <<< "$(parse_semver "$REMOTE_NAME")"

                if [ "$c_major" != "$r_major" ] || [ "$c_minor" != "$r_minor" ]; then
                    echo -e "${YELLOW}⚠️  Warning: Hotfix should only bump patch version${NC}"
                    echo "   Expected: ${r_major}.${r_minor}.$((r_patch + 1))"
                    echo "   Got: $CURRENT_NAME"
                    echo ""
                fi
            fi

            if [[ $BRANCH_NAME =~ ^release/ ]]; then
                # Extract version from branch name
                BRANCH_VERSION=$(echo "$BRANCH_NAME" | sed 's/release\///')

                if [ "$CURRENT_NAME" != "$BRANCH_VERSION" ]; then
                    echo -e "${YELLOW}⚠️  Warning: Version mismatch with branch name${NC}"
                    echo "   Branch: release/$BRANCH_VERSION"
                    echo "   Version: $CURRENT_NAME"
                    echo ""
                fi
            fi
        fi
    else
        echo -e "${YELLOW}ℹ️  No remote branch found for comparison${NC}"
        echo "   This appears to be a new branch"
        echo ""
    fi
fi

# Check for version consistency
if [[ $CURRENT_NAME =~ ^([0-9]+)\.([0-9]+)\.([0-9]+) ]]; then
    VERSION_MAJOR="${BASH_REMATCH[1]}"
    VERSION_MINOR="${BASH_REMATCH[2]}"
    VERSION_PATCH="${BASH_REMATCH[3]}"

    # Calculate expected versionCode (e.g., major*10000 + minor*100 + patch)
    EXPECTED_CODE=$((VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH))

    if [ "$CURRENT_CODE" != "$EXPECTED_CODE" ]; then
        echo -e "${YELLOW}ℹ️  Note: versionCode doesn't follow semantic versioning formula${NC}"
        echo "   Current: $CURRENT_CODE"
        echo "   Expected (MAJOR*10000 + MINOR*100 + PATCH): $EXPECTED_CODE"
        echo "   This is OK if you're using a different versioning scheme"
        echo ""
    fi
fi

# Summary
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Version validation passed${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "📋 Version Summary:"
echo "   Code: $CURRENT_CODE"
echo "   Name: $CURRENT_NAME"
echo "   Branch: $BRANCH_NAME"
echo ""

exit 0
