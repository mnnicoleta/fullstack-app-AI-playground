---
name: sonarqube-fix
description: Automatically fix SonarQube code quality findings. Use this skill when the user wants to fix SonarQube issues, improve code quality, address static analysis findings, or clean up technical debt. This skill fetches SonarQube findings, analyzes them, automatically fixes common issues, and provides detailed reports on what was fixed and what needs manual attention. ALWAYS use this when users mention "fix sonarqube", "sonarqube issues", "code quality", "static analysis", or "clean up findings".
---

# SonarQube Findings Fixer

This skill automatically analyzes and fixes SonarQube code quality findings in your codebase. It connects to SonarQube (or analyzes local reports), categorizes issues, applies automated fixes where possible, and provides comprehensive reports on improvements and remaining issues.

## Overview

### Capabilities

1. **Fetch Findings** - Connect to SonarQube server or read local reports
2. **Prioritize Issues** - Sort by severity (Blocker, Critical, Major, Minor, Info)
3. **Auto-Fix** - Automatically fix common issues (formatting, imports, simple bugs)
4. **Manual Review** - Flag complex issues needing human review
5. **Reporting** - Generate detailed before/after reports
6. **Commit Changes** - Optionally create git commits with fixes

### Issue Types Supported

- **Bugs** - Logic errors, null pointer risks, resource leaks
- **Vulnerabilities** - Security issues, injection risks, crypto weaknesses
- **Code Smells** - Maintainability issues, complexity, duplication
- **Security Hotspots** - Security-sensitive code requiring review
- **Coverage** - Missing test coverage (flagged, not auto-fixed)

---

## Configuration

### SonarQube Connection

**Option 1: SonarQube Server API**
```bash
# Environment variables
export SONAR_HOST_URL="http://localhost:9000"
export SONAR_TOKEN="your-sonarqube-token"
export SONAR_PROJECT_KEY="your-project-key"
```

**Option 2: Local Report Files**
```bash
# Read from SonarQube report files
# Typically in: target/sonar/ or .scannerwork/
```

**Option 3: Maven/Gradle Integration**
```bash
# Run SonarQube analysis first
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000
# or
gradle sonarqube
```

### Output Structure

All fixes and reports saved to:
```
claudeTasks/sonarqube-fix-YYYYMMDD-HHMMSS/
├── reports/
│   ├── FINDINGS-SUMMARY.md         # Overview of all findings
│   ├── FIXES-APPLIED.md            # What was fixed automatically
│   ├── MANUAL-REVIEW-NEEDED.md    # Issues requiring human review
│   └── BEFORE-AFTER-STATS.md      # Metrics comparison
├── backups/                        # Original files before changes
│   └── {file-path}.backup
└── logs/
    └── fix-execution.log           # Detailed execution log
```

---

## Workflow

### Step 1: Fetch SonarQube Findings

**Check for SonarQube connection:**
```bash
# Option 1: Check SonarQube server
curl -s -u ${SONAR_TOKEN}: "${SONAR_HOST_URL}/api/system/status" | jq .

# Option 2: Check for local reports
find . -name "sonar-report.json" -o -name "issues-report.json"

# Option 3: Check if Maven/Gradle are configured
grep -r "sonar" pom.xml build.gradle 2>/dev/null
```

**Fetch findings via API:**
```bash
# Fetch all issues for project
curl -s -u ${SONAR_TOKEN}: \
  "${SONAR_HOST_URL}/api/issues/search?componentKeys=${SONAR_PROJECT_KEY}&resolved=false&ps=500" \
  > sonarqube-issues.json
```

**Parse findings:**
- Extract issue key, type, severity, rule, file, line, message
- Categorize by type (Bug, Vulnerability, Code Smell, etc.)
- Sort by severity (Blocker → Critical → Major → Minor → Info)
- Group by file and rule

### Step 2: Create Output Directory

```bash
timestamp=$(date +%Y%m%d-%H%M%S)
output_dir="claudeTasks/sonarqube-fix-${timestamp}"
mkdir -p "${output_dir}"/{reports,backups,logs}
```

### Step 3: Analyze and Categorize

**Prioritization Matrix:**

| Severity | Bug | Vulnerability | Code Smell | Priority |
|----------|-----|---------------|------------|----------|
| Blocker | P1 | P1 | P2 | Fix First |
| Critical | P1 | P1 | P2 | Fix First |
| Major | P2 | P2 | P3 | Fix Second |
| Minor | P3 | P3 | P4 | Fix Last |
| Info | P4 | P4 | P5 | Optional |

**Auto-Fix Capability Assessment:**

| Issue Type | Auto-Fixable | Examples |
|------------|--------------|----------|
| Unused imports | ✅ Yes | Remove unused imports |
| Formatting | ✅ Yes | Indentation, spacing, line length |
| Deprecated API | ✅ Yes | Replace with recommended alternative |
| Missing annotations | ✅ Yes | Add @Override, @Deprecated |
| Simple null checks | ✅ Yes | Add null guards |
| Magic numbers | ✅ Yes | Extract to constants |
| Resource leaks | ⚠️ Partial | Add try-with-resources |
| Security issues | ❌ Manual | Requires security review |
| Logic bugs | ❌ Manual | Requires understanding context |
| Complex refactoring | ❌ Manual | Requires architectural decisions |

### Step 4: Apply Automated Fixes

For each auto-fixable issue:

1. **Backup original file:**
   ```bash
   cp file.java "${output_dir}/backups/file.java.backup"
   ```

2. **Apply fix based on rule:**
   - Use Read to load file
   - Use Edit to apply fix
   - Verify fix doesn't break syntax

3. **Log fix:**
   ```
   [FIXED] file.java:123 - Removed unused import: java.util.ArrayList
   [FIXED] file.java:456 - Extracted magic number to constant: MAX_RETRIES
   ```

4. **Track statistics:**
   - Count fixes by type
   - Count fixes by severity
   - Measure changes (lines added/removed)

### Step 5: Generate Reports

Create comprehensive reports on all findings and fixes.

---

## Auto-Fix Rules

### Java/Spring Boot

#### Rule: Unused Imports
**SonarQube Rule:** `java:S1128`
**Auto-Fix:** Remove unused import statements

```java
// Before
import java.util.ArrayList;  // Unused
import java.util.List;
import java.util.Map;  // Unused

public class Example {
    List<String> items = new ArrayList<>();
}

// After
import java.util.ArrayList;
import java.util.List;

public class Example {
    List<String> items = new ArrayList<>();
}
```

**Implementation:**
1. Parse import statements
2. Scan file for usage of each import
3. Remove unused imports
4. Preserve import order and grouping

---

#### Rule: Missing @Override
**SonarQube Rule:** `java:S1161`
**Auto-Fix:** Add @Override annotation

```java
// Before
public class Service extends BaseService {
    public void process() {  // Missing @Override
        // implementation
    }
}

// After
public class Service extends BaseService {
    @Override
    public void process() {
        // implementation
    }
}
```

---

#### Rule: Magic Numbers
**SonarQube Rule:** `java:S109`
**Auto-Fix:** Extract to named constant

```java
// Before
public void retry() {
    for (int i = 0; i < 3; i++) {  // Magic number
        // retry logic
    }
}

// After
private static final int MAX_RETRIES = 3;

public void retry() {
    for (int i = 0; i < MAX_RETRIES; i++) {
        // retry logic
    }
}
```

---

#### Rule: String Literals Duplicated
**SonarQube Rule:** `java:S1192`
**Auto-Fix:** Extract to constant

```java
// Before
log.info("Processing order");
// ... 50 lines later
log.info("Processing order");  // Duplicated

// After
private static final String MSG_PROCESSING = "Processing order";

log.info(MSG_PROCESSING);
// ... 50 lines later
log.info(MSG_PROCESSING);
```

---

#### Rule: Empty Block
**SonarQube Rule:** `java:S108`
**Auto-Fix:** Add comment or remove

```java
// Before
try {
    // some code
} catch (Exception e) {
    // Empty catch block
}

// After
try {
    // some code
} catch (Exception e) {
    // TODO: Handle exception appropriately
    log.error("Error processing request", e);
}
```

---

#### Rule: Unnecessary Semicolons
**SonarQube Rule:** `java:S1116`
**Auto-Fix:** Remove extra semicolons

```java
// Before
public void method() {
    int x = 5;;  // Double semicolon
    return;;
}

// After
public void method() {
    int x = 5;
    return;
}
```

---

#### Rule: Deprecated API Usage
**SonarQube Rule:** `java:S1874`
**Auto-Fix:** Replace with recommended alternative

```java
// Before
Date date = new Date();  // Deprecated constructor

// After
Instant instant = Instant.now();
// or
LocalDateTime dateTime = LocalDateTime.now();
```

---

### TypeScript/Angular

#### Rule: Unused Variables
**SonarQube Rule:** `typescript:S1481`
**Auto-Fix:** Remove unused variables

```typescript
// Before
function calculate(a: number, b: number) {
    const result = a + b;
    const unused = 42;  // Unused variable
    return result;
}

// After
function calculate(a: number, b: number) {
    const result = a + b;
    return result;
}
```

---

#### Rule: Console.log in Production
**SonarQube Rule:** `typescript:S2228`
**Auto-Fix:** Remove or replace with logger

```typescript
// Before
console.log("Debug info", data);  // Should not be in production

// After
// Option 1: Remove
// (removed)

// Option 2: Replace with logger
this.logger.debug("Debug info", data);
```

---

#### Rule: Any Type Usage
**SonarQube Rule:** `typescript:S6571`
**Manual Review:** Requires type definition
**Flag for Review:** Cannot auto-fix without context

```typescript
// Before
function process(data: any) {  // Should have specific type
    return data.value;
}

// Needs Manual Fix - example:
function process(data: DataModel) {
    return data.value;
}
```

---

### SQL/Database

#### Rule: SQL Injection Risk
**SonarQube Rule:** `java:S2077`, `typescript:S2077`
**Manual Review:** Requires parameterization
**Flag for Review:** Security-critical

```java
// Before (Vulnerable)
String query = "SELECT * FROM users WHERE id = " + userId;  // SQL injection risk
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(query);

// Needs Manual Fix
String query = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, userId);
ResultSet rs = stmt.executeQuery();
```

---

## Manual Review Rules

These issues require human review and cannot be auto-fixed:

### Security Issues

**Examples:**
- SQL injection vulnerabilities
- XSS vulnerabilities
- Hardcoded credentials
- Weak cryptographic algorithms
- Insecure deserialization
- Path traversal risks

**Action:** Flag for security review with detailed context

---

### Logic Bugs

**Examples:**
- Null pointer dereference (complex cases)
- Race conditions
- Dead code (non-obvious)
- Incorrect logic
- Off-by-one errors

**Action:** Flag with explanation and suggest fix

---

### Architectural Issues

**Examples:**
- God classes (high complexity)
- Circular dependencies
- Tight coupling
- Missing abstraction
- Poor separation of concerns

**Action:** Suggest refactoring approach, don't auto-change

---

## Execution Steps

### Step 1: Analyze Findings

```bash
# Fetch and parse findings
total_issues=$(jq '.total' sonarqube-issues.json)
blockers=$(jq '[.issues[] | select(.severity=="BLOCKER")] | length' sonarqube-issues.json)
critical=$(jq '[.issues[] | select(.severity=="CRITICAL")] | length' sonarqube-issues.json)
major=$(jq '[.issues[] | select(.severity=="MAJOR")] | length' sonarqube-issues.json)

echo "Total Issues: ${total_issues}"
echo "Blockers: ${blockers}"
echo "Critical: ${critical}"
echo "Major: ${major}"
```

---

### Step 2: Categorize by Auto-Fix Capability

```python
# Pseudo-code for categorization
auto_fixable = []
manual_review = []
wont_fix = []

for issue in issues:
    if issue.rule in AUTO_FIX_RULES:
        auto_fixable.append(issue)
    elif issue.severity in ['BLOCKER', 'CRITICAL']:
        manual_review.append(issue)
    else:
        wont_fix.append(issue)  # Low priority, user decides
```

---

### Step 3: Apply Fixes (Priority Order)

**Order of Operations:**

1. **Blocker Bugs** - Critical logic errors
2. **Critical Vulnerabilities** - Security issues
3. **Major Code Smells** - Significant maintainability
4. **Minor Issues** - Formatting, style
5. **Info Issues** - Optional improvements

**For Each Issue:**

```bash
# 1. Backup file
cp ${file} ${output_dir}/backups/$(basename ${file}).backup

# 2. Read file
Read file_path=${file}

# 3. Apply fix based on rule
case ${rule} in
    "java:S1128")  # Unused imports
        # Remove unused import
        Edit file_path=${file} old_string="import ${unused_import};" new_string=""
        ;;
    "java:S109")  # Magic numbers
        # Extract to constant
        # ... complex logic
        ;;
esac

# 4. Log fix
echo "[FIXED] ${file}:${line} - ${rule}: ${message}" >> ${output_dir}/logs/fix-execution.log
```

---

### Step 4: Verify Fixes

**Java Projects:**
```bash
# Compile to verify no syntax errors
mvn clean compile -DskipTests
# Check exit code
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed - reverting changes"
    # Restore from backups
fi
```

**TypeScript Projects:**
```bash
# Type check
npm run build -- --noEmit
if [ $? -eq 0 ]; then
    echo "✅ Type checking passed"
else
    echo "❌ Type errors - reverting changes"
fi
```

---

### Step 5: Generate Reports

Create comprehensive markdown reports.

---

## Report Templates

### FINDINGS-SUMMARY.md

```markdown
# SonarQube Findings Summary

**Project:** {project_name}
**Analysis Date:** {date}
**Total Issues:** {total}

---

## Overview by Severity

| Severity | Bugs | Vulnerabilities | Code Smells | Total |
|----------|------|-----------------|-------------|-------|
| Blocker  | {n}  | {n}             | {n}         | {sum} |
| Critical | {n}  | {n}             | {n}         | {sum} |
| Major    | {n}  | {n}             | {n}         | {sum} |
| Minor    | {n}  | {n}             | {n}         | {sum} |
| Info     | {n}  | {n}             | {n}         | {sum} |
| **Total**| {n}  | {n}             | {n}         | {n}   |

---

## Auto-Fix Assessment

| Category | Count | Auto-Fixable | Manual Review | Won't Fix |
|----------|-------|--------------|---------------|-----------|
| Blocker  | {n}   | {n}          | {n}           | {n}       |
| Critical | {n}   | {n}          | {n}           | {n}       |
| Major    | {n}   | {n}          | {n}           | {n}       |
| Minor    | {n}   | {n}          | {n}           | {n}       |
| **Total**| {n}   | {n} ({pct}%) | {n} ({pct}%) | {n}       |

---

## Top Issues by Frequency

| Rule | Description | Count | Severity |
|------|-------------|-------|----------|
| java:S1128 | Unused imports | {n} | Minor |
| java:S109 | Magic numbers | {n} | Minor |
| java:S2259 | Null pointer | {n} | Critical |
| ... | ... | ... | ... |

---

## Files with Most Issues

| File | Issues | Blocker | Critical | Major |
|------|--------|---------|----------|-------|
| {file1} | {n} | {n} | {n} | {n} |
| {file2} | {n} | {n} | {n} | {n} |
| ... | ... | ... | ... | ... |
```

---

### FIXES-APPLIED.md

```markdown
# Fixes Applied

**Session:** {session_id}
**Date:** {date}
**Total Fixes:** {total}

---

## Summary Statistics

- **Files Modified:** {n}
- **Lines Changed:** {added} added, {removed} removed
- **Issues Fixed:** {n}
- **Avg. Time per Fix:** {ms}ms

---

## Fixes by Type

### Unused Imports Removed ({count})

| File | Line | Import Removed |
|------|------|----------------|
| ProductService.java | 12 | java.util.ArrayList |
| OrderController.java | 8 | java.util.Map |
| ... | ... | ... |

### Magic Numbers Extracted ({count})

| File | Line | Before | After |
|------|------|--------|-------|
| CartService.java | 45 | `if (count > 3)` | `if (count > MAX_RETRIES)` |
| ... | ... | ... | ... |

### @Override Annotations Added ({count})

| File | Line | Method |
|------|------|--------|
| Service.java | 89 | process() |
| ... | ... | ... |

---

## Files Modified

### {file_path}

**Issues Fixed:** {n}
**Lines Changed:** +{added} -{removed}

#### Changes:
1. **Line 12:** Removed unused import `java.util.ArrayList`
2. **Line 45:** Extracted magic number to constant `MAX_RETRIES`
3. **Line 89:** Added @Override annotation

---

## Before/After Code Samples

### Example 1: Magic Number Extraction

**Before:**
```java
public void retry() {
    for (int i = 0; i < 3; i++) {
        // retry logic
    }
}
```

**After:**
```java
private static final int MAX_RETRIES = 3;

public void retry() {
    for (int i = 0; i < MAX_RETRIES; i++) {
        // retry logic
    }
}
```

---

## Verification

✅ All fixes compiled successfully
✅ No new issues introduced
✅ Backups created for all modified files

**Backups Location:** `claudeTasks/sonarqube-fix-{session}/backups/`
```

---

### MANUAL-REVIEW-NEEDED.md

```markdown
# Manual Review Needed

**Session:** {session_id}
**Total Issues:** {count}

---

## Critical Security Issues ({count})

### 1. SQL Injection Risk

**File:** `UserRepository.java:156`
**Severity:** Critical
**Rule:** `java:S2077`

**Issue:**
```java
String query = "SELECT * FROM users WHERE email = '" + email + "'";
```

**Recommendation:**
Use PreparedStatement with parameterized queries:
```java
String query = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, email);
```

**Priority:** P1 - Fix Immediately

---

### 2. Hardcoded Credentials

**File:** `application.properties:23`
**Severity:** Blocker
**Rule:** `java:S2068`

**Issue:**
```properties
database.password=admin123
```

**Recommendation:**
Use environment variables or secrets management:
```properties
database.password=${DB_PASSWORD}
```

**Priority:** P1 - Fix Immediately

---

## Complex Bugs Needing Review ({count})

### 1. Potential Null Pointer Dereference

**File:** `OrderService.java:234`
**Severity:** Major
**Rule:** `java:S2259`

**Issue:**
```java
User user = userRepository.findById(userId);
return user.getEmail();  // user might be null
```

**Recommendation:**
Add null check or use Optional:
```java
User user = userRepository.findById(userId);
if (user == null) {
    throw new UserNotFoundException(userId);
}
return user.getEmail();
```

**Priority:** P2 - Fix Soon

---

## Architectural Improvements ({count})

### 1. God Class - High Complexity

**File:** `ProductService.java`
**Severity:** Major
**Rule:** `java:S1448`

**Issue:**
- 850 lines of code
- Cyclomatic complexity: 45
- Handles too many responsibilities

**Recommendation:**
Split into smaller services:
- `ProductQueryService` - Read operations
- `ProductCommandService` - Write operations
- `ProductValidationService` - Validation logic
- `ProductNotificationService` - Events/notifications

**Priority:** P3 - Refactor When Possible

---

## Summary by Priority

| Priority | Count | Action Required |
|----------|-------|-----------------|
| P1 (Immediate) | {n} | Fix within 1 day |
| P2 (Soon) | {n} | Fix within 1 week |
| P3 (When Possible) | {n} | Schedule refactoring |
| P4 (Optional) | {n} | Nice to have |

---

## Next Steps

1. **Review P1 Issues** - Security vulnerabilities and blockers
2. **Plan P2 Fixes** - Schedule time for bug fixes
3. **Discuss P3 Items** - Team architecture review
4. **Consider P4** - Low priority improvements

**Assigned To:** {developer}
**Review By:** {date}
```

---

### BEFORE-AFTER-STATS.md

```markdown
# Before/After Statistics

**Session:** {session_id}
**Date:** {date}

---

## Issue Count Changes

| Category | Before | After | Fixed | Reduction |
|----------|--------|-------|-------|-----------|
| **Bugs** | {n} | {n} | {n} | {pct}% |
| Blocker | {n} | {n} | {n} | {pct}% |
| Critical | {n} | {n} | {n} | {pct}% |
| Major | {n} | {n} | {n} | {pct}% |
| Minor | {n} | {n} | {n} | {pct}% |
| **Vulnerabilities** | {n} | {n} | {n} | {pct}% |
| **Code Smells** | {n} | {n} | {n} | {pct}% |
| **Security Hotspots** | {n} | {n} | {n} | {pct}% |
| **TOTAL** | {n} | {n} | {n} | {pct}% |

---

## Technical Debt

| Metric | Before | After | Change |
|--------|--------|-------|--------|
| Debt Ratio | {pct}% | {pct}% | {delta}% |
| Debt (hours) | {h}h | {h}h | -{delta}h |
| Maintainability Rating | {A-E} | {A-E} | {change} |

---

## Code Quality Metrics

| Metric | Before | After | Change |
|--------|--------|-------|--------|
| Lines of Code | {n} | {n} | {delta} |
| Duplicated Lines | {n} ({pct}%) | {n} ({pct}%) | -{delta} |
| Complexity | {n} | {n} | {delta} |
| Comment Lines | {n} ({pct}%) | {n} ({pct}%) | +{delta} |

---

## Test Coverage

| Metric | Before | After | Change |
|--------|--------|-------|--------|
| Coverage | {pct}% | {pct}% | {delta}% |
| Lines Covered | {n} | {n} | +{delta} |
| Branches Covered | {pct}% | {pct}% | {delta}% |

---

## Quality Gate Status

**Before:** {PASS/FAIL}
**After:** {PASS/FAIL}

### Quality Gate Conditions

| Condition | Before | After | Status |
|-----------|--------|-------|--------|
| Coverage >= 80% | {pct}% | {pct}% | {PASS/FAIL} |
| Duplicated Lines < 3% | {pct}% | {pct}% | {PASS/FAIL} |
| Maintainability Rating = A | {rating} | {rating} | {PASS/FAIL} |
| Security Rating = A | {rating} | {rating} | {PASS/FAIL} |

---

## Visual Progress

### Issue Reduction
```
Before: ████████████████████ (100 issues)
After:  ████████             (40 issues)
Fixed:  ████████████         (60 issues - 60% reduction)
```

### Severity Distribution (Before)
```
Blocker:  ██         (10 issues)
Critical: ████       (20 issues)
Major:    ██████     (30 issues)
Minor:    ████████   (40 issues)
```

### Severity Distribution (After)
```
Blocker:  █          (5 issues)
Critical: ██         (10 issues)
Major:    ███        (15 issues)
Minor:    ████       (20 issues)
```

---

## Recommendations

Based on the improvements:

1. ✅ **Immediate Wins** - {n} issues auto-fixed
2. ⚠️ **Manual Review** - {n} issues need human review
3. 📈 **Progress** - {pct}% reduction in technical debt
4. 🎯 **Next Steps** - Focus on {category} issues

**Quality Trend:** {Improving/Stable/Declining}
```

---

## Advanced Features

### Feature 1: Incremental Fixing

**Progressive Mode:**
- Fix only files changed in current branch
- Compare against main branch
- Fix only new issues

```bash
# Get changed files
changed_files=$(git diff --name-only origin/main)

# Fetch issues only for changed files
for file in $changed_files; do
    # Fetch issues for this file
    # Apply fixes
done
```

---

### Feature 2: Dry Run Mode

**Simulate fixes without applying:**
```bash
# Add flag: --dry-run
# Show what would be fixed
# Don't modify files
# Generate preview report
```

---

### Feature 3: Custom Rule Configuration

**Allow user to specify:**
- Which rules to auto-fix
- Which rules to skip
- Custom fix patterns
- File exclusions

```yaml
# sonarqube-fix-config.yml
auto_fix:
  enabled:
    - java:S1128  # Unused imports
    - java:S109   # Magic numbers
  disabled:
    - java:S1192  # Don't extract string literals
  
exclude_files:
  - "*/generated/**"
  - "*/test/**"
```

---

### Feature 4: Git Integration

**Commit fixes automatically:**
```bash
# After fixes applied
git add .
git commit -m "fix: Apply SonarQube fixes (session: ${session_id})

Auto-fixed ${count} issues:
- ${n} unused imports removed
- ${n} magic numbers extracted
- ${n} @Override annotations added

Manual review needed: ${manual_count} issues
See: claudeTasks/sonarqube-fix-${session}/MANUAL-REVIEW-NEEDED.md"
```

---

### Feature 5: CI/CD Integration

**Run as part of pipeline:**
```yaml
# .github/workflows/sonarqube-fix.yml
name: SonarQube Auto-Fix

on:
  schedule:
    - cron: '0 2 * * 1'  # Weekly on Monday 2 AM

jobs:
  fix-issues:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run SonarQube Fixer
        run: |
          claude-code /sonarqube-fix --auto-commit
      - name: Create PR
        uses: peter-evans/create-pull-request@v3
        with:
          title: 'chore: Auto-fix SonarQube findings'
          body: 'Automated fixes from SonarQube analysis'
```

---

## Usage Examples

### Basic Usage

```bash
# Run skill
/sonarqube-fix

# With SonarQube server
/sonarqube-fix --server http://localhost:9000 --project my-project --token xxx

# Dry run (preview only)
/sonarqube-fix --dry-run

# Auto-commit fixes
/sonarqube-fix --commit

# Fix specific severity
/sonarqube-fix --severity BLOCKER,CRITICAL

# Fix specific file
/sonarqube-fix --file src/main/java/Service.java
```

### Conversational Usage

- **"fix sonarqube issues"** → Run full analysis and fixes
- **"fix blocker issues only"** → Fix P1 issues
- **"show me what would be fixed"** → Dry run mode
- **"fix and commit"** → Apply fixes and create commit
- **"analyze code quality"** → Fetch findings report only

---

## Implementation Checklist

- [ ] Fetch SonarQube findings (API or local)
- [ ] Parse and categorize issues
- [ ] Prioritize by severity and type
- [ ] Identify auto-fixable issues
- [ ] Backup original files
- [ ] Apply automated fixes
- [ ] Verify fixes (compile/type check)
- [ ] Generate summary reports
- [ ] Flag manual review items
- [ ] Create before/after statistics
- [ ] Optional: Create git commit
- [ ] Save all outputs to claudeTasks/

---

## Troubleshooting

### Issue: Cannot connect to SonarQube server

**Solution:**
- Verify SONAR_HOST_URL is correct
- Check SONAR_TOKEN has permissions
- Try curl to test connectivity
- Use local report files as fallback

---

### Issue: Fixes break compilation

**Solution:**
- Automatic rollback from backups
- Report which fix caused issue
- Mark rule as "needs manual review"
- Continue with remaining fixes

---

### Issue: Too many issues to fix

**Solution:**
- Use incremental mode (changed files only)
- Fix by severity (Blocker → Critical first)
- Set max fixes limit per run
- Schedule multiple fix sessions

---

## Best Practices

1. **Always backup** - Backup files before modifying
2. **Verify fixes** - Compile/test after changes
3. **Review before commit** - Don't auto-commit without review
4. **Prioritize security** - Fix vulnerabilities first
5. **Track progress** - Maintain fix history
6. **Educate team** - Share patterns to prevent recurrence
7. **Update rules** - Customize auto-fix rules over time

---

**Last Updated:** 2026-06-20
**Version:** 1.0
**Supported Languages:** Java, TypeScript, JavaScript, Python (extendable)
