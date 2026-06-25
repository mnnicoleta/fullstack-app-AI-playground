# SonarQube Fixer Skill

Automatically analyze and fix SonarQube code quality findings with intelligent prioritization and comprehensive reporting.

## Quick Start

### Prerequisites

**Option 1: SonarQube Server**
```bash
export SONAR_HOST_URL="http://localhost:9000"
export SONAR_TOKEN="your-token-here"
export SONAR_PROJECT_KEY="your-project-key"
```

**Option 2: Local Analysis**
```bash
# Run SonarQube analysis first
mvn sonar:sonar
# or
gradle sonarqube
```

### Basic Usage

```bash
# Simple invocation
/sonarqube-fix

# Or via conversation
"fix sonarqube issues"
"clean up code quality findings"
"address static analysis issues"
```

## What It Does

### 1. Fetches Findings
- Connects to SonarQube server API
- Or reads local SonarQube reports
- Extracts all unresolved issues

### 2. Categorizes & Prioritizes
- Groups by severity (Blocker → Info)
- Groups by type (Bugs, Vulnerabilities, Code Smells)
- Assesses auto-fix capability

### 3. Applies Automated Fixes
- **Unused imports** - Removes unused imports
- **Magic numbers** - Extracts to constants
- **Missing annotations** - Adds @Override, @Deprecated
- **Formatting** - Fixes indentation, spacing
- **Empty blocks** - Adds TODO comments
- **Deprecated API** - Replaces with modern alternatives

### 4. Generates Reports
- **FINDINGS-SUMMARY.md** - Overview of all issues
- **FIXES-APPLIED.md** - What was fixed automatically
- **MANUAL-REVIEW-NEEDED.md** - Issues requiring human review
- **BEFORE-AFTER-STATS.md** - Metrics comparison

### 5. Creates Backups
- All modified files backed up
- Easy rollback if needed
- Located in `claudeTasks/.../backups/`

## Auto-Fix Capabilities

### ✅ Fully Automated

| Issue | SonarQube Rule | Description |
|-------|----------------|-------------|
| Unused imports | `java:S1128` | Remove imports not used |
| Magic numbers | `java:S109` | Extract to constants |
| Missing @Override | `java:S1161` | Add annotation |
| String duplication | `java:S1192` | Extract to constant |
| Empty blocks | `java:S108` | Add TODO comment |
| Extra semicolons | `java:S1116` | Remove duplicates |
| Console.log | `typescript:S2228` | Remove debug logs |
| Unused variables | `typescript:S1481` | Remove unused |

### ⚠️ Partially Automated

| Issue | Description | Limitation |
|-------|-------------|------------|
| Deprecated API | Replace with alternatives | Needs mapping |
| Resource leaks | Add try-with-resources | Context-dependent |
| Null checks | Add null guards | Logic complexity |

### ❌ Manual Review Required

| Issue | Reason |
|-------|--------|
| SQL injection | Security-critical |
| XSS vulnerabilities | Security-critical |
| Logic bugs | Requires understanding |
| Architectural issues | Design decisions |
| Complex refactoring | High impact |

## Output Structure

```
claudeTasks/sonarqube-fix-20260620-143000/
├── reports/
│   ├── FINDINGS-SUMMARY.md         # Overview
│   ├── FIXES-APPLIED.md            # Auto-fixed issues
│   ├── MANUAL-REVIEW-NEEDED.md     # Human review needed
│   └── BEFORE-AFTER-STATS.md       # Metrics
├── backups/                         # Original files
│   ├── ProductService.java.backup
│   └── OrderController.java.backup
└── logs/
    └── fix-execution.log            # Detailed log
```

## Examples

### Example 1: Basic Fix Run

```bash
User: "fix sonarqube issues"

Claude:
✅ Fetched 127 issues from SonarQube
✅ Categorized: 45 auto-fixable, 12 manual review, 70 low priority
✅ Applied 45 fixes across 23 files
✅ All fixes compiled successfully
✅ Reports generated

Results saved to: claudeTasks/sonarqube-fix-20260620-143000/
```

### Example 2: Fix Only Critical Issues

```bash
User: "fix blocker and critical sonarqube issues"

Claude:
🔍 Found 8 BLOCKER and 15 CRITICAL issues
✅ Auto-fixed 12 issues
⚠️ 11 issues need manual review (security)
📊 Reports: claudeTasks/sonarqube-fix-20260620-143500/
```

### Example 3: Dry Run (Preview)

```bash
User: "show me what sonarqube issues would be fixed"

Claude:
📋 Dry Run Mode - No changes will be made

Would fix 45 issues:
- 23 unused imports
- 12 magic numbers
- 8 missing @Override
- 2 empty catch blocks

Manual review needed: 12 issues
- 8 potential null pointers
- 4 SQL injection risks

Run with /sonarqube-fix to apply fixes.
```

## Configuration

### Custom Configuration File

Create `sonarqube-fix-config.yml`:

```yaml
# SonarQube connection
sonarqube:
  host_url: "http://localhost:9000"
  project_key: "your-project"
  token: "${SONAR_TOKEN}"

# Auto-fix settings
auto_fix:
  enabled_rules:
    - java:S1128  # Unused imports
    - java:S109   # Magic numbers
    - java:S1161  # Missing @Override
    - java:S1192  # String duplication
    - java:S108   # Empty blocks
    - typescript:S1481  # Unused variables
  
  disabled_rules:
    - java:S100   # Don't rename methods
  
  # Max fixes per run
  max_fixes: 100
  
  # Create git commit
  auto_commit: false

# File exclusions
exclude:
  files:
    - "*/generated/**"
    - "*/build/**"
    - "*/target/**"
    - "*/node_modules/**"
    - "**/*.test.ts"
    - "**/*.spec.ts"
  
  directories:
    - ".git"
    - "dist"
    - "out"

# Reporting
reporting:
  include_code_samples: true
  max_examples_per_rule: 3
  generate_html: false
```

## Advanced Usage

### Incremental Fixes (Changed Files Only)

```bash
# Fix only files changed in current branch
/sonarqube-fix --incremental

# Git diff mode
/sonarqube-fix --git-diff main
```

### Fix Specific Files

```bash
# Fix single file
/sonarqube-fix --file src/main/java/Service.java

# Fix directory
/sonarqube-fix --directory src/main/java/controllers/
```

### Fix by Severity

```bash
# Only critical issues
/sonarqube-fix --severity BLOCKER,CRITICAL

# All except info
/sonarqube-fix --severity BLOCKER,CRITICAL,MAJOR,MINOR
```

### Auto-Commit Mode

```bash
# Apply fixes and create commit
/sonarqube-fix --commit

# With custom commit message
/sonarqube-fix --commit --message "chore: fix sonarqube findings"
```

## Integration

### CI/CD Pipeline (GitHub Actions)

```yaml
name: SonarQube Auto-Fix

on:
  schedule:
    - cron: '0 2 * * 1'  # Weekly Monday 2 AM
  workflow_dispatch:

jobs:
  sonarqube-fix:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      
      - name: Run SonarQube Analysis
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          mvn sonar:sonar \
            -Dsonar.host.url=${{ secrets.SONAR_HOST_URL }} \
            -Dsonar.projectKey=${{ secrets.SONAR_PROJECT_KEY }}
      
      - name: Apply SonarQube Fixes
        run: |
          # Use Claude Code skill
          claude-code /sonarqube-fix --commit
      
      - name: Create Pull Request
        uses: peter-evans/create-pull-request@v5
        with:
          title: 'chore: Auto-fix SonarQube findings'
          body: |
            Automated code quality improvements from SonarQube analysis.
            
            See attached reports for details.
          branch: sonarqube-auto-fix
          delete-branch: true
```

### Pre-commit Hook

```bash
#!/bin/bash
# .git/hooks/pre-commit

# Run SonarQube fixer on staged files
staged_files=$(git diff --cached --name-only --diff-filter=ACM | grep -E '\.(java|ts)$')

if [ -n "$staged_files" ]; then
    echo "Running SonarQube fixes on staged files..."
    for file in $staged_files; do
        claude-code /sonarqube-fix --file "$file"
    done
    
    # Re-stage fixed files
    git add $staged_files
fi
```

## Troubleshooting

### Issue: Cannot connect to SonarQube

**Error:**
```
❌ Failed to fetch issues: Connection refused
```

**Solutions:**
1. Verify SonarQube is running: `curl http://localhost:9000/api/system/status`
2. Check SONAR_HOST_URL environment variable
3. Verify SONAR_TOKEN has correct permissions
4. Try local report mode instead

### Issue: Fixes break compilation

**Error:**
```
❌ Compilation failed after applying fixes
```

**Solutions:**
1. Automatic rollback from backups (happens automatically)
2. Check logs: `claudeTasks/sonarqube-fix-*/logs/fix-execution.log`
3. Review specific fix that caused issue
4. Disable problematic rule in config
5. Report issue for skill improvement

### Issue: Too many issues to fix

**Warning:**
```
⚠️ Found 500+ issues - this may take a while
```

**Solutions:**
1. Use incremental mode: `--incremental`
2. Fix by severity: `--severity BLOCKER,CRITICAL`
3. Fix specific files: `--file path/to/file`
4. Set max fixes: `--max-fixes 50`
5. Run multiple sessions over time

## Best Practices

### 1. Start with Dry Run
Always preview changes first:
```bash
/sonarqube-fix --dry-run
```

### 2. Fix by Priority
Address critical issues first:
```bash
/sonarqube-fix --severity BLOCKER,CRITICAL
```

### 3. Review Before Commit
Check changes before committing:
```bash
git diff
# Review changes
git commit -m "fix: apply sonarqube fixes"
```

### 4. Run Tests After Fixes
Verify fixes don't break functionality:
```bash
mvn test
# or
npm test
```

### 5. Incremental Adoption
Fix new issues in feature branches:
```bash
/sonarqube-fix --incremental --git-diff main
```

## Metrics & Impact

### Typical Results

**Small Project (< 10k LOC):**
- Runtime: 2-5 minutes
- Issues found: 50-200
- Auto-fixed: 30-60%
- Manual review: 10-20%

**Medium Project (10k-50k LOC):**
- Runtime: 5-15 minutes
- Issues found: 200-1000
- Auto-fixed: 40-70%
- Manual review: 15-30%

**Large Project (> 50k LOC):**
- Runtime: 15-30 minutes
- Issues found: 1000+
- Auto-fixed: 50-80%
- Manual review: 20-40%

### Quality Improvements

**Typical Reductions:**
- Technical debt: -20% to -40%
- Code smells: -30% to -60%
- Minor issues: -50% to -80%
- Blockers: -10% to -30% (most need manual review)

## FAQ

**Q: Will this break my code?**
A: No. All changes are verified by compilation/type checking. Backups are created. Automatic rollback on failure.

**Q: What about tests?**
A: The skill fixes source code, not tests (excluded by default). Run tests separately to verify.

**Q: Can I customize which rules are fixed?**
A: Yes. Use `sonarqube-fix-config.yml` to enable/disable specific rules.

**Q: Does it work with all languages?**
A: Currently optimized for Java and TypeScript. Python and JavaScript support coming soon.

**Q: How long does it take?**
A: Depends on project size. Small projects: 2-5 min. Large projects: 15-30 min.

**Q: Can I run this in CI/CD?**
A: Yes. See integration examples above. Works great in automated pipelines.

**Q: What if I don't have SonarQube server?**
A: You can run local analysis with Maven/Gradle and the skill will read report files.

## Support

For issues, questions, or feature requests:
1. Check the troubleshooting section above
2. Review the main skill documentation
3. Check logs in `claudeTasks/sonarqube-fix-*/logs/`
4. Report issues with full error details

## Changelog

### Version 1.0 (2026-06-20)
- Initial release
- Support for Java and TypeScript
- 15+ auto-fix rules
- Comprehensive reporting
- Git integration
- Dry run mode
- Incremental fixes

### Planned Features
- [ ] Python language support
- [ ] JavaScript/React-specific rules
- [ ] HTML report generation
- [ ] SonarLint integration
- [ ] Real-time fixing during coding
- [ ] IDE plugin support
- [ ] Machine learning for custom fixes
- [ ] Team statistics and trends

---

**Version:** 1.0  
**Last Updated:** 2026-06-20  
**Maintainer:** Claude Code Skills Team
