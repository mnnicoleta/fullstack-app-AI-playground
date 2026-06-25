# Onboard-App Skill Update Plan

**Date:** 2026-06-25  
**Issue:** MCP Chrome DevTools has persistent timeout/connection issues  
**Solution:** Migrate to Playwright-based automation

---

## Problem Analysis

### Current Approach (Not Working)
- Uses MCP Chrome DevTools server tools (`mcp__chrome-devtools__*`)
- Requires MCP server to be running and stable
- Has persistent `Target.setDiscoverTargets timed out` errors
- Connection issues even after reconnection
- Unreliable for automated testing

### Root Causes
1. **MCP Server Instability:** Chrome DevTools MCP server has protocol timeout issues
2. **Environment Dependency:** Requires specific MCP configuration
3. **Connection Management:** MCP connection can drop unexpectedly
4. **No Fallback:** When MCP fails, there's no alternative automation method

---

## Solution: Playwright-Based Approach

### Why Playwright?

✅ **Proven Working:** Already successfully used in `user-journey-automation/`  
✅ **Reliable:** No dependency on MCP server  
✅ **Self-Contained:** Uses Node.js + Playwright NPM package  
✅ **Headless Mode:** Works in WSL without X server  
✅ **Full Control:** Direct browser automation via CDP  
✅ **Easy to Debug:** Clear error messages, good documentation

### Architecture Change

**Before (MCP-based):**
```
Claude → MCP Server → Chrome DevTools Protocol → Browser
```

**After (Playwright-based):**
```
Claude → Bash (runs Node.js script) → Playwright → Browser
```

---

## Implementation Strategy

### Option 1: Hybrid Approach (Recommended)

**Keep skill.md as high-level guide**, but update implementation method:

1. **Keep:** 
   - Scenario descriptions
   - Test case definitions
   - Expected results
   - Screenshot naming conventions

2. **Change:**
   - Replace MCP tool references with Playwright script execution
   - Update "Technical Notes" section
   - Add Playwright installation steps
   - Reference reusable automation scripts

3. **Add:**
   - `scripts/` directory with reusable Playwright automation scripts
   - Script templates for each scenario
   - Fallback to manual testing if Playwright fails

**Skill Workflow:**
```
1. Check services running (same)
2. Install Playwright if needed (NEW)
3. Generate/customize Playwright script for scenario (NEW)
4. Run script: `node scripts/{scenario}-automation.js` (NEW)
5. Collect screenshots and results (same)
6. Generate summary (same)
```

### Option 2: Pure Playwright Skill

**Completely rewrite skill.md** to be Playwright-first:

1. Remove all MCP tool references
2. Document Playwright API directly
3. Provide code snippets for each scenario
4. Users must have Node.js + Playwright installed

**Pros:** Clean, no MCP dependency  
**Cons:** More work to rewrite, loses skill abstraction

### Option 3: Dual-Mode Skill

Support **both MCP and Playwright**:

1. Try MCP tools first
2. If MCP fails, fall back to Playwright
3. Document both approaches in skill

**Pros:** Maximum compatibility  
**Cons:** Complex, maintenance burden

---

## Recommended Approach: Option 1 (Hybrid)

### Changes to `skill.md`

#### 1. Update "Workflow" Section

**Replace:**
```markdown
4. **Verify Chromium is available:**
   - Check if `/opt/google/chrome/chrome` exists
   - If not, install Chromium...
```

**With:**
```markdown
4. **Verify Playwright is installed:**
   ```bash
   # Check if Playwright is available
   if ! node -e "require('playwright')" 2>/dev/null; then
     echo "Installing Playwright..."
     npm install playwright
     npx playwright install chromium
   fi
   ```
```

#### 2. Update Test Execution Method

**Replace MCP tool calls with:**

```markdown
### Automation Execution

**Method:** Playwright-based Node.js scripts

**Available Scripts:**
- `scripts/basic-user-journey.js` - Scenario 1
- `scripts/cart-management.js` - Scenario 2
- `scripts/admin-workflow.js` - Scenario 4
- `scripts/i18n-testing.js` - I18n feature testing

**Usage:**
```bash
cd .claude/skills/onboard-app/scripts
node {scenario}.js [output-directory]
```

**Script Template:**
```javascript
const { chromium } = require('playwright');
// ... (see scripts/template.js for full example)
```
```

#### 3. Add "Playwright API Quick Reference"

Replace "Chrome DevTools MCP Tools" section with:

```markdown
### Playwright API Quick Reference

**Available APIs:**
- `browser = await chromium.launch()` - Open browser
- `page = await context.newPage()` - New page/tab
- `await page.goto(url)` - Navigate to URL
- `await page.screenshot({ path })` - Capture screenshot
- `await page.click(selector)` - Click element
- `await page.fill(selector, text)` - Fill form field
- `await page.waitForLoadState('networkidle')` - Wait for page load
- `await page.waitForSelector(selector)` - Wait for element
- `await page.locator(selector).textContent()` - Get text content

**Best Practices:**
- Use `headless: true` for WSL/server environments
- Add `await page.waitForTimeout(1000)` after navigation
- Use CSS selectors: `button:has-text("Login")`
- Full-page screenshots: `fullPage: true`
```

#### 4. Update "Implementation Guidelines"

Remove MCP-specific guidance, add Playwright patterns:

```markdown
### Playwright Patterns

**Login Flow:**
```javascript
await page.goto('http://172.22.208.1:4200/auth/login');
await page.fill('input[type="email"]', 'admin@onlineshop.com');
await page.fill('input[type="password"]', 'password');
await page.click('button[type="submit"]');
await page.waitForLoadState('networkidle');
```

**Screenshot with Description:**
```javascript
await page.screenshot({ 
  path: 'screenshots/01-login.png', 
  fullPage: true 
});
fs.writeFileSync(
  'screenshots/01-login.txt', 
  'Login page description...'
);
```

**Error Handling:**
```javascript
try {
  await page.click('button:has-text("Submit")');
} catch (e) {
  console.log('Button not found, trying alternative...');
  await page.click('button[type="submit"]');
}
```
```

---

## Migration Steps

### Step 1: Create Scripts Directory

```bash
mkdir -p .claude/skills/onboard-app/scripts
```

### Step 2: Create Script Templates

**File:** `scripts/template.js`

```javascript
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

// Configuration
const screenshotDir = process.argv[2] || 'screenshots';
const FRONTEND_URL = 'http://172.22.208.1:4200';

// Test credentials
const testUser = {
  email: 'admin@onlineshop.com',
  password: 'password'
};

// Helper functions
async function saveScreenshot(page, filename, description) {
  const imgPath = path.join(screenshotDir, filename);
  const txtPath = path.join(screenshotDir, filename.replace('.png', '.txt'));
  
  await page.screenshot({ path: imgPath, fullPage: true });
  fs.writeFileSync(txtPath, description);
  
  console.log(`✓ ${filename}`);
}

// Main automation
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  try {
    // Test steps here
    console.log('Starting automation...');
    
    await page.goto(FRONTEND_URL);
    await saveScreenshot(page, '01-homepage.png', 'Homepage loaded');
    
    // ... more steps ...
    
    console.log('✅ Automation complete!');
  } catch (error) {
    console.error('❌ Error:', error);
  } finally {
    await browser.close();
  }
})();
```

### Step 3: Create Scenario Scripts

Copy and customize the template for each scenario:

- `basic-user-journey.js` (from i18n-test-automation.js pattern)
- `cart-management.js`
- `admin-workflow.js`
- `i18n-testing.js` (already created)

### Step 4: Update skill.md

Apply changes from sections above:
1. Update Workflow section
2. Replace MCP references with Playwright
3. Add Playwright API reference
4. Update Implementation Guidelines

### Step 5: Test Each Script

Run each script to verify it works:

```bash
cd .claude/skills/onboard-app/scripts
node basic-user-journey.js
node cart-management.js
node admin-workflow.js
```

### Step 6: Update Documentation

- Update skill description (keep it high-level)
- Document Playwright requirement in README
- Add troubleshooting for Playwright issues

---

## File Structure After Update

```
.claude/skills/onboard-app/
├── skill.md                    # Updated (Playwright references)
├── README.md                   # Installation guide
├── scripts/
│   ├── template.js            # NEW: Script template
│   ├── basic-user-journey.js  # NEW: Scenario 1
│   ├── cart-management.js     # NEW: Scenario 2
│   ├── admin-workflow.js      # NEW: Scenario 4
│   ├── i18n-testing.js        # NEW: I18n tests
│   ├── run.sh                 # NEW: Wrapper script
│   └── package.json           # NEW: Dependencies
└── examples/
    ├── successful-run/        # Example screenshots
    └── error-handling/        # Error examples
```

---

## Testing Plan

After implementing changes:

1. **Unit Test:** Run each script individually
2. **Integration Test:** Run full onboard-app skill flow
3. **Edge Cases:** Test with services down, network issues
4. **Documentation:** Verify all examples work

---

## Rollback Plan

If Playwright approach has issues:

1. Keep old skill.md as `skill.md.mcp-backup`
2. Document why rollback was needed
3. Consider Option 3 (Dual-Mode) instead

---

## Benefits of This Approach

✅ **Reliable:** No MCP dependency, proven to work  
✅ **Maintainable:** Standard Node.js scripts  
✅ **Debuggable:** Clear error messages  
✅ **Reusable:** Scripts can be run independently  
✅ **Extensible:** Easy to add new scenarios  
✅ **Documented:** Playwright has excellent docs

---

## Next Steps

1. **Approve this plan** ✓ (waiting for user feedback)
2. **Create scripts directory**
3. **Port existing i18n script as template**
4. **Create scenario scripts**
5. **Update skill.md**
6. **Test thoroughly**
7. **Document changes**

---

**Estimated Time:** 2-3 hours for full migration  
**Risk:** Low (Playwright already proven working)  
**Impact:** High (makes onboard-app skill actually usable)

---

## Questions for User

1. **Approve this migration plan?**
2. **Should we keep MCP references as "deprecated" or remove completely?**
3. **Any specific scenarios you want prioritized?**
4. **Should scripts be in skill folder or in project root?**

---

**Status:** ⏳ Awaiting approval to proceed with implementation
