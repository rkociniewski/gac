# 🛠️ Gospel A Caso Scripts

This directory contains utility scripts for development, testing, and automation.

## 📋 Available Scripts

### 🪝 Git Hooks Management

#### `setup-git-hooks.sh`

Installs Git hooks for commit validation and code quality checks.

**Usage:**
```bash
./scripts/setup-git-hooks.sh
```

**What it does:**
- Creates hooks in `.githooks/` directory
- Symlinks hooks to `.git/hooks/`
- Makes all hooks executable
- Verifies installation

**Installed hooks:**
- `commit-msg` - Validates commit message format
- `pre-commit` - Checks code quality and secrets
- `prepare-commit-msg` - Adds issue numbers
- `post-commit` - Warns about version bumps
- `pre-push` - Enforces version increments

**Output:**
```
🔧 Setting up Git hooks...
✅ Installed commit-msg
✅ Installed pre-commit
✅ Installed prepare-commit-msg
✅ Installed post-commit
✅ Installed pre-push
🎉 Git hooks installed successfully!
```

---

#### `test-hooks.sh`

Tests Git hooks without actually committing code.

**Usage:**
```bash
# Test all hooks
./scripts/test-hooks.sh all

# Test specific hook
./scripts/test-hooks.sh commit-msg
./scripts/test-hooks.sh pre-commit
./scripts/test-hooks.sh pre-push
```

**What it tests:**
- Commit message validation (valid/invalid formats)
- Debug statement detection
- Secret detection
- Large file detection
- Merge conflict markers
- Version extraction and validation

**Example output:**
```
🔍 Testing Git hooks...

Testing commit-msg hook...
PASS: valid message - "feat: Add new feature"
PASS: valid with scope - "feat(auth): Add login"
FAIL: missing type - "Added new feature"
PASS: merge commit allowed

Testing pre-commit hook...
PASS: Debug statements detected
PASS: Large file blocked
PASS: Secret detected
PASS: Merge conflict detected

Results: 12 passed, 0 failed
✅ All tests passed!
```

---

#### `validate-hooks.sh`

Validates Git hooks syntax before installation.

**Usage:**
```bash
./scripts/validate-hooks.sh
```

**What it checks:**
- Shell script syntax (using `bash -n`)
- File existence
- Proper shebang
- No syntax errors

**Output:**
```
🔍 Validating Git hooks...
Checking commit-msg...
  ✅ commit-msg - syntax OK
Checking pre-commit...
  ✅ pre-commit - syntax OK
Checking setup-git-hooks.sh...
  ✅ setup-git-hooks.sh - syntax OK

✅ All hooks validated successfully!
```

---

### 📦 Version Management

#### `advanced-version-check.sh`

Advanced version validation and semantic versioning checks.

**Usage:**
```bash
./scripts/advanced-version-check.sh
```

**What it does:**
- Extracts version from `build.gradle.kts`
- Validates semantic versioning format
- Compares with remote branch
- Checks version consistency
- Suggests appropriate version bump

**Used by:** `pre-push` Git hook

**Features:**
- Semantic version parsing (MAJOR.MINOR.PATCH)
- Version comparison (upgrade, downgrade, same)
- Branch-specific recommendations (hotfix, release, feature)
- Detailed error messages

**Output:**
```
🔍 Checking version information...

📦 Current Version:
   versionCode: 10204
   versionName: 1.2.4
   Branch: hotfix/1.2.4

📦 Remote Version:
   versionCode: 10203
   versionName: 1.2.3

✅ versionCode incremented by 1
✅ versionName bumped (patch)
   1.2.3 → 1.2.4

✅ Version validation passed
```

---

## 🚀 Quick Start

### First Time Setup

```bash
# 1. Make scripts executable (if needed)
chmod +x scripts/*.sh

# 2. Install Git hooks
./scripts/setup-git-hooks.sh

# 3. Verify installation
./scripts/validate-hooks.sh

# 4. Test hooks
./scripts/test-hooks.sh all
```

### Daily Development

```bash
# Just code and commit normally
git add .
git commit -m "feat: Add feature"
git push

# Hooks run automatically!
```

### Troubleshooting

```bash
# If hooks don't work
./scripts/validate-hooks.sh

# Reinstall hooks
./scripts/setup-git-hooks.sh

# Test specific scenario
./scripts/test-hooks.sh pre-commit
```

---

## 📖 Detailed Documentation

### Script Architecture

```
scripts/
├── setup-git-hooks.sh          # Main installation script
│   ├── Creates .githooks/ directory
│   ├── Generates all hook scripts
│   └── Symlinks to .git/hooks/
│
├── test-hooks.sh               # Testing framework
│   ├── Tests commit-msg validation
│   ├── Tests pre-commit checks
│   └── Tests version validation
│
├── validate-hooks.sh           # Syntax validator
│   └── Checks all scripts for errors
│
└── advanced-version-check.sh  # Version management
    ├── Semantic version parsing
    ├── Version comparison
    └── Branch-aware recommendations
```

### Hook Flow

```
Developer commits
      ↓
  pre-commit
      ↓ (checks)
   commit-msg
      ↓ (validates)
prepare-commit-msg
      ↓ (enhances)
  post-commit
      ↓ (warns)
Developer pushes
      ↓
   pre-push
      ↓ (validates version)
    SUCCESS
```

---

## 🔧 Configuration

### Customizing Hooks

Edit `.githooks/config`:

```bash
# Debug statement exclusions
DEBUG_EXCLUDE_PATTERNS=(
    "Logger"
    "MyCustomLogger"  # Add your logger
)

# Secret detection exclusions
SECRET_EXCLUDE_PATTERNS=(
    "\.md$"
    "test/"
    "examples/"  # Add custom exclusions
)

# File size limit (bytes)
MAX_FILE_SIZE=5242880  # 5MB
```

### Environment Variables

```bash
# Skip pre-commit checks (emergency only!)
SKIP_PRE_COMMIT=1 git commit -m "emergency fix"

# Verbose output
VERBOSE=true git commit -m "feat: Add feature"
```

---

## 🐛 Troubleshooting

### Common Issues

**Hook not executable**
```bash
chmod +x .git/hooks/pre-commit
# Or reinstall:
./scripts/setup-git-hooks.sh
```

**Syntax error**
```bash
./scripts/validate-hooks.sh
# Fix errors shown, then:
./scripts/setup-git-hooks.sh
```

**Hook not running**
```bash
# Check if hooks are installed
ls -la .git/hooks/

# Reinstall
./scripts/setup-git-hooks.sh

# Test manually
bash .git/hooks/pre-commit
```

**False positive in checks**
```bash
# Temporary bypass
git commit --no-verify -m "fix: Emergency"

# Or customize exclusions in .githooks/config
```

---

## 📚 Related Documentation

- [Git Hooks Guide](../docs/GIT_HOOKS.md)
- [Hooks Examples](../docs/HOOKS_EXAMPLES.md)
- [Hooks FAQ](../docs/GIT_HOOKS_FAQ.md)
- [Hooks Cheat Sheet](../docs/GIT_HOOKS_CHEATSHEET.md)

---

## 🔄 Updating Scripts

When updating scripts:

1. **Test changes locally**
   ```bash
   bash -n scripts/setup-git-hooks.sh
   ./scripts/test-hooks.sh all
   ```

2. **Update version in script**
   ```bash
   # Add to script header:
   # Version: 1.1.0
   # Last Updated: 2024-10-11
   ```

3. **Update documentation**
    - Update this README
    - Update related docs
    - Add to CHANGELOG

4. **Commit with proper message**
   ```bash
   git commit -m "ci: Update Git hooks setup script

   - Added new exclusion patterns
   - Improved error messages
   - Fixed Windows compatibility"
   ```

5. **Notify team**
   ```bash
   # In PR or Slack:
   "Git hooks updated - please run: ./scripts/setup-git-hooks.sh"
   ```

---

## 🤝 Contributing

To add new scripts:

1. Create script in `scripts/` directory
2. Make it executable: `chmod +x scripts/new-script.sh`
3. Add documentation to this README
4. Add tests to `test-hooks.sh` if applicable
5. Submit PR with examples

### Script Template

```bash
#!/bin/bash
# Description: What this script does
# Usage: ./scripts/my-script.sh [options]
# Version: 1.0.0

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Main logic
echo "🚀 Starting script..."

# ... your code ...

echo -e "${GREEN}✅ Done!${NC}"
```

---

## 📞 Support

**Issues with scripts?**

1. Check [Git Hooks FAQ](../docs/GIT_HOOKS_FAQ.md)
2. Run `./scripts/validate-hooks.sh`
3. Check logs: `bash -x scripts/setup-git-hooks.sh`
4. Create GitHub issue with `scripts` label

**Emergency bypass:**
```bash
git commit --no-verify -m "emergency: reason"
```

---

**Last Updated**: 2024-10-11
**Maintained By**: [@rkociniewski](https://github.com/rkociniewski)
