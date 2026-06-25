# Online Shop UI - Angular Frontend Documentation

## 1. Project Overview

**Online Shop UI** is a modern Angular 21.2 single-page application (SPA) for an e-commerce platform. Built with standalone components, signal-based reactive state management, and Tailwind CSS v4 styling, it provides a responsive shopping experience with dark mode support and offline development capabilities via mock APIs.

**Key Features**:
- Signal-based reactive state management (no NgRx)
- Lazy-loaded feature modules for optimal performance
- JWT authentication with auto-token injection
- **Multi-language support (i18n)** - English and German with runtime switching
- Mock API mode for offline development and testing
- Dark mode with localStorage persistence
- Client-side shopping cart with localStorage
- Role-based UI rendering (ADMIN vs CUSTOMER)
- Tailwind CSS v4 with custom theming

**Technology Stack**:
- **Framework**: Angular 21.2.0 (standalone components)
- **Language**: TypeScript 5.9.2 with strict mode
- **State Management**: Angular Signals with RxJS
- **Styling**: Tailwind CSS 4.1.12, PostCSS
- **Icons**: lucide-angular 0.577.0
- **HTTP**: Angular HttpClient with interceptors
- **Testing**: Karma 6.4.4 with Jasmine 6.3.0
- **Linting**: ESLint 10.0.2 with angular-eslint
- **Formatting**: Prettier 3.8.1
- **Package Manager**: npm 11.6.1
- **Node Version**: 24+

---

## 2. Quick Command Reference

| Command | Purpose |
|---------|---------|
| `npm start` | Dev server with backend (http://localhost:4200) |
| `npm start:mock` | Dev server without backend (uses mock API) |
| `npm test` | Run Karma/Jasmine tests in watch mode |
| `npm run lint` | Run ESLint |
| `npm run format` | Format code with Prettier |
| `npm run build` | Production build |
| `npm run watch` | Watch mode for development |

**Mock Users** (for mock mode):
- Admin: `admin@example.com` / `admin123`
- User: `user@example.com` / `user123`
- Jane: `jane@example.com` / `jane123`

**Real Backend Users** (when backend is running):
- Admin: `admin@onlineshop.com` / `password`
- Customer: `john.doe@email.com` / `password`
- Customer: `jane.smith@email.com` / `password`

---

## 3. Internationalization (i18n)

### 3.1 Overview

The application supports **multiple languages** with a runtime language switcher. Currently supported:
- **English** (en) - Default
- **German** (de) - Deutsch

Users can switch languages via the navbar dropdown. The choice is persisted in localStorage and all UI text updates immediately.

### 3.2 Architecture

**Pattern**: Custom signal-based implementation using Angular signals for reactivity.

**Why not ngx-translate or @angular/localize?**
- Smaller bundle size (~5KB vs 50KB+)
- Native signal integration (no RxJS observables needed)
- Full TypeScript type safety for translation keys
- Perfect alignment with existing signal-based services
- SSR compatible with proper guards

### 3.3 Core Components

**I18nService** (`/core/services/i18n.service.ts`):
- Signal-based state management
- Methods: `setLanguage()`, `translate()`, `t()`
- localStorage persistence (key: `app_language`)
- Browser language detection with fallback to English
- SSR-safe with `typeof window === 'undefined'` guards

**TranslatePipe** (`/core/pipes/translate.pipe.ts`):
- Template usage: `{{ 'nav.products' | translate }}`
- With parameters: `{{ 'cart.itemCount' | translate:{ count: 5 } }}`
- Marked as `pure: false` for signal reactivity

**Translation Files** (`/core/config/i18n/`):
- `translations.en.ts` - English translations
- `translations.de.ts` - German translations
- Structured object with nested keys (common, nav, auth, products, cart, orders, validation, notifications)

**Type Definitions** (`/core/types/i18n.types.ts`):
- `SupportedLanguage`: `'en' | 'de'`
- `TranslationsMap`: Interface defining translation structure
- `TranslationKey`: Type-safe string literal union of all translation keys
- `LANGUAGE_METADATA`: Language metadata with flags and native names

### 3.4 Usage in Components

**Import the pipe**:
```typescript
import { TranslatePipe } from '../../../core/pipes/translate.pipe';

@Component({
  imports: [TranslatePipe, ...],
  ...
})
```

**Use in templates**:
```html
<!-- Simple translation -->
<h1>{{ 'products.title' | translate }}</h1>

<!-- With parameters -->
<p>{{ 'cart.itemCount' | translate:{ count: items().length } }}</p>

<!-- In attributes -->
<button [attr.aria-label]="'nav.language' | translate">
```

**Use in TypeScript code**:
```typescript
export class MyComponent {
  private readonly i18n = inject(I18nService);
  
  showNotification(): void {
    this.notifications.notifySuccess({
      title: this.i18n.translate('notifications.success'),
      message: ''
    });
  }
}
```

**Reactive computed signal**:
```typescript
readonly translatedTitle = this.i18n.t('products.title');

// In template
<h1>{{ translatedTitle() }}</h1>
```

### 3.5 Translation File Structure

```typescript
export const EN_TRANSLATIONS: TranslationsMap = {
  common: {
    loading: 'Loading...',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    ...
  },
  nav: {
    products: 'Products',
    orders: 'Orders',
    cart: 'Cart',
    login: 'Login',
    ...
  },
  validation: {
    required: 'This field is required',
    minLength: 'Minimum length is {{length}} characters',
    email: 'Please enter a valid email address',
    ...
  },
  ...
};
```

**Parameter interpolation**: Use `{{paramName}}` syntax in translations.

### 3.6 Adding a New Language

1. **Create translation file**: `/core/config/i18n/translations.{code}.ts`
   ```typescript
   import { TranslationsMap } from '../../types/i18n.types';
   
   export const FR_TRANSLATIONS: TranslationsMap = {
     common: { ... },
     nav: { ... },
     // Copy structure from EN_TRANSLATIONS and translate
   };
   ```

2. **Update types**: Add language to `SupportedLanguage` in `/core/types/i18n.types.ts`
   ```typescript
   export type SupportedLanguage = 'en' | 'de' | 'fr';
   export const SUPPORTED_LANGUAGES: SupportedLanguage[] = ['en', 'de', 'fr'];
   ```

3. **Add metadata**: Update `LANGUAGE_METADATA` with flag and native name
   ```typescript
   export const LANGUAGE_METADATA: Record<SupportedLanguage, LanguageMetadata> = {
     en: { code: 'en', flag: '🇬🇧', nativeName: 'English' },
     de: { code: 'de', flag: '🇩🇪', nativeName: 'Deutsch' },
     fr: { code: 'fr', flag: '🇫🇷', nativeName: 'Français' },
   };
   ```

4. **Import in service**: Add case in `loadTranslations()` method
   ```typescript
   case 'fr':
     return FR_TRANSLATIONS;
   ```

5. **Test**: Switch language in navbar dropdown and verify all text updates

### 3.7 Accessibility

The language switcher has full accessibility support:
- **ARIA attributes**: `aria-expanded`, `aria-haspopup`, `role="menu"`, `role="menuitem"`, `aria-current`
- **Keyboard navigation**: Escape key closes dropdown
- **Click outside**: Dropdown closes when clicking outside
- **Screen reader friendly**: Flags marked with `aria-hidden="true"`

### 3.8 Performance

- **Signal-based reactivity**: Language changes trigger updates only where needed
- **OnPush components**: View components use `ChangeDetectionStrategy.OnPush` to minimize change detection cycles
- **Static imports**: All languages loaded upfront (small overhead, ~5KB per language)
- **No RxJS**: Pure signal-based, no observables needed

### 3.9 SSR Compatibility

The I18nService is SSR-safe:
- Guards against `window` and `localStorage` access
- Falls back to default language during SSR
- No runtime errors in server-side rendering

### 3.10 Layout Considerations

German text is ~20-30% longer than English. The UI uses flexible layouts to accommodate this:
- **Navbar**: Uses `flex` with `gap`, no fixed widths
- **Buttons**: Use `flex-1`, `w-full`, or `min-w-*` instead of fixed `w-*`
- **Forms**: Inputs use `w-full`, labels are short
- **Cards**: Product names use `break-words`, descriptions use `line-clamp-2`

All layouts tested at mobile width (375px) with German active.

### 3.11 localStorage Keys

| Key | Description | Values |
|-----|-------------|--------|
| `app_language` | User's selected language | `'en'`, `'de'` |

### 3.12 Troubleshooting

**Translations not updating after language change:**
- Ensure component imports `TranslatePipe`
- Check that `ChangeDetectionStrategy.OnPush` components trigger change detection
- Verify translation key exists in both `EN_TRANSLATIONS` and `DE_TRANSLATIONS`

**Missing translation warning in console:**
- Add the missing key to translation files
- Service falls back to English, then returns the key itself

**localStorage errors:**
- Service handles errors gracefully with try-catch
- Falls back to browser language detection or default (English)

**SSR errors:**
- Check that `typeof window === 'undefined'` guards are in place
- Service should not crash during server-side rendering

---

## 4. Architecture

### 3.1 Standalone Component Architecture

Angular 21 uses **standalone components** (no NgModules):

```
┌──────────────────────────────────────┐
│  main.ts (bootstrapApplication)      │
│  └─> app.config.ts (providers)       │
│      └─> app.component.ts            │
│          └─> <router-outlet>         │
└──────────────────────────────────────┘
           │
           ├─> /auth (lazy-loaded)
           ├─> /products (lazy-loaded)
           ├─> /cart (lazy-loaded)
           └─> /orders (lazy-loaded)
```

**No NgModules**: All components are standalone with explicit imports.

### 3.2 Signal-Based State Management

**Pattern**: Services use Angular signals for reactive state.

```typescript
// Example: CartService
export class CartService {
    // Private mutable signal
    private readonly _items = signal<CartItem[]>([]);
    
    // Public readonly signal
    readonly items = this._items.asReadonly();
    
    // Computed signal (derived state)
    readonly totalItems = computed(() =>
        this._items().reduce((sum, item) => sum + item.quantity, 0)
    );
    
    // Update signal immutably
    addItem(productId: string, quantity: number): void {
        this._items.update(items => [
            ...items,
            { productId, quantity }
        ]);
    }
}
```

**Benefits**:
- Automatic change detection
- No manual subscriptions needed
- Type-safe reactive state
- Computed values update automatically

### 3.3 Feature Module Organization

Each feature is self-contained:

```
features/{feature}/
├── components/
│   ├── pages/          # Smart components (OnInit, inject services)
│   └── views/          # Dumb components (input/output signals)
├── services/           # State management services
├── types/              # DTOs and interfaces
├── guards/             # Route guards
├── interceptors/       # HTTP interceptors
└── {feature}.routes.ts # Lazy-loaded routes
```

### 3.4 HTTP Flow

```
1. Component calls service method
2. Service makes HttpClient request
3. authTokenInterceptor adds JWT Bearer token
4. (Mock mode: mockApiInterceptor intercepts and returns mock data)
5. Backend responds with JSON
6. Service updates signal state via tap()
7. Components react to signal changes automatically
```

### 3.5 Component Communication

**Pages (Smart Components)**:
- Inject services via `inject()`
- Call service methods
- Read signals directly in templates
- Handle navigation

**Views (Dumb Components)**:
- Accept props via `input.required<T>()`
- Emit events via `output<T>()`
- Use `ChangeDetectionStrategy.OnPush`
- No service dependencies

**Example**:
```typescript
// Page Component
export class ProductCatalogPageComponent {
    private readonly productService = inject(ProductService);
    readonly products = this.productService.products;  // Signal
}

// View Component
@Component({
    selector: 'app-product-card',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductCardComponent {
    product = input.required<ProductDto>();
    onAddToCart = output<string>();
    
    handleAddClick(): void {
        this.onAddToCart.emit(this.product().id);
    }
}
```

---

## 4. Directory Structure

### 4.1 Project Root

```
onlineshopui/
├── src/
│   ├── app/                  # Application source
│   ├── environments/         # Environment configurations
│   ├── public/               # Static assets
│   ├── styles.css            # Global styles
│   └── main.ts               # Bootstrap entry point
├── angular.json              # Angular CLI configuration
├── package.json              # Dependencies and scripts
├── tsconfig.json             # TypeScript configuration
├── .postcssrc.json           # PostCSS configuration
└── vitest.config.ts          # Test configuration
```

### 4.2 Application Structure (`/src/app/`)

```
app/
├── features/                 # Feature modules (business logic)
│   ├── auth/                # Authentication feature
│   │   ├── components/
│   │   │   └── pages/
│   │   │       ├── login-page.component.ts
│   │   │       └── register-page.component.ts
│   │   ├── services/
│   │   │   └── auth.service.ts
│   │   ├── types/
│   │   │   └── auth.dto.ts
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   ├── guest.guard.ts
│   │   │   └── roles.guard.ts
│   │   ├── directives/
│   │   │   └── has-role.directive.ts
│   │   ├── interceptors/
│   │   │   └── auth-token.interceptor.ts
│   │   ├── utils/
│   │   │   └── auth-forms.util.ts
│   │   └── auth.routes.ts
│   │
│   ├── products/            # Product management
│   │   ├── components/
│   │   │   ├── pages/
│   │   │   │   ├── product-catalog-page.component.ts
│   │   │   │   ├── product-detail-page.component.ts
│   │   │   │   ├── product-create-page.component.ts
│   │   │   │   └── product-update-page.component.ts
│   │   │   └── views/
│   │   │       ├── product-card.component.ts
│   │   │       └── product-form.component.ts
│   │   ├── services/
│   │   │   └── product.service.ts
│   │   ├── types/
│   │   │   └── product.dto.ts
│   │   ├── utils/
│   │   │   └── product-forms.util.ts
│   │   └── products.routes.ts
│   │
│   ├── cart/                # Shopping cart
│   │   ├── components/
│   │   │   ├── pages/
│   │   │   │   └── cart-overview-page.component.ts
│   │   │   └── views/
│   │   │       ├── cart-item-row.component.ts
│   │   │       └── cart-summary.component.ts
│   │   ├── services/
│   │   │   └── cart.service.ts
│   │   ├── types/
│   │   │   └── cart.type.ts
│   │   ├── utils/
│   │   │   └── cart.util.ts
│   │   └── cart.routes.ts
│   │
│   └── orders/              # Order management
│       ├── components/
│       │   ├── pages/
│       │   │   ├── orders-overview-page.component.ts
│       │   │   └── order-detail-page.component.ts
│       │   └── views/
│       │       └── order-card.component.ts
│       ├── services/
│       │   └── orders.service.ts
│       ├── types/
│       │   └── order.dto.ts
│       ├── utils/
│       │   └── order.util.ts
│       └── orders.routes.ts
│
├── core/                    # Core functionality (singletons)
│   ├── config/              # Constants and configurations
│   │   ├── api.config.ts
│   │   ├── icons.config.ts
│   │   ├── navigation.constants.ts
│   │   └── validation-messages.constants.ts
│   ├── services/            # App-wide services
│   │   └── notifications.service.ts
│   ├── providers/           # DI providers
│   │   ├── environment.provider.ts
│   │   ├── validation-messages.provider.ts
│   │   └── mock-interceptors.provider.ts
│   ├── types/               # Shared types
│   │   ├── dtos/            # All DTOs
│   │   │   ├── user.dto.ts
│   │   │   ├── product.dto.ts
│   │   │   ├── order.dto.ts
│   │   │   └── ...
│   │   ├── notification.type.ts
│   │   └── environment.type.ts
│   └── mocks/               # Mock API implementation
│       ├── data/            # Mock data
│       │   ├── users.mock.ts
│       │   ├── products.mock.ts
│       │   └── orders.mock.ts
│       └── interceptors/    # Mock interceptor
│           └── mock-api.interceptor.ts
│
├── clib/                    # Component library (reusable UI)
│   ├── components/          # Shared components
│   │   ├── card.component.ts
│   │   ├── error-message.component.ts
│   │   ├── icon.component.ts
│   │   ├── modal.component.ts
│   │   ├── navbar.component.ts
│   │   ├── notification-popup.component.ts
│   │   └── spinner.component.ts
│   ├── layouts/             # Layout components
│   │   └── root-layout.component.ts
│   └── services/            # UI services
│       └── theme.service.ts
│
├── app.component.ts         # Root component
├── app.config.ts            # Application providers
└── app.routes.ts            # Root routing
```

---

## 5. Development Setup

### 5.1 Prerequisites

- **Node.js 24+** (specified in `package.json` engines)
- **npm 11.6.1** (package manager)

### 5.2 Installation

```bash
cd onlineshopui
npm install
```

### 5.3 Running the Application

**Option 1: Development Mode (with Backend)**

Requires backend API running at `http://localhost:3000/api`.

```bash
npm start
# or
npm run start
# or
ng serve
```

Application runs at: `http://localhost:4200`

**Option 2: Mock Mode (No Backend Required)**

Uses mock API interceptor for all HTTP requests.

```bash
npm start:mock
# or
npm run start:mock
# or
ng serve --configuration mock
```

**Mock Users**:
| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | `admin123` | ADMIN |
| `user@example.com` | `user123` | CUSTOMER |
| `jane@example.com` | `jane123` | CUSTOMER |

### 5.4 Build Commands

```bash
# Development build
npm run build

# Production build
ng build --configuration production

# Watch mode (auto-rebuild on changes)
npm run watch
```

### 5.5 Testing

```bash
# Run tests with Karma/Jasmine
npm test
# or
npm run test
```

### 5.6 Linting & Formatting

```bash
# Run ESLint
npm run lint

# Format code with Prettier
npm run format
```

---

## 6. Feature Modules

### 6.1 Authentication Feature (`/features/auth/`)

#### Routes

**Base**: `/auth`

| Path | Component | Guard | Description |
|------|-----------|-------|-------------|
| `/auth/login` | LoginPageComponent | guestGuard | Login form |
| `/auth/register` | RegisterPageComponent | guestGuard | Registration form |

#### AuthService (`/services/auth.service.ts`)

**Signals**:
```typescript
export class AuthService {
    // Private signals
    private readonly token = signal<string | null>(this.readTokenFromStorage());
    private readonly user = signal<UserDto | null>(null);
    private readonly profileLoaded = signal(false);
    
    // Public computed signals
    readonly isAuthenticated = computed(() => this.token() !== null);
    readonly roles = computed<string[]>(() => {
        const user = this.user();
        return user?.role ? [user.role] : [];
    });
}
```

**Methods**:
```typescript
login(credentials: LoginCredentialsDto): Observable<void>
// POST /auth/login → stores token → loads profile

register(payload: RegisterRequestDto): Observable<void>
// POST /auth/register → returns void

logout(): void
// Clears token from localStorage → navigates to login

loadProfileIfNeeded(): Observable<void>
// GET /auth/profile (if not already loaded)

getToken(): string | null
// Returns current JWT token

hasRole(roles: string[]): boolean
// Checks if user has any of the specified roles
```

**localStorage Key**: `access_token`

#### Guards

**authGuard** (`/guards/auth.guard.ts`):
```typescript
export const authGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    
    if (authService.isAuthenticated()) {
        return true;
    }
    
    // Store intended URL for redirect after login
    return router.createUrlTree(['/auth/login'], {
        queryParams: { returnUrl: state.url }
    });
};
```

**guestGuard** (`/guards/guest.guard.ts`):
```typescript
// Prevents authenticated users from accessing login/register
// Redirects to /products/overview if already logged in
```

**rolesGuard** (`/guards/roles.guard.ts`):
```typescript
// Checks route data for required roles
// Route configuration:
{
    path: 'admin',
    canActivate: [rolesGuard],
    data: { roles: [UserRole.ADMIN] }
}
```

#### Directives

**HasRoleDirective** (`/directives/has-role.directive.ts`):
```typescript
// Structural directive for conditional rendering
// Usage in templates:
<button *appHasRole="UserRole.ADMIN">Admin Action</button>
```

#### Interceptors

**authTokenInterceptor** (`/interceptors/auth-token.interceptor.ts`):
```typescript
export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.getToken();
    
    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }
    
    return next(req);
};
```

#### DTOs (`/types/auth.dto.ts`)
```typescript
export interface LoginCredentialsDto {
    email: string;
    password: string;
}

export interface RegisterRequestDto {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}

export interface JwtPayloadDto {
    access_token: string;
}

export interface UserDto {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    role: UserRole;
}

export enum UserRole {
    ADMIN = 'ADMIN',
    CUSTOMER = 'CUSTOMER'
}
```

---

### 6.2 Products Feature (`/features/products/`)

#### Routes

**Base**: `/products`

| Path | Component | Guard | Description |
|------|-----------|-------|-------------|
| `/products/overview` | ProductCatalogPageComponent | authGuard | Product list (default) |
| `/products/:id` | ProductDetailPageComponent | authGuard | Product details |
| `/products/create` | ProductCreatePageComponent | authGuard + rolesGuard(ADMIN) | Create product |
| `/products/update/:id` | ProductUpdatePageComponent | authGuard + rolesGuard(ADMIN) | Edit product |

#### ProductService (`/services/product.service.ts`)

**Signals**:
```typescript
export class ProductService {
    // Private signals
    private readonly _products = signal<ProductDto[]>([]);
    private readonly _selectedProduct = signal<ProductDto | null>(null);
    private readonly _categories = signal<ProductCategoryDto[]>([]);
    private readonly _loading = signal(false);
    private readonly _error = signal<string | null>(null);
    
    // Public readonly signals
    readonly products = this._products.asReadonly();
    readonly selectedProduct = this._selectedProduct.asReadonly();
    readonly categories = this._categories.asReadonly();
    readonly loading = this._loading.asReadonly();
    readonly error = this._error.asReadonly();
}
```

**Methods**:
```typescript
loadAll(): Observable<void>
// GET /products → updates products signal

loadById(id: string): Observable<void>
// GET /products/:id → updates selectedProduct signal

loadCategories(): Observable<void>
// GET /products/categories → updates categories signal

create(data: CreateProductRequest): Observable<void>
// POST /products (ADMIN only)

update(id: string, data: UpdateProductRequest): Observable<void>
// PUT /products/:id (ADMIN only)

delete(id: string): Observable<void>
// DELETE /products/:id (ADMIN only)
```

**Error Handling**:
```typescript
loadAll(): Observable<void> {
    this._loading.set(true);
    this._error.set(null);
    
    return this.http.get<ProductDto[]>(`${apiUrl}/products`).pipe(
        tap(products => this._products.set(products)),
        catchError(err => {
            this._error.set('Failed to load products');
            return of([]);  // Return empty array to allow app to continue
        }),
        finalize(() => this._loading.set(false)),
        map(() => undefined)
    );
}
```

#### Pages

**ProductCatalogPageComponent** (`/components/pages/product-catalog-page.component.ts`):
- Displays grid of products
- Admin controls (edit/delete) visible with `*appHasRole="UserRole.ADMIN"`
- Add-to-cart functionality
- Modal confirmation for deletion

**ProductDetailPageComponent** (`/components/pages/product-detail-page.component.ts`):
- Single product view with image
- Quantity selector (signal-based)
- Computed `totalPrice` signal: `computed(() => product().price * quantity())`
- Add-to-cart with selected quantity
- Image error handling with placeholder fallback

**ProductCreatePageComponent** (`/components/pages/product-create-page.component.ts`):
- Admin-only page
- Form for creating products
- Category dropdown
- Image URL input

**ProductUpdatePageComponent** (`/components/pages/product-update-page.component.ts`):
- Admin-only page
- Pre-populated form with existing product data
- Updates via PUT request

#### Views

**ProductCardComponent** (`/components/views/product-card.component.ts`):
```typescript
@Component({
    selector: 'app-product-card',
    imports: [/* ... */],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductCardComponent {
    // Inputs (signals)
    product = input.required<ProductDto>();
    showAdminControls = input(false);
    
    // Outputs (events)
    onViewDetails = output<string>();
    onEdit = output<string>();
    onDelete = output<string>();
    onAddToCart = output<string>();
    
    handleViewClick(): void {
        this.onViewDetails.emit(this.product().id);
    }
}
```

**ProductFormComponent** (`/components/views/product-form.component.ts`):
- Reusable form for create/update
- Inputs: `form`, `categories`, `isSubmitting`, `submitLabel`
- Outputs: `formSubmit`, `cancelled`
- Form validation with reactive forms

#### DTOs (`/types/product.dto.ts`)
```typescript
export interface ProductDto {
    id: string;
    name: string;
    description: string;
    price: number;
    weight: number;
    category: ProductCategoryDto;
    imageUrl: string;
}

export interface ProductCategoryDto {
    id: string;
    name: string;
    description: string;
}

export type CreateProductRequest = Omit<ProductDto, 'id' | 'category'> & {
    categoryId: string;
};

export type UpdateProductRequest = Partial<CreateProductRequest>;
```

---

### 6.3 Cart Feature (`/features/cart/`)

#### Routes

**Base**: `/cart`

| Path | Component | Description |
|------|-----------|-------------|
| `/cart/overview` | CartOverviewPageComponent | Shopping cart view (default) |

#### CartService (`/services/cart.service.ts`)

**Signals**:
```typescript
export class CartService {
    // Private signal
    private readonly _items = signal<CartItem[]>(this.readFromStorage());
    
    // Public signals
    readonly items = this._items.asReadonly();
    readonly totalItems = computed(() =>
        this._items().reduce((sum, item) => sum + item.quantity, 0)
    );
}
```

**Methods**:
```typescript
addItem(productId: string, quantity: number): void
// Adds item or increments quantity if exists → persists to localStorage

updateQuantity(productId: string, quantity: number): void
// Updates item quantity → persists

removeItem(productId: string): void
// Removes item from cart → persists

clear(): void
// Empties entire cart → persists

private readFromStorage(): CartItem[]
// Reads cart state from localStorage

private persistToStorage(): void
// Saves cart state to localStorage
```

**localStorage Key**: `cart_state`

**Storage Format**:
```json
{
    "items": [
        { "productId": "uuid-1", "quantity": 2 },
        { "productId": "uuid-2", "quantity": 1 }
    ]
}
```

#### Pages

**CartOverviewPageComponent** (`/components/pages/cart-overview-page.component.ts`):
- Loads product details for each cart item
- Displays list of cart items with quantity controls
- Shows subtotal (computed from products and quantities)
- Checkout button → creates order → navigates to orders
- Clear cart button
- Empty state message if no items

**Computed Subtotal**:
```typescript
readonly subtotal = computed(() => {
    const items = this.cartService.items();
    const products = this.productService.products();
    const productsById = new Map(products.map(p => [p.id, p]));
    
    return items.reduce((sum, item) => {
        const product = productsById.get(item.productId);
        return sum + (product ? product.price * item.quantity : 0);
    }, 0);
});
```

#### Views

**CartItemRowComponent** (`/components/views/cart-item-row.component.ts`):
- Displays product name, price, quantity, line total
- Quantity input field with change event
- Remove button

**CartSummaryComponent** (`/components/views/cart-summary.component.ts`):
- Shows item count and subtotal
- Checkout button (disabled if cart empty)

#### Types (`/types/cart.type.ts`)
```typescript
export interface CartItem {
    productId: string;
    quantity: number;
}

export interface CartStorage {
    items: CartItem[];
}
```

---

### 6.4 Orders Feature (`/features/orders/`)

#### Routes

**Base**: `/orders`

| Path | Component | Description |
|------|-----------|-------------|
| `/orders/overview` | OrdersOverviewPageComponent | List of user's orders (default) |
| `/orders/details/:id` | OrderDetailPageComponent | Single order details |

#### OrdersService (`/services/orders.service.ts`)

**Signals**:
```typescript
export class OrdersService {
    private readonly _orders = signal<OrderDto[]>([]);
    private readonly _selectedOrder = signal<OrderDto | null>(null);
    private readonly _loading = signal(false);
    private readonly _error = signal<string | null>(null);
    
    readonly orders = this._orders.asReadonly();
    readonly selectedOrder = this._selectedOrder.asReadonly();
    readonly loading = this._loading.asReadonly();
    readonly error = this._error.asReadonly();
}
```

**Methods**:
```typescript
loadAll(): Observable<void>
// GET /orders → updates orders signal

loadById(id: string): Observable<void>
// GET /orders/:id → updates selectedOrder signal

create(order: CreateOrderDto): Observable<string>
// POST /orders → returns created order ID
```

#### Pages

**OrdersOverviewPageComponent** (`/components/pages/orders-overview-page.component.ts`):
- Loads all orders on init
- Maps orders to summaries via computed signal
- Displays order cards
- Click to navigate to order details

**OrderDetailPageComponent** (`/components/pages/order-detail-page.component.ts`):
- Loads order by ID from route parameter
- Displays order items with product details
- Shows shipping address
- Displays location details for each item (shipped-from)

#### Views

**OrderCardComponent** (`/components/views/order-card.component.ts`):
- Compact order display
- Shows order ID, date, item count, total
- Click to view details

#### DTOs (`/types/order.dto.ts`)
```typescript
export interface OrderDto {
    id: string;
    userId: string;
    createdAt: string;
    address: AddressDto;
    details?: OrderDetailsDto[];
}

export interface OrderDetailsDto {
    orderId: string;
    product: ProductDto;
    shippedFrom: LocationDto;
    quantity: number;
}

export interface CreateOrderItemDto {
    productId: string;
    quantity: number;
}

export interface CreateOrderDto {
    items: CreateOrderItemDto[];
}

export interface AddressDto {
    country: string;
    city: string;
    county: string;
    streetAddress: string;
}

export interface LocationDto {
    id: string;
    name: string;
    address: AddressDto;
}
```

---

## 7. State Management

### 7.1 Signal Patterns

**Mutable Signal**:
```typescript
private readonly _data = signal<DataType[]>([]);
```

**Readonly Accessor**:
```typescript
readonly data = this._data.asReadonly();
```

**Computed Signal**:
```typescript
readonly derivedData = computed(() => {
    return this._data().map(item => transform(item));
});
```

**Update Signal Immutably**:
```typescript
this._data.update(currentData => {
    // Return new array (immutable)
    return [...currentData, newItem];
});

// Or replace entirely
this._data.set(newData);
```

### 7.2 Service Pattern

**Standard Service Structure**:
```typescript
@Injectable({ providedIn: 'root' })
export class FeatureService {
    private readonly http = inject(HttpClient);
    private readonly environment = inject(EnvironmentConfig);
    
    // Private signals for state
    private readonly _data = signal<Data[]>([]);
    private readonly _loading = signal(false);
    private readonly _error = signal<string | null>(null);
    
    // Public readonly accessors
    readonly data = this._data.asReadonly();
    readonly loading = this._loading.asReadonly();
    readonly error = this._error.asReadonly();
    
    // Methods return Observable<void> for side effects only
    load(): Observable<void> {
        this._loading.set(true);
        this._error.set(null);
        
        return this.http.get<Data[]>(`${this.environment.apiUrl}/endpoint`).pipe(
            tap(data => this._data.set(data)),
            catchError(err => {
                this._error.set(err.message || 'Failed to load data');
                return of([]);
            }),
            finalize(() => this._loading.set(false)),
            map(() => undefined)  // Return void
        );
    }
}
```

### 7.3 localStorage Persistence

**AuthService Token Persistence**:
```typescript
private readonly STORAGE_KEY = 'access_token';

private readTokenFromStorage(): string | null {
    try {
        return localStorage.getItem(this.STORAGE_KEY);
    } catch {
        return null;
    }
}

private saveTokenToStorage(token: string): void {
    try {
        localStorage.setItem(this.STORAGE_KEY, token);
    } catch {
        console.error('Failed to save token to localStorage');
    }
}

private removeTokenFromStorage(): void {
    try {
        localStorage.removeItem(this.STORAGE_KEY);
    } catch {
        console.error('Failed to remove token from localStorage');
    }
}
```

**CartService State Persistence**:
```typescript
private readonly STORAGE_KEY = 'cart_state';

private readFromStorage(): CartItem[] {
    try {
        const json = localStorage.getItem(this.STORAGE_KEY);
        if (!json) return [];
        const storage: CartStorage = JSON.parse(json);
        return storage.items || [];
    } catch {
        return [];
    }
}

private persistToStorage(): void {
    try {
        const storage: CartStorage = { items: this._items() };
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(storage));
    } catch {
        console.error('Failed to persist cart to localStorage');
    }
}
```

### 7.4 RxJS Patterns

**takeUntilDestroyed for Auto-Cleanup**:
```typescript
export class SomeComponent implements OnInit {
    private readonly destroyRef = inject(DestroyRef);
    private readonly service = inject(SomeService);
    
    ngOnInit(): void {
        this.service.load()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe();
    }
}
```

**take(1) for One-Time Operations**:
```typescript
// No need to unsubscribe - completes after one emission
this.service.load().pipe(take(1)).subscribe();
```

**switchMap for Request Chaining**:
```typescript
this.authService.login(credentials).pipe(
    switchMap(() => this.authService.loadProfileIfNeeded()),
    take(1)
).subscribe();
```

**tap for Side Effects**:
```typescript
return this.http.get<Data[]>(url).pipe(
    tap(data => this._data.set(data)),  // Side effect
    tap(() => console.log('Data loaded'))
);
```

**finalize for Cleanup**:
```typescript
return this.http.get<Data[]>(url).pipe(
    tap(data => this._data.set(data)),
    finalize(() => this._loading.set(false))  // Always runs
);
```

---

## 9. Routing & Navigation

### 8.1 Main Routes (`app.routes.ts`)

```typescript
export const routes: Routes = [
    // Auth routes (guest only)
    {
        path: 'auth',
        canActivate: [guestGuard],
        loadChildren: () => import('./features/auth/auth.routes')
            .then(m => m.AUTH_ROUTES)
    },
    
    // Authenticated routes
    {
        path: '',
        component: RootLayoutComponent,
        canActivate: [authGuard],
        children: [
            {
                path: 'products',
                loadChildren: () => import('./features/products/products.routes')
                    .then(m => m.PRODUCTS_ROUTES)
            },
            {
                path: 'cart',
                loadChildren: () => import('./features/cart/cart.routes')
                    .then(m => m.CART_ROUTES)
            },
            {
                path: 'orders',
                loadChildren: () => import('./features/orders/orders.routes')
                    .then(m => m.ORDERS_ROUTES)
            },
            {
                path: '',
                redirectTo: '/products/overview',
                pathMatch: 'full'
            }
        ]
    },
    
    // Fallback
    {
        path: '**',
        redirectTo: '/products/overview'
    }
];
```

### 8.2 Feature Routes

**Auth Routes** (`/features/auth/auth.routes.ts`):
```typescript
export const AUTH_ROUTES: Routes = [
    {
        path: 'login',
        component: LoginPageComponent
    },
    {
        path: 'register',
        component: RegisterPageComponent
    }
];
```

**Products Routes** (`/features/products/products.routes.ts`):
```typescript
export const PRODUCTS_ROUTES: Routes = [
    {
        path: 'overview',
        component: ProductCatalogPageComponent
    },
    {
        path: 'create',
        component: ProductCreatePageComponent,
        canActivate: [rolesGuard],
        data: { roles: [UserRole.ADMIN] }
    },
    {
        path: 'update/:id',
        component: ProductUpdatePageComponent,
        canActivate: [rolesGuard],
        data: { roles: [UserRole.ADMIN] }
    },
    {
        path: ':id',
        component: ProductDetailPageComponent
    },
    {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full'
    }
];
```

### 8.3 Navigation Constants (`/core/config/navigation.constants.ts`)

```typescript
export const AppNavRoutes = {
    Auth: {
        root: 'auth',
        features: {
            login: 'login',
            register: 'register'
        }
    },
    Products: {
        root: 'products',
        features: {
            overview: 'overview',
            create: 'create',
            update: 'update',
            detail: ':id'
        }
    },
    Cart: {
        root: 'cart',
        features: {
            overview: 'overview'
        }
    },
    Orders: {
        root: 'orders',
        features: {
            overview: 'overview',
            details: 'details'
        }
    }
};
```

**Usage**:
```typescript
// Navigate to product create page
this.router.navigate([AppNavRoutes.Products.root, AppNavRoutes.Products.features.create]);
```

### 8.4 Lazy Loading

All feature modules are lazy-loaded via `loadChildren`:

```typescript
{
    path: 'products',
    loadChildren: () => import('./features/products/products.routes')
        .then(m => m.PRODUCTS_ROUTES)
}
```

**Benefits**:
- Smaller initial bundle size
- Faster app startup
- Code-splitting per feature

---

## 9. HTTP Interceptors

### 9.1 Auth Token Interceptor (`/features/auth/interceptors/auth-token.interceptor.ts`)

**Purpose**: Automatically add JWT Bearer token to all HTTP requests.

```typescript
export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.getToken();
    
    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }
    
    return next(req);
};
```

**Registration** (`app.config.ts`):
```typescript
provideHttpClient(
    withInterceptors([authTokenInterceptor])
)
```

### 9.2 Mock API Interceptor (`/core/mocks/interceptors/mock-api.interceptor.ts`)

**Purpose**: Intercept HTTP requests in mock mode and return mock data.

**Conditional Activation**:
```typescript
// Only active when environment.envType === 'mock'
export function getMockInterceptors(envType?: string): HttpInterceptorFn[] {
    return envType === 'mock' ? [mockApiInterceptor] : [];
}

// In app.config.ts
provideHttpClient(
    withInterceptors([
        authTokenInterceptor,
        ...getMockInterceptors(environment.envType)
    ])
)
```

**Interceptor Structure**:
```typescript
export const mockApiInterceptor: HttpInterceptorFn = (req, next) => {
    // Only intercept requests to mock API base URL
    if (!req.url.startsWith('http://mock-api')) {
        return next(req);
    }
    
    console.log('[mock-api] Intercepted:', req.method, req.url);
    
    // Route to feature handlers
    if (req.url.includes('/auth/')) {
        return handleAuthFeature(req);
    }
    if (req.url.includes('/products')) {
        return handleProductsFeature(req);
    }
    if (req.url.includes('/orders')) {
        return handleOrdersFeature(req);
    }
    
    // Fallback: 404
    return of(new HttpResponse({
        status: 404,
        body: { error: 'Mock endpoint not found' }
    })).pipe(delay(MOCK_DELAY_MS));
};

const MOCK_DELAY_MS = 300;  // Simulate network latency
```

**Auth Handler**:
```typescript
function handleAuthFeature(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    // POST /auth/login
    if (req.url.endsWith('/login') && req.method === 'POST') {
        const { email, password } = req.body;
        const credentials = MOCK_USER_CREDENTIALS.get(email);
        
        if (credentials && credentials.password === password) {
            const token = `mock-jwt-token-${credentials.user.id}`;
            MOCK_SESSION_TOKENS.set(token, credentials.user);
            
            return of(new HttpResponse({
                status: 200,
                body: { token }
            })).pipe(delay(MOCK_DELAY_MS));
        }
        
        return of(new HttpErrorResponse({
            status: 401,
            error: { message: 'Invalid credentials' }
        })).pipe(delay(MOCK_DELAY_MS));
    }
    
    // GET /auth/profile
    if (req.url.endsWith('/profile') && req.method === 'GET') {
        const token = req.headers.get('Authorization')?.replace('Bearer ', '');
        const user = token ? MOCK_SESSION_TOKENS.get(token) : null;
        
        if (user) {
            return of(new HttpResponse({
                status: 200,
                body: user
            })).pipe(delay(MOCK_DELAY_MS));
        }
        
        return of(new HttpErrorResponse({
            status: 401,
            error: { message: 'Unauthorized' }
        })).pipe(delay(MOCK_DELAY_MS));
    }
    
    // POST /auth/register
    // ... similar pattern
}
```

**Products Handler**:
```typescript
function handleProductsFeature(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    // GET /products
    if (req.url.endsWith('/products') && req.method === 'GET') {
        return of(new HttpResponse({
            status: 200,
            body: MOCK_PRODUCTS
        })).pipe(delay(MOCK_DELAY_MS));
    }
    
    // GET /products/:id
    if (req.url.match(/\/products\/[a-z0-9-]+$/) && req.method === 'GET') {
        const id = req.url.split('/').pop();
        const product = MOCK_PRODUCTS.find(p => p.id === id);
        
        if (product) {
            return of(new HttpResponse({ status: 200, body: product }))
                .pipe(delay(MOCK_DELAY_MS));
        }
        
        return of(new HttpErrorResponse({ status: 404 }))
            .pipe(delay(MOCK_DELAY_MS));
    }
    
    // POST /products (create)
    // PUT /products/:id (update)
    // DELETE /products/:id
    // ... similar patterns
}
```

---

## 10. Component Architecture

### 10.1 Pages (Smart Components)

**Characteristics**:
- Located in `components/pages/`
- Implement `OnInit` lifecycle
- Inject services via `inject()`
- Handle data fetching and navigation
- Use `DestroyRef` and `takeUntilDestroyed` for cleanup
- No `ChangeDetectionStrategy` override (default)

**Example**:
```typescript
@Component({
    selector: 'app-product-catalog-page',
    standalone: true,
    imports: [CommonModule, ProductCardComponent, SpinnerComponent],
    templateUrl: './product-catalog-page.component.html'
})
export class ProductCatalogPageComponent implements OnInit {
    private readonly productService = inject(ProductService);
    private readonly cartService = inject(CartService);
    private readonly router = inject(Router);
    private readonly destroyRef = inject(DestroyRef);
    
    readonly products = this.productService.products;
    readonly loading = this.productService.loading;
    
    ngOnInit(): void {
        this.loadProducts();
    }
    
    private loadProducts(): void {
        this.productService.loadAll()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe();
    }
    
    handleAddToCart(productId: string): void {
        this.cartService.addItem(productId, 1);
        // Show notification
    }
    
    handleViewDetails(productId: string): void {
        this.router.navigate(['/products', productId]);
    }
}
```

### 10.2 Views (Presentational Components)

**Characteristics**:
- Located in `components/views/`
- Accept data via `input.required<T>()` or `input<T>(defaultValue)`
- Emit events via `output<T>()`
- Use `ChangeDetectionStrategy.OnPush` for performance
- No service dependencies
- Pure functions

**Example**:
```typescript
@Component({
    selector: 'app-product-card',
    standalone: true,
    imports: [CommonModule, IconComponent, CardComponent],
    templateUrl: './product-card.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductCardComponent {
    // Inputs (signals)
    product = input.required<ProductDto>();
    showAdminControls = input(false);
    
    // Outputs (events)
    onViewDetails = output<string>();
    onEdit = output<string>();
    onDelete = output<string>();
    onAddToCart = output<string>();
    
    // Methods
    handleViewClick(): void {
        this.onViewDetails.emit(this.product().id);
    }
    
    handleEditClick(): void {
        this.onEdit.emit(this.product().id);
    }
    
    handleDeleteClick(): void {
        this.onDelete.emit(this.product().id);
    }
    
    handleAddToCartClick(): void {
        this.onAddToCart.emit(this.product().id);
    }
}
```

**Template**:
```html
<app-card [hoverable]="true">
    <img [src]="product().imageUrl" [alt]="product().name" />
    <h3>{{ product().name }}</h3>
    <p>{{ product().description }}</p>
    <p class="price">{{ product().price | currency }}</p>
    
    <div class="actions">
        <button (click)="handleViewClick()">View</button>
        <button (click)="handleAddToCartClick()">Add to Cart</button>
        
        @if (showAdminControls()) {
            <button (click)="handleEditClick()">Edit</button>
            <button (click)="handleDeleteClick()">Delete</button>
        }
    </div>
</app-card>
```

### 10.3 Reusable Components (`/clib/components/`)

**CardComponent**:
- Wrapper with configurable padding, shadow, hover effects
- Inputs: `padding` ('sm'|'md'|'lg'), `shadow`, `hoverable`, `fullHeight`
- Content projection via `<ng-content>`

**ErrorMessageComponent**:
- Displays error message with icon
- Optional retry action button
- Inputs: `message`, `showRetry`
- Outputs: `retry`

**IconComponent**:
- Wrapper for lucide-angular icons
- Inputs: `name`, `size` (xs/sm/md/lg)
- Maps size to pixel values

**ModalComponent**:
- Generic modal/dialog
- Inputs: `title`, `showModal`
- Outputs: `closed`
- Content projection for body and actions

**NavbarComponent**:
- Application header with navigation links
- Shows user email when authenticated
- Theme toggle (light/dark)
- Logout button
- Responsive mobile menu

**NotificationPopupComponent**:
- Toast notification display
- Reads from `NotificationsService`
- Auto-dismiss based on duration
- Stacks notifications

**SpinnerComponent**:
- Loading indicator with optional message
- CSS-based animation

### 10.4 Layouts

**RootLayoutComponent** (`/clib/layouts/root-layout.component.ts`):
```typescript
@Component({
    selector: 'app-root-layout',
    standalone: true,
    imports: [RouterOutlet, NavbarComponent, NotificationPopupComponent],
    template: `
        <div class="root-layout">
            <app-navbar />
            <main class="main-content">
                <router-outlet />
            </main>
            <app-notification-popup />
        </div>
    `
})
export class RootLayoutComponent {}
```

---

## 11. Services & DTOs

### 11.1 Core Services

**NotificationsService** (`/core/services/notifications.service.ts`):

**Methods**:
```typescript
notifySuccess(input: NotificationInput): void
notifyInfo(input: NotificationInput): void
notifyError(input: NotificationInput): void
dismiss(id: string): void
clearAll(): void
```

**Signals**:
```typescript
private readonly _notifications = signal<Notification[]>([]);
readonly notifications = this._notifications.asReadonly();
```

**Auto-Dismiss**:
```typescript
private scheduleAutoDismiss(id: string, durationMs: number): void {
    window.setTimeout(() => {
        this.dismiss(id);
    }, durationMs);
}
```

**ThemeService** (`/clib/services/theme.service.ts`):

**Methods**:
```typescript
toggle(): void
enableDarkMode(): void
disableDarkMode(): void
```

**Signals**:
```typescript
private readonly _isDarkMode = signal(this.readThemeFromStorage());
readonly isDarkMode = this._isDarkMode.asReadonly();
```

**Effect**:
```typescript
constructor() {
    effect(() => {
        const isDark = this._isDarkMode();
        if (isDark) {
            document.documentElement.classList.add('dark', 'theme-dark');
        } else {
            document.documentElement.classList.remove('dark', 'theme-dark');
        }
        this.persistThemeToStorage(isDark);
    });
}
```

**localStorage Key**: `theme` (stores 'light' or 'dark')

### 11.2 All DTOs

Located in `/core/types/dtos/`:

**Auth DTOs**: LoginCredentialsDto, RegisterRequestDto, JwtPayloadDto, UserDto

**Product DTOs**: ProductDto, ProductCategoryDto, CreateProductRequest, UpdateProductRequest

**Cart DTOs**: CartItem, CartStorage

**Order DTOs**: OrderDto, OrderDetailsDto, CreateOrderItemDto, CreateOrderDto, AddressDto, LocationDto

**Notification DTOs**: Notification, NotificationInput, NotificationLevel

---

## 12. Styling & Theming

### 12.1 Tailwind CSS v4

**Configuration** (`tailwind.config.ts`):
```typescript
export default {
    content: ['./src/**/*.{html,ts}'],
    theme: {
        extend: {
            colors: {
                primary: 'var(--color-primary)',
                surface: 'var(--color-surface)',
                border: 'var(--color-border)',
                text: 'var(--color-text)',
                accent: 'var(--color-accent)',
                destructive: 'var(--color-destructive)'
            }
        }
    }
};
```

**PostCSS** (`.postcssrc.json`):
```json
{
    "plugins": {
        "@tailwindcss/postcss": {}
    }
}
```

### 12.2 Color System (`/src/styles.css`)

```css
:root {
    --color-primary: #7a0f14;      /* Wine red */
    --color-surface: #fbf8f8;      /* Off-white */
    --color-border: #e2d3d4;       /* Light pink-gray */
    --color-text: #2a1516;         /* Dark brown */
    --color-accent: #a83b3f;       /* Burnt sienna */
    --color-destructive: #b2342f;  /* Warm red */
}

.theme-dark {
    --color-primary: #a81f25;      /* Brighter red */
    --color-surface: #121212;      /* Dark gray */
    --color-border: #302628;       /* Dark burgundy */
    --color-text: #efe6e6;         /* Light beige */
}
```

### 12.3 Dark Mode

**Implementation**: Class-based strategy

**Classes**: `dark` and `theme-dark` applied to `<html>` element

**ThemeService** toggles classes based on signal state:
```typescript
effect(() => {
    if (this._isDarkMode()) {
        document.documentElement.classList.add('dark', 'theme-dark');
    } else {
        document.documentElement.classList.remove('dark', 'theme-dark');
    }
});
```

**Persistence**: localStorage key `theme`

**Initial State**: Respects `prefers-color-scheme` media query

### 12.4 Icon Library

**lucide-angular** v0.577.0

**Configuration** (`/core/config/icons.config.ts`):
```typescript
import { 
    Sun, Moon, Menu, X, ChevronLeft, ChevronRight,
    Minus, Plus, ShoppingCart, User, LogOut,
    Trash2, Edit, Eye, Check, AlertCircle
} from 'lucide-angular';

export const AppIcons = {
    Sun, Moon, Menu, X, ChevronLeft, ChevronRight,
    Minus, Plus, ShoppingCart, User, LogOut,
    Trash2, Edit, Eye, Check, AlertCircle
};

export const AppIconSizePixels = {
    xs: 16,
    sm: 20,
    md: 24,
    lg: 28
};
```

**Registration** (`app.config.ts`):
```typescript
importProvidersFrom(LucideAngularModule.pick(AppIcons))
```

**Usage**:
```html
<lucide-icon [img]="AppIcons.ShoppingCart" [size]="24"></lucide-icon>
```

---

## 13. Mock Mode

### 13.1 Mock Data Structure (`/core/mocks/data/`)

**Mock Users** (`users.mock.ts`):
```typescript
export const MOCK_USERS: UserDto[] = [
    {
        id: 'admin-id',
        email: 'admin@example.com',
        firstName: 'Admin',
        lastName: 'User',
        role: UserRole.ADMIN
    },
    {
        id: 'user-id',
        email: 'user@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: UserRole.CUSTOMER
    },
    {
        id: 'jane-id',
        email: 'jane@example.com',
        firstName: 'Jane',
        lastName: 'Smith',
        role: UserRole.CUSTOMER
    }
];

export const MOCK_USER_CREDENTIALS = new Map([
    ['admin@example.com', { password: 'admin123', user: MOCK_USERS[0] }],
    ['user@example.com', { password: 'user123', user: MOCK_USERS[1] }],
    ['jane@example.com', { password: 'jane123', user: MOCK_USERS[2] }]
]);
```

**Mock Products** (`products.mock.ts`):

10 products across 4 categories:
- **Electronics**: Wireless Headphones ($79.99), Smart Watch ($199.99), Bluetooth Speaker ($49.99)
- **Clothing**: Cotton T-Shirt ($24.99), Denim Jeans ($59.99)
- **Home & Garden**: Garden Hose ($29.99), LED Desk Lamp ($39.99)
- **Sports**: Yoga Mat ($34.99), Running Shoes ($89.99), Fitness Tracker ($119.99)

**Mock Orders** (`orders.mock.ts`):

2 pre-populated orders:
- Order-1001: 2 Wireless Headphones + 1 Cotton T-Shirt (shipped from Portland)
- Order-1002: 1 Yoga Mat (shipped from Boise)

### 13.2 Session State

**In-Memory Session Storage**:
```typescript
// Maps JWT token to user
const MOCK_SESSION_TOKENS = new Map<string, UserDto>();

// Token format: mock-jwt-token-{userId}
const token = `mock-jwt-token-${user.id}`;
MOCK_SESSION_TOKENS.set(token, user);
```

**Token Validation**:
```typescript
const token = req.headers.get('Authorization')?.replace('Bearer ', '');
const user = token ? MOCK_SESSION_TOKENS.get(token) : null;

if (!user) {
    return of(new HttpErrorResponse({
        status: 401,
        error: { message: 'Unauthorized' }
    }));
}
```

### 13.3 Development Workflow

**Start Mock Mode**:
```bash
npm start:mock
```

**Benefits**:
- No backend required
- Offline development
- Faster iteration on UI
- Predictable test data
- Network simulation (300ms delay)

**Logging**:
All mock requests logged to console:
```
[mock-api] Intercepted: POST http://mock-api/auth/login
[mock-api] Response: 200 { token: 'mock-jwt-token-admin-id' }
```

---

## 14. Testing

### 14.1 Karma Configuration

**Test Runner**: Karma v6.4.4 with Jasmine v6.3.0

**Configuration** (`karma.conf.js`):
```javascript
module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    browsers: ['ChromeHeadless'],
    singleRun: false,
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/karma'),
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcovonly' }
      ]
    }
  });
};
```

### 14.2 Test Patterns

**AAA Pattern** (Arrange-Act-Assert):
```typescript
describe('NotificationsService', () => {
    let service: NotificationsService;
    
    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(NotificationsService);
    });
    
    it('should add notification with success level', () => {
        // Arrange
        const input: NotificationInput = {
            title: 'Test',
            message: 'Test message'
        };
        
        // Act
        service.notifySuccess(input);
        
        // Assert
        const notifications = service.notifications();
        expect(notifications.length).toBe(1);
        expect(notifications[0].level).toBe('success');
        expect(notifications[0].title).toBe('Test');
    });
});
```

**Fake Timers for Auto-Dismiss**:
```typescript
it('should auto-dismiss notification after duration', () => {
    // Arrange
    jasmine.clock().install();
    
    // Act
    service.notifySuccess({ title: 'Test', durationMs: 1000 });
    expect(service.notifications().length).toBe(1);
    
    jasmine.clock().tick(1000);
    
    // Assert
    expect(service.notifications().length).toBe(0);
    
    jasmine.clock().uninstall();
});
```

**localStorage Mocking**:
```typescript
it('should persist token to localStorage', () => {
    const setItemSpy = spyOn(Storage.prototype, 'setItem');
    
    service.login({ email: 'test@example.com', password: 'password' })
        .subscribe();
    
    expect(setItemSpy).toHaveBeenCalledWith('access_token', jasmine.any(String));
});
```

### 14.3 Running Tests

```bash
# Run tests in watch mode (browser opens)
npm test

# Run tests once with coverage
npm run test:coverage

# Run tests for CI/CD (headless)
npm run test:ci

# Watch mode (default for npm test)
npm run test:watch
```

**Coverage Output**: `./coverage/karma/`
- `index.html` - HTML report (open in browser)
- `lcov.info` - LCOV format for CI tools
- `coverage-final.json` - JSON format

---

## 15. Build & Configuration

### 15.1 Package.json Scripts

```json
{
    "scripts": {
        "start": "ng serve",
        "start:mock": "ng serve --configuration mock",
        "build": "ng build",
        "watch": "ng build --watch --configuration development",
        "test": "ng test",
        "lint": "ng lint",
        "format": "prettier --write \"src/**/*.{ts,html,css,scss}\""
    }
}
```

### 15.2 Angular.json Configurations

**Development**:
- File replacement: `environment.dev.ts`
- Source maps: true
- Optimization: false
- Output path: `dist/onlineshopui/browser`

**Production**:
- File replacement: `environment.prod.ts`
- Source maps: false
- Optimization: true
- Output hashing: all
- Budget warnings for bundle sizes

**Mock**:
- File replacement: `environment.mock.ts`
- Source maps: true
- Optimization: false

### 15.3 Environment Files

**environment.ts** (base):
```typescript
export const environment = {
    production: false,
    apiUrl: '${API_URL}',
    envType: 'development' as const
};
```

**environment.mock.ts**:
```typescript
export const environment = {
    production: false,
    apiUrl: 'http://mock-api',
    envType: 'mock' as const
};
```

**environment.prod.ts**:
```typescript
export const environment = {
    production: true,
    apiUrl: '${API_URL}',  // Set to production API URL
    envType: 'production' as const
};
```

### 15.4 TypeScript Configuration

**tsconfig.json**:
```json
{
    "compilerOptions": {
        "strict": true,
        "noImplicitOverride": true,
        "noPropertyAccessFromIndexSignature": true,
        "noImplicitReturns": true,
        "noFallthroughCasesInSwitch": true,
        "target": "ES2022",
        "module": "preserve",
        "lib": ["ES2022", "DOM", "DOM.Iterable"],
        "outDir": "./dist/out-tsc",
        "declaration": false,
        "downlevelIteration": true,
        "experimentalDecorators": true,
        "moduleResolution": "bundler",
        "importHelpers": true,
        "skipLibCheck": true,
        "esModuleInterop": true,
        "sourceMap": true,
        "baseUrl": "./"
    },
    "angularCompilerOptions": {
        "strictInjectionParameters": true,
        "strictInputAccessModifiers": true,
        "strictTemplates": true
    }
}
```

---

## Additional Resources

- **Angular Documentation**: https://angular.dev/
- **Angular Signals**: https://angular.dev/guide/signals
- **Tailwind CSS**: https://tailwindcss.com/docs
- **Lucide Icons**: https://lucide.dev/
- **RxJS**: https://rxjs.dev/
- **Jasmine**: https://jasmine.github.io/
- **Karma**: https://karma-runner.github.io/
- **TypeScript**: https://www.typescriptlang.org/docs/

---

**Development Server**: http://localhost:4200 (when running `npm start` or `npm start:mock`)
