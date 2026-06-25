---
name: dev-workflow
description: Enforced development workflow - plan, implement, test, fix quality issues, verify (BLOCKS shortcuts)
version: 1.0.0
skill_type: workflow_automation
requires:
  - sonarqube-fix
  - verify (optional)
whenToUse: |
  Trigger when user wants to implement a feature or fix using the complete development workflow.
  This skill ENFORCES the mandatory workflow from CLAUDE.md section 1.1.
  
  Trigger on:
  - "use dev workflow"
  - "implement X with workflow"
  - "full development workflow"
  - "add feature following workflow"
  
  This is a BLOCKING workflow - skipping steps is not allowed except for docs/config-only changes.
---

# Development Workflow Automation

This skill automates and enforces the mandatory development workflow defined in CLAUDE.md section 1.1.

## Instructions for Claude

When this skill is invoked, you MUST follow these steps in order. DO NOT skip steps.

### Step 1: Planning Phase (REQUIRED)
1. Use `EnterPlanMode` tool to create a structured plan
2. Plan must include:
   - Problem statement and goals
   - List of affected files (frontend + backend)
   - Implementation approach
   - Test strategy (what tests to write)
   - Potential SonarQube issues to watch for
3. Present plan to user
4. Wait for user approval
5. Use `ExitPlanMode` tool after approval

### Step 2: Implementation Phase (REQUIRED)
1. Make code changes according to approved plan
2. Follow project conventions from CLAUDE.md:
   - Frontend: `/onlineshopui/src/app/features/`
   - Backend: `/onlineshopapi/src/main/java/`
3. Use proper naming conventions
4. Document complex logic with comments

### Step 3: Testing Phase (REQUIRED - CRITICAL)

**For Frontend Changes:**
1. Generate `*.spec.ts` test files for:
   - New components (component.spec.ts)
   - New services (service.spec.ts)
   - New guards (guard.spec.ts)
   - New interceptors (interceptor.spec.ts)
2. Use Jasmine/Karma framework
3. Write tests for:
   - Component creation
   - Service methods
   - User interactions
   - Error cases
4. Run tests: `cd onlineshopui && npm test`
5. Check coverage (minimum 80% for new code)
6. Fix any failing tests before proceeding

**For Backend Changes:**
1. Generate JUnit test files in `/onlineshopapi/src/test/java/`:
   - Unit tests for services
   - Integration tests with TestContainers
   - Controller tests with MockMvc
2. Write tests for:
   - Service methods (happy path + error cases)
   - Repository queries
   - Controller endpoints
   - DTO mapping
3. Run tests: `cd onlineshopapi && mvn test`
4. Check coverage (minimum 80% for new code)
5. Fix any failing tests before proceeding

**BLOCK if tests fail - do not proceed to next step**

### Step 4: Code Quality Phase (REQUIRED)
1. Run the `/sonarqube-fix` skill
2. Review SonarQube findings:
   - BLOCKER: Must fix (blocks commit)
   - CRITICAL: Must fix (blocks commit)
   - MAJOR: Must fix (blocks commit)
   - MINOR: Should fix
3. Fix all issues found
4. Run linters:
   - Frontend: `cd onlineshopui && npm run lint`
   - Backend: Maven validation during `mvn test`
5. Fix all linting errors
6. Re-run tests to ensure fixes didn't break anything

**BLOCK if blocker/critical/major issues remain - do not proceed**

### Step 5: Verification Phase (REQUIRED)
1. Run full test suite again:
   - Frontend: `npm test`
   - Backend: `mvn test`
2. Verify all tests pass
3. If `/verify` skill is available, use it to test in running application
4. Check for regressions in existing features
5. Smoke test main user flows:
   - Frontend: Login, navigate, key features
   - Backend: API endpoints via Swagger

**BLOCK if verification fails - fix issues before proceeding**

### Step 6: Commit Preparation (FINAL)
1. Run `git status` to show changed files
2. Run `git diff` to show changes
3. Generate conventional commit message:
   - Format: `type(scope): description`
   - Types: `feat`, `fix`, `refactor`, `test`, `chore`, `docs`
   - Example: `feat(products): add supplier support to product catalog`
   - Include bullet points for key changes
4. Remind user that pre-commit hook will:
   - Automatically run tests
   - Block commit if tests fail
   - Enforce commit message format
5. Ask user: "Ready to commit?"

## Output Format

After completing each phase, output:

```
✓ PHASE COMPLETED: [Phase Name]
  
  Changes Made:
  - [file.ts]: [brief description]
  - [file.java]: [brief description]
  
  Tests Generated:
  - [test-file.spec.ts]: [X tests, Y% coverage]
  - [TestFile.java]: [X tests, Y% coverage]
  
  Quality Checks:
  - Linter: PASSED ✓ / FAILED ✗
  - SonarQube: [X issues fixed]
  - Test Coverage: [Y%]
  
  Next Step: [Next phase name]
```

## Error Handling Rules

**If ANY step fails:**
1. **STOP IMMEDIATELY** - do not proceed to next step
2. Report the failure clearly with error messages
3. Provide actionable suggestions to fix
4. Wait for user to fix or provide guidance
5. Re-run the failed step after fixes
6. Only proceed when step succeeds

**Blocking Conditions:**
- Tests fail → STOP, fix tests
- SonarQube blocker/critical/major issues → STOP, fix issues
- Linting errors → STOP, fix errors
- Verification fails → STOP, investigate
- User has not approved plan → STOP, wait for approval

## Skip Conditions (EXCEPTIONAL - RARE)

You may skip workflow steps ONLY for:
1. **Documentation-only changes**: README, comments, markdown files
2. **Config-only changes**: application.yml, package.json (no logic changes)
3. **Emergency hotfixes**: Only with explicit user permission

For ALL other changes: **ENFORCE THE FULL WORKFLOW**

## Success Criteria Checklist

Before declaring workflow complete, verify:
- [ ] Plan created and user approved
- [ ] Code implemented following project conventions
- [ ] Tests written for all new/changed code
- [ ] All tests passing (frontend + backend)
- [ ] Test coverage ≥ 80% for new code
- [ ] SonarQube issues fixed (all blocker/critical/major)
- [ ] Linting passed (no errors)
- [ ] Full verification completed
- [ ] No regressions detected
- [ ] Commit message prepared (conventional format)
- [ ] User ready to commit

## Example Execution

```
User: "Use dev workflow to add supplier field to products"

Claude:
---
✓ PHASE 1: Planning
  
  Creating implementation plan...
  [Enters plan mode, creates structured plan]
  
  Plan Summary:
  - Add Supplier entity (backend)
  - Add SupplierRepository
  - Update Product entity with supplier FK
  - Create DTOs for supplier
  - Update ProductController
  - Generate comprehensive tests
  
  Awaiting your approval to proceed...

User: "Approved"

Claude:
---
✓ PHASE 2: Implementation
  
  Changes Made:
  - Supplier.java: New entity with id, name, contact fields
  - SupplierRepository.java: JPA repository for CRUD
  - Product.java: Added @ManyToOne supplier field
  - ProductResponseDto.java: Added SupplierDto
  - ProductController.java: Updated to include supplier
  
  Next Step: Testing Phase

---
✓ PHASE 3: Testing
  
  Tests Generated:
  - SupplierRepositoryTest.java: 8 tests, 95% coverage
  - ProductServiceTest.java: Updated, 15 tests, 87% coverage
  - ProductControllerTest.java: Updated, 12 tests, 92% coverage
  
  Test Results:
  - Backend: 42/42 passed ✓
  - Overall coverage: 89%
  
  Next Step: Code Quality Phase

---
✓ PHASE 4: Code Quality
  
  Running /sonarqube-fix...
  
  Quality Checks:
  - Linter: PASSED ✓
  - SonarQube: 3 code smells fixed
    - Removed unused import
    - Simplified boolean expression
    - Added @Override annotation
  - No blocker/critical issues
  
  Next Step: Verification Phase

---
✓ PHASE 5: Verification
  
  Full Test Suite:
  - Frontend: Not changed, skipped
  - Backend: 42/42 passed ✓
  
  Manual Verification:
  - Started backend with mvn spring-boot:run
  - Tested POST /api/products with supplier
  - Tested GET /api/products includes supplier
  - No regressions in existing endpoints
  
  Next Step: Commit Preparation

---
✓ PHASE 6: Commit Preparation
  
  Changed Files:
  - onlineshopapi/src/main/java/.../model/Supplier.java (new)
  - onlineshopapi/src/main/java/.../repository/SupplierRepository.java (new)
  - onlineshopapi/src/main/java/.../model/Product.java (modified)
  - onlineshopapi/src/test/java/.../SupplierRepositoryTest.java (new)
  
  Commit Message:
  ```
  feat(products): add supplier support to product catalog
  
  - Add Supplier entity with name and contact fields
  - Link products to suppliers via many-to-one relationship
  - Update product DTOs to include supplier information
  - Add comprehensive test coverage (89%)
  - Fix 3 SonarQube code smells
  ```
  
  Note: Pre-commit hook will run tests automatically and block if they fail.
  
  Ready to commit?
```

## Technical Reference

**Frontend Test Template (Jasmine/Karma)**:
```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ComponentName } from './component-name.component';

describe('ComponentName', () => {
  let component: ComponentName;
  let fixture: ComponentFixture<ComponentName>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentName]
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentName);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Add more tests for methods, interactions, etc.
});
```

**Backend Test Template (JUnit + TestContainers)**:
```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ServiceNameTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private ServiceName service;

    @Test
    void shouldPerformOperation() {
        // Arrange
        var input = createTestData();
        
        // Act
        var result = service.performOperation(input);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSomeField()).isEqualTo("expected");
    }
}
```

## Integration Points

This skill integrates with:
- **EnterPlanMode** tool: For creating plans
- **ExitPlanMode** tool: For finalizing plans
- **/sonarqube-fix** skill: For code quality analysis
- **/verify** skill: For application verification (optional)
- **Git hooks**: Pre-commit validation (configured separately)

## Notes

- This workflow is MANDATED by CLAUDE.md section 1.1
- Skipping steps is not allowed except for explicit exceptions
- All tests must pass before proceeding
- SonarQube blocker/critical/major issues block progression
- Pre-commit hook provides final safety net
- Conventional commit format is enforced
