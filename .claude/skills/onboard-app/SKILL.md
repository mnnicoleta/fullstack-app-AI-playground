---
name: onboard-app
description: Interactive onboarding and testing for the fullstack e-commerce application. Use this skill when the user asks for onboarding, wants to see how the app works, needs a demo of the online shop, wants to test the full user flow, or requests specific test scenarios. This skill guides users through starting the application stack (UI, backend, database) and then automates complete user journeys using browser automation - registration, login, product browsing, adding items to cart, order creation, and various test scenarios. ALWAYS use this when users mention "onboard", "demo the app", "show me how it works", "test the user flow", "walk me through the online shop", or "test scenario".
---

# App Onboarding & Testing

This skill provides an interactive onboarding and testing experience for the fullstack e-commerce application by guiding users through starting the services and automating complete user journeys through the application.

## Overview

The skill supports multiple test scenarios:
1. **Basic User Journey** - Registration, login, browse, add to cart, checkout
2. **Cart Management** - Add, update quantities, remove items, clear cart
3. **Multiple Products** - Add various products from different categories
4. **Admin Workflow** - Admin login, create/edit/delete products
5. **Error Handling** - Test validation, out of stock, invalid inputs
6. **Edge Cases** - Empty cart checkout, large quantities, special characters

## Configuration

### Output Directory Structure

**IMPORTANT:** All test results, screenshots, and explanations MUST be stored in:

```
claudeTasks/onboarding-YYYYMMDD-HHMMSS/
├── screenshots/
│   ├── 01-step-name.png
│   ├── 01-step-name.txt
│   ├── 02-step-name.png
│   └── ...
├── SCENARIO-SUMMARY.md
└── TEST-RESULTS.md
```

**Directory Rules:**
- Base directory: `claudeTasks/` (create if doesn't exist)
- Session directory: `onboarding-YYYYMMDD-HHMMSS/` (timestamped)
- Screenshots subdirectory: `screenshots/`
- Always use this structure - never use project root or other locations

### Application URLs

**WSL Environment:**
- Frontend: `http://172.22.208.1:4200` (WSL networking)
- Backend API: `http://172.22.208.1:3000/api`

**Local Environment:**
- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:3000/api`

**Default:** Use WSL URLs (`172.22.208.1`) for WSL2 environments.

---

## Workflow

### Step 1: Check Services & Prepare

Before starting automation:

1. **Check if services are running:**
   ```bash
   curl -s -o /dev/null -w "%{http_code}" http://172.22.208.1:4200
   curl -s -o /dev/null -w "%{http_code}" http://172.22.208.1:3000/api/products
   docker ps --filter "name=postgres" --format "{{.Status}}"
   ```

2. **If services are not running, ask user:**
   > To begin testing, please start these services:
   >
   > 1. **Database**: `cd docker/development && docker-compose up -d`
   > 2. **Backend**: `cd onlineshopapi && mvn spring-boot:run -Dspring-boot.run.profiles=local`
   > 3. **Frontend**: `cd onlineshopui && npm start`
   >
   > The frontend will be available at http://localhost:4200 (http://172.22.208.1:4200 from WSL).
   >
   > Let me know when all services are running.

3. **Create output directory:**
   ```bash
   mkdir -p claudeTasks/onboarding-$(date +%Y%m%d-%H%M%S)/screenshots
   ```

4. **Verify Chromium is available:**
   - Check if `/opt/google/chrome/chrome` exists (symlink to Chromium)
   - If not, install Chromium: `sudo dnf install -y chromium`
   - Create symlink: `sudo mkdir -p /opt/google/chrome && sudo ln -sf /usr/bin/chromium-browser /opt/google/chrome/chrome`

### Step 2: Select Test Scenario

Ask the user which scenario to run (if not specified):

**Available Scenarios:**

1. **Basic User Journey (Default)**
   - Register → Login → Browse → Add to Cart → Checkout → View Orders

2. **Cart Management Testing**
   - Add items → Update quantities → Remove items → Clear cart

3. **Multiple Products Workflow**
   - Add products from different categories → Mixed quantities → Bulk checkout

4. **Admin Workflow**
   - Admin login → Create product → Edit product → Delete product

5. **Error Handling Tests**
   - Invalid login → Empty cart checkout → Form validation

6. **Performance & Edge Cases**
   - Large quantities → Special characters in forms → Rapid clicks

If user doesn't specify, run **Scenario 1: Basic User Journey**.

---

## Test Scenarios

### Scenario 1: Basic User Journey (Default)

**Goal:** Demonstrate complete e-commerce flow from registration to order completion.

**Test User Credentials:**
- Email: `testuser-{timestamp}@example.com` (e.g., `testuser-20260620223000@example.com`)
- First Name: `Test`
- Last Name: `User`
- Password: `testpassword123`

**Steps:**

#### 1.1: Navigate to Login Page
- Open browser: `mcp__chrome-devtools__new_page` → `http://172.22.208.1:4200/auth/login`
- Take snapshot to find elements
- Screenshot: `01-login-page.png`
- Explanation: `01-login-page.txt` - "Login page with email and password fields. Register link available for new users."

#### 1.2: Register New User
- Click "Register" link
- Fill form with `mcp__chrome-devtools__fill_form`:
  - Email: `testuser-{timestamp}@example.com`
  - First Name: `Test`
  - Last Name: `User`
  - Password: `testpassword123`
- Click "Create Account" button
- Screenshot: `02-registration-form.png`
- Explanation: `02-registration-form.txt` - "Registration form filled with new user credentials. Account created successfully."

#### 1.3: Login with New Account
- Wait for redirect to login page
- Fill login form:
  - Email: `testuser-{timestamp}@example.com`
  - Password: `testpassword123`
- Click "Sign In" button
- Wait for redirect to products page
- Screenshot: `03-login-success.png`
- Explanation: `03-login-success.txt` - "Successfully logged in. User email displayed in navigation bar. Redirected to product catalog."

#### 1.4: Browse Product Catalog
- Wait for products to load: `mcp__chrome-devtools__wait_for` → `["Products", "Browse our collection"]`
- Take snapshot to identify products
- Screenshot: `04-product-catalog.png`
- Explanation: `04-product-catalog.txt` - "Product catalog displaying {count} products across Electronics, Clothing, Home & Garden, and Sports categories. Each product shows image, name, price, and 'Add to Cart' button."

#### 1.5: View Product Details
- Click "View" button on first product (e.g., Wireless Headphones)
- Wait for product detail page to load
- Screenshot: `05-product-detail.png`
- Explanation: `05-product-detail.txt` - "Product detail page for {product_name} showing price ({price}), description, quantity selector, supplier information, and 'Add to Cart' button."

#### 1.6: Add Product to Cart (with Quantity)
- Increase quantity to 2 using "+" button
- Wait for total price to update
- Click "Add to Cart" button
- Screenshot: `06-add-to-cart.png`
- Explanation: `06-add-to-cart.txt` - "Added {quantity}x {product_name} to cart. Total: {total}. Cart badge updated in navigation."

#### 1.7: Add Additional Product from Catalog
- Click "Back to Products" or navigate to products
- Add another product directly from catalog (e.g., Yoga Mat)
- Screenshot: `07-additional-product.png`
- Explanation: `07-additional-product.txt` - "Added second product ({product_name}) to cart from catalog page. Cart now contains {total_items} items."

#### 1.8: View Shopping Cart
- Click "Cart" link in navigation
- Wait for cart page to load
- Screenshot: `08-cart-view.png`
- Explanation: `08-cart-view.txt` - "Shopping cart displaying {item_count} items. Shows product details, quantities, line totals, and order summary with subtotal of {subtotal}. Options to adjust quantities or remove items."

#### 1.9: Complete Checkout
- Click "Place Order" button
- Wait for shipping address form to appear
- Fill shipping address:
  - Country: `USA`
  - County/State: `California`
  - City: `San Francisco`
  - Street Address: `{random_number} Test Street`
- Click "Continue" button
- Wait for redirect to orders page
- Screenshot: `09-checkout-complete.png`
- Explanation: `09-checkout-complete.txt` - "Order created successfully with shipping address. Cart cleared. Redirected to orders page."

#### 1.10: View Order History
- Wait for orders page to load
- Take snapshot to find new order
- Screenshot: `10-orders-list.png`
- Explanation: `10-orders-list.txt` - "Orders page showing order history. New order ({order_id}) created at {timestamp} with {item_count} items totaling {total}."

#### 1.11: View Order Details
- Click "View Details" on the newly created order
- Wait for order details page to load
- Screenshot: `11-order-details.png`
- Explanation: `11-order-details.txt` - "Order details showing order ID, date, shipping address, itemized products with quantities and prices, and order total. Order fulfilled successfully."

---

### Scenario 2: Cart Management Testing

**Goal:** Test all cart operations - add, update, remove, clear.

**Test User:** Use existing user or create new one.

**Steps:**

#### 2.1: Login
- Login with existing credentials

#### 2.2: Add Multiple Products
- Add 3-4 different products with varying quantities
- Screenshot after each addition
- Verify cart badge updates

#### 2.3: Navigate to Cart
- Open cart page
- Screenshot: `cart-with-items.png`

#### 2.4: Update Quantities
- Increase quantity of one product
- Decrease quantity of another product
- Screenshot: `cart-quantity-updated.png`
- Verify subtotal updates correctly

#### 2.5: Remove Individual Item
- Click "Remove" button on one product
- Screenshot: `cart-item-removed.png`
- Verify item disappears and subtotal updates

#### 2.6: Clear Cart
- Click "Clear Cart" button
- Screenshot: `cart-cleared.png`
- Verify empty cart message appears

#### 2.7: Add Product Again
- Navigate to products
- Add one product to verify cart works after clearing
- Screenshot: `cart-restored.png`

---

### Scenario 3: Multiple Products Workflow

**Goal:** Test adding products from different categories and bulk checkout.

**Steps:**

#### 3.1: Login
- Login with test user

#### 3.2: Add Electronics Products
- Add 2x Wireless Headphones
- Add 1x Smart Watch
- Screenshot: `electronics-added.png`

#### 3.3: Add Clothing Products
- Add 3x Cotton T-Shirt
- Add 1x Denim Jeans
- Screenshot: `clothing-added.png`

#### 3.4: Add Sports Products
- Add 2x Yoga Mat
- Add 1x Running Shoes
- Screenshot: `sports-added.png`

#### 3.5: Review Cart
- Open cart
- Screenshot: `mixed-cart-view.png`
- Verify all products from different categories are present

#### 3.6: Place Large Order
- Complete checkout with mixed products
- Screenshot: `large-order-complete.png`

#### 3.7: Verify Order Details
- View order details
- Screenshot: `large-order-details.png`
- Verify all products and quantities are correct

---

### Scenario 4: Admin Workflow

**Goal:** Test admin product management features.

**Admin Credentials:**
- Email: `admin@onlineshop.com`
- Password: `password`

**Steps:**

#### 4.1: Admin Login
- Navigate to login page
- Login with admin credentials
- Screenshot: `admin-logged-in.png`
- Verify admin role visible

#### 4.2: Navigate to Product Creation
- Look for "Create Product" button or link (admin-only)
- Click to open product creation form
- Screenshot: `product-create-form.png`

#### 4.3: Create New Product
- Fill product form:
  - Name: `Test Product {timestamp}`
  - Description: `This is a test product created during automated testing`
  - Price: `99.99`
  - Weight: `1.5`
  - Category: Select "Electronics"
  - Image URL: `https://picsum.photos/seed/test/400/300`
- Submit form
- Screenshot: `product-created.png`

#### 4.4: Verify Product in Catalog
- Navigate to products page
- Scroll to find new product
- Screenshot: `new-product-in-catalog.png`

#### 4.5: Edit Product
- Click "Edit" button on the new product (admin-only button)
- Change price to `89.99`
- Update description
- Submit changes
- Screenshot: `product-edited.png`

#### 4.6: Delete Product
- Click "Delete" button on the test product
- Confirm deletion in modal/dialog
- Screenshot: `product-deleted.png`
- Verify product removed from catalog

---

### Scenario 5: Error Handling Tests

**Goal:** Test validation and error scenarios.

**Steps:**

#### 5.1: Invalid Login
- Navigate to login page
- Enter invalid credentials:
  - Email: `invalid@example.com`
  - Password: `wrongpassword`
- Submit form
- Screenshot: `invalid-login.png`
- Verify error message appears

#### 5.2: Registration Validation
- Navigate to register page
- Test empty form submission
- Screenshot: `registration-validation-empty.png`
- Test invalid email format
- Screenshot: `registration-validation-email.png`
- Test short password
- Screenshot: `registration-validation-password.png`

#### 5.3: Empty Cart Checkout
- Ensure cart is empty
- Try to access checkout
- Screenshot: `empty-cart-checkout.png`
- Verify "Place Order" button is disabled or shows error

#### 5.4: Product Form Validation (Admin)
- Login as admin
- Open product creation form
- Submit with missing required fields
- Screenshot: `product-validation.png`
- Test negative price
- Screenshot: `product-negative-price.png`

---

### Scenario 6: Performance & Edge Cases

**Goal:** Test application behavior under edge conditions.

**Steps:**

#### 6.1: Large Quantity Order
- Add product with quantity 99
- Screenshot: `large-quantity.png`
- Complete checkout
- Verify order processes correctly

#### 6.2: Special Characters in Forms
- Register user with special characters in name: `Test O'Brien`
- Enter address with special characters: `123 Test St. Apt #4B`
- Screenshot: `special-characters.png`

#### 6.3: Rapid Cart Operations
- Rapidly add same product multiple times
- Screenshot: `rapid-additions.png`
- Verify cart quantity aggregates correctly

#### 6.4: Browser Back/Forward
- Add items to cart
- Navigate through pages using browser back button
- Return to cart
- Screenshot: `browser-navigation.png`
- Verify cart state persists (localStorage)

#### 6.5: Refresh During Checkout
- Start checkout process
- Fill partial shipping form
- Refresh page
- Screenshot: `refresh-during-checkout.png`
- Verify cart data persists but form resets

---

## Implementation Guidelines

### Screenshot & Explanation Naming

Use descriptive, sequential naming:
- `01-step-description.png` / `01-step-description.txt`
- `02-step-description.png` / `02-step-description.txt`

**Naming Rules:**
- Use lowercase with hyphens
- Be specific: `product-detail-wireless-headphones.png` not `product.png`
- Include context: `cart-with-3-items.png` not `cart.png`

### Explanation File Format

Keep `.txt` files concise (1-3 sentences):

```
Product detail page for Wireless Headphones ($149.99). Shows quantity selector set to 2 units, calculated total of $299.98, supplier information (TechVision Electronics), and "Add to Cart" button. Product is in Electronics category with 0.25g weight.
```

### Error Handling

**If a step fails:**

1. **Take error screenshot:**
   ```
   Screenshot: `error-{step-name}.png`
   Explanation: `error-{step-name}.txt` - "Error occurred: {error_description}. Expected: {expected_behavior}. Actual: {actual_behavior}."
   ```

2. **Log error details:**
   - What step failed
   - What was expected
   - What actually happened
   - Error messages (if any)

3. **Decide whether to continue:**
   - **Critical failures** (login, navigation): Stop scenario and report
   - **Non-critical failures** (optional features): Log and continue
   - **Validation errors** (expected): Document and continue

4. **Report to user:**
   ```
   ⚠️ Step {number} failed: {step_name}
   Error: {error_description}
   Next steps: {recommendation}
   ```

### Dynamic Element Handling

**Always use `take_snapshot` first:**

```typescript
// 1. Take snapshot to see page structure
mcp__chrome-devtools__take_snapshot()

// 2. Identify elements by uid from snapshot
// uid=5_23 button "Add to Cart"

// 3. Interact with element
mcp__chrome-devtools__click({ uid: "5_23" })
```

**Wait for dynamic content:**

```typescript
// Wait for specific text to appear
mcp__chrome-devtools__wait_for({
  text: ["Products", "Order Details", "Shopping Cart"],
  timeout: 10000
})
```

**Handle navigation:**

```typescript
// After navigation, always wait for page to load
mcp__chrome-devtools__click({ uid: "link_uid" })
mcp__chrome-devtools__wait_for({ text: ["Expected Page Title"] })
mcp__chrome-devtools__take_snapshot() // Get new page structure
```

### Data Generation

**Timestamps for unique data:**
```javascript
const timestamp = Date.now() // or use: date +%Y%m%d%H%M%S
const email = `testuser-${timestamp}@example.com`
const streetNumber = Math.floor(Math.random() * 9000) + 1000
```

**Realistic test data:**
- **Names:** Test User, Jane Doe, John Smith
- **Addresses:** 123 Test Street, 456 Main Avenue
- **Cities:** San Francisco, New York, Los Angeles
- **Products:** Use actual products from catalog (Wireless Headphones, Yoga Mat, etc.)

---

## Post-Execution Tasks

### Step 3: Generate Summary Documents

After completing the test scenario, create these files in the session directory:

#### SCENARIO-SUMMARY.md

```markdown
# Test Scenario Summary

**Scenario:** {scenario_name}
**Date:** {date}
**Session ID:** {session_folder}
**Status:** ✅ Passed / ⚠️ Passed with warnings / ❌ Failed

## Test Results

### Steps Completed: {completed}/{total}

| Step | Status | Duration | Notes |
|------|--------|----------|-------|
| 01 - Login Page | ✅ Pass | 2s | Initial page loaded correctly |
| 02 - Registration | ✅ Pass | 3s | User created successfully |
| ... | ... | ... | ... |

## Summary Statistics

- **Total Steps:** {total}
- **Passed:** {passed}
- **Failed:** {failed}
- **Warnings:** {warnings}
- **Total Duration:** {duration}
- **Screenshots Captured:** {screenshot_count}

## Test User Created

- **Email:** {email}
- **Password:** {password}
- **Name:** {name}

## Orders Created

- **Order ID:** {order_id}
- **Total:** {total}
- **Items:** {item_count}
- **Status:** {status}

## Issues Found

{list any issues, errors, or unexpected behavior}

## Recommendations

{suggestions for improvements or next steps}
```

#### TEST-RESULTS.md

```markdown
# Detailed Test Results

## Environment

- **Frontend:** http://172.22.208.1:4200
- **Backend:** http://172.22.208.1:3000/api
- **Database:** PostgreSQL 18 (Docker)
- **Browser:** Chromium {version} (headless)

## Features Tested

- ✅ User Registration
- ✅ User Authentication (JWT)
- ✅ Product Catalog Browsing
- ✅ Product Detail View
- ✅ Shopping Cart Operations
- ✅ Order Creation
- ✅ Order History
- ✅ Order Details

## Performance Metrics

- **Average Page Load:** {avg_load_time}ms
- **Average API Response:** {avg_api_time}ms
- **Cart Operations:** {cart_ops_time}ms
- **Checkout Process:** {checkout_time}ms

## Screenshots

{table of screenshots with descriptions}

## API Calls Observed

| Endpoint | Method | Status | Response Time |
|----------|--------|--------|---------------|
| /auth/register | POST | 201 | 245ms |
| /auth/login | POST | 200 | 189ms |
| /products | GET | 200 | 123ms |
| ... | ... | ... | ... |

## Browser Console Logs

{any errors or warnings from console}

## Next Steps

{recommendations for follow-up tests or improvements}
```

### Step 4: Report to User

Provide a concise summary:

```markdown
## ✅ Test Scenario Complete!

**Scenario:** {scenario_name}
**Status:** {status}
**Duration:** {duration}

### Results Summary

- **Steps Completed:** {completed}/{total}
- **Screenshots:** {screenshot_count}
- **Test User:** {email}
- **Order Created:** {order_id} ({total})

### Files Created

All results saved to: `claudeTasks/{session_folder}/`

- **Screenshots:** `screenshots/` ({count} files)
- **Scenario Summary:** `SCENARIO-SUMMARY.md`
- **Test Results:** `TEST-RESULTS.md`

### Key Findings

{bullet list of main observations}

### Next Steps

{recommendations or offers for additional scenarios}
```

---

## Usage Examples

**User Request:** "onboard the app"
→ Run **Scenario 1: Basic User Journey**

**User Request:** "test cart management"
→ Run **Scenario 2: Cart Management Testing**

**User Request:** "test admin features"
→ Run **Scenario 4: Admin Workflow**

**User Request:** "test error handling"
→ Run **Scenario 5: Error Handling Tests**

**User Request:** "test everything"
→ Ask which scenarios to run, or run multiple scenarios sequentially

---

## Technical Notes

### Chrome DevTools MCP Tools

**Available tools:**
- `mcp__chrome-devtools__new_page` - Open browser to URL
- `mcp__chrome-devtools__take_snapshot` - Get page structure (a11y tree)
- `mcp__chrome-devtools__take_screenshot` - Capture visual screenshot
- `mcp__chrome-devtools__click` - Click element by uid
- `mcp__chrome-devtools__fill_form` - Fill multiple form fields at once (preferred)
- `mcp__chrome-devtools__fill` - Fill single form field
- `mcp__chrome-devtools__type_text` - Type text into focused input
- `mcp__chrome-devtools__wait_for` - Wait for text to appear
- `mcp__chrome-devtools__navigate_page` - Navigate forward/back/reload
- `mcp__chrome-devtools__close_page` - Close browser tab

**Best Practices:**
- Always `take_snapshot` before interacting with elements
- Use `fill_form` for multi-field forms (faster than individual fills)
- Use `wait_for` after navigation or dynamic content
- Set timeout to 30000ms for slow operations
- Handle errors gracefully with try/catch equivalent

### Browser Configuration

**Headless mode:**
Chromium runs in headless mode via wrapper script at `/opt/google/chrome/chrome`:

```bash
#!/bin/bash
exec /usr/bin/chromium-browser --headless=new --no-sandbox --disable-dev-shm-usage --disable-gpu "$@"
```

**WSL Networking:**
Use `172.22.208.1` to access localhost services from WSL2 container.

---

## Maintenance

**When to update this skill:**

1. **New features added** - Add test steps for new functionality
2. **UI changes** - Update element selectors and screenshots
3. **New scenarios needed** - Add test scenarios for new workflows
4. **API changes** - Update endpoint expectations
5. **Bug fixes** - Add regression test scenarios

**Version tracking:**
Document changes in skill commit messages with test scenario coverage.

---

**Last Updated:** 2026-06-20
**Version:** 2.0
**Scenarios:** 6
