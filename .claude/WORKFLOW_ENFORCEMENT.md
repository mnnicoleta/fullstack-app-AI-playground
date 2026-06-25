# Development Workflow Enforcement Summary

This document summarizes the workflow enforcement mechanisms implemented for this project.

## Overview

The project now enforces a mandatory development workflow for all code changes. This ensures quality, maintainability, and reduces technical debt.

## Enforcement Mechanisms

### 1. CLAUDE.md Instructions (Permanent)

**Location**: `/CLAUDE.md` - Section 1.1

**What it does**:
- Documents the BLOCKING workflow requirements
- Serves as the authoritative source of truth
- Always loaded into Claude's context
- Visible to all team members

**Workflow Steps**:
1. **Planning Phase**: Create plan with `EnterPlanMode`, get approval
2. **Implementation Phase**: Make changes following conventions
3. **Testing Phase**: Write tests (80%+ coverage), run tests
4. **Code Quality Phase**: Run `/sonarqube-fix`, fix linting
5. **Verification Phase**: Re-run tests, manual verification
6. **Commit Phase**: Prepare conventional commit message

### 2. Custom `/dev-workflow` Skill (Automation)

**Location**: `.claude/skills/dev-workflow/`

**What it does**:
- Automates all workflow steps
- Provides clear phase-by-phase progress
- Blocks on failures (tests, SonarQube, linting)
- Ensures no shortcuts are taken

**Usage**:
```bash
/dev-workflow "Add new feature X"
```

**Features**:
- Enforces plan approval before implementation
- Generates tests automatically
- Runs SonarQube analysis and fixes
- Verifies all tests pass
- Prepares conventional commit message

### 3. Git Hooks (In Progress - Manual Setup)

**Note**: Claude Code's hook system doesn't support traditional git hooks. Alternative: Use native git hooks.

**To configure native git hooks** (manual step):

1. Create `.git/hooks/pre-commit`:
```bash
#!/bin/bash

echo "🔍 Running pre-commit checks..."

# Run frontend tests
echo "📦 Testing frontend..."
cd onlineshopui
npm test -- --watch=false --code-coverage=false || exit 1
cd ..

# Run backend tests
echo "☕ Testing backend..."
cd onlineshopapi
mvn test -q || exit 1
cd ..

# Run frontend linting
echo "🔍 Linting frontend..."
cd onlineshopui
npm run lint || exit 1
cd ..

echo "✅ All pre-commit checks passed!"
```

2. Make executable:
```bash
chmod +x .git/hooks/pre-commit
```

### 4. Claude Code Hooks (Reminders)

**Location**: `.claude/settings.local.json`

**What they do**:
- Display workflow reminders when user submits prompts
- Remind about testing/quality after Edit/Write operations
- Non-blocking (just reminders, not enforcement)

**Configured Hooks**:
- `UserPromptSubmit`: Reminds about workflow on every prompt
- `PostToolUse(Edit)`: Reminds to test after editing files
- `PostToolUse(Write)`: Reminds to test after creating files

### 5. Session Settings (Per-session)

You can reinforce workflow at the start of each session:

```
"For this session, always:
1. Create a plan before implementing
2. Write tests for all changes
3. Run /sonarqube-fix after changes
4. Verify tests pass before commit"
```

## Workflow Compliance

### MUST Follow Workflow For:
- ✅ New features
- ✅ Bug fixes
- ✅ Refactoring with logic changes
- ✅ API endpoint changes
- ✅ Database schema changes
- ✅ UI component changes

### Can Skip Workflow For:
- ⚠️ Documentation-only changes (README, comments)
- ⚠️ Config-only changes (no logic)
- ⚠️ Emergency hotfixes (explicit permission)

## Quality Gates

### BLOCKING Issues (Must Fix):
- ❌ Test failures
- ❌ SonarQube blocker/critical/major issues
- ❌ Linting errors
- ❌ Coverage below 80% for new code

### SOFT Issues (Should Fix):
- ⚠️ SonarQube minor issues
- ⚠️ Code style inconsistencies

## Testing Requirements

### Frontend (Angular)
- **Framework**: Jasmine/Karma
- **Files**: `*.spec.ts` alongside source
- **Coverage**: 80%+ for new code
- **Run**: `cd onlineshopui && npm test`

### Backend (Spring Boot)
- **Framework**: JUnit 5 + TestContainers
- **Location**: `/onlineshopapi/src/test/java/`
- **Coverage**: 80%+ for new code
- **Run**: `cd onlineshopapi && mvn test`

## Code Quality Requirements

### Frontend
- **Linter**: ESLint with angular-eslint
- **Run**: `npm run lint`
- **Formatter**: Prettier (configured in package.json)

### Backend
- **Static Analysis**: SonarQube via `/sonarqube-fix` skill
- **Build Validation**: Maven (automatic during `mvn test`)

## Commit Message Format

**Format**: `type(scope): description`

**Valid Types**:
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring
- `test`: Add or update tests
- `chore`: Maintenance tasks
- `docs`: Documentation changes

**Examples**:
```
feat(products): add supplier support to product catalog
fix(auth): resolve JWT token expiration issue
test(orders): add integration tests for order processing
```

## Workflow Automation

### Using `/dev-workflow` Skill

**Best Practice**: Use the skill for ALL non-trivial changes.

**Example Session**:
```
User: "Use dev workflow to add supplier field to products"

Claude:
1. ✓ PLAN: Created implementation plan, awaiting approval
2. ✓ IMPLEMENT: Added Supplier entity, updated Product
3. ✓ TEST: Generated tests, 89% coverage, 42/42 passed
4. ✓ QUALITY: SonarQube 0 issues, linting passed
5. ✓ VERIFY: All tests pass, manual check complete
6. ✓ COMMIT PREP: Ready to commit

Commit Message:
feat(products): add supplier support to product catalog
```

### Manual Workflow (Without Skill)

If you choose not to use the skill, manually follow these steps:

1. **Plan**: Outline your approach, identify affected files
2. **Implement**: Make changes following project conventions
3. **Test**: Write and run tests
4. **Quality**: Run `/sonarqube-fix` and linters
5. **Verify**: Ensure all tests pass
6. **Commit**: Create conventional commit

## Troubleshooting

### "Tests are failing"
1. Check test output for specific failures
2. Fix failing tests before proceeding
3. Re-run tests to confirm fixes

### "SonarQube issues found"
1. Run `/sonarqube-fix` skill
2. Review and apply suggested fixes
3. Re-run to ensure issues resolved

### "Linting errors"
1. Run `npm run lint` (frontend)
2. Fix reported errors
3. Consider using auto-fix: `npm run lint -- --fix`

### "Coverage below 80%"
1. Identify untested code paths
2. Add tests for missing scenarios
3. Re-run tests with coverage report

## Benefits

### For Development:
- ✅ Catches bugs early (before commit)
- ✅ Maintains consistent code quality
- ✅ Reduces technical debt
- ✅ Improves test coverage
- ✅ Faster code reviews

### For Team:
- ✅ Consistent development process
- ✅ Predictable code quality
- ✅ Easier onboarding
- ✅ Reduced merge conflicts
- ✅ Better documentation

### For Project:
- ✅ Lower maintenance costs
- ✅ Fewer production bugs
- ✅ Easier refactoring
- ✅ Better scalability
- ✅ Improved reliability

## Next Steps

1. ✅ CLAUDE.md updated with workflow requirements
2. ✅ `/dev-workflow` skill created and available
3. ✅ Claude Code hooks configured (reminders)
4. ⚠️ TODO: Configure native git hooks (manual step)
5. ⚠️ TODO: Team training on workflow usage

## Resources

- **CLAUDE.md**: Section 1.1 - Development Workflow
- **Dev Workflow Skill**: `.claude/skills/dev-workflow/`
- **SonarQube Fix Skill**: `.claude/skills/sonarqube-fix/`
- **Hooks Config**: `.claude/settings.local.json`

## Questions?

- Review CLAUDE.md section 1.1 for detailed workflow steps
- Try `/dev-workflow` skill for automated enforcement
- Ask Claude for guidance: "Explain the dev workflow"

---

**Remember**: Quality code today prevents production fires tomorrow. Follow the workflow! 🚀
