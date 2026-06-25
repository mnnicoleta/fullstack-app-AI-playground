/**
 * Supported languages in the application
 */
export type SupportedLanguage = 'en' | 'de';

/**
 * Array of all supported language codes
 */
export const SUPPORTED_LANGUAGES: ReadonlyArray<SupportedLanguage> = ['en', 'de'] as const;

/**
 * Metadata for a language including display information
 */
export interface LanguageMetadata {
  code: SupportedLanguage;
  name: string;
  nativeName: string;
  flag: string; // Unicode flag emoji
}

/**
 * Language metadata for all supported languages
 */
export const LANGUAGE_METADATA: Record<SupportedLanguage, LanguageMetadata> = {
  en: {
    code: 'en',
    name: 'English',
    nativeName: 'English',
    flag: '🇬🇧'
  },
  de: {
    code: 'de',
    name: 'German',
    nativeName: 'Deutsch',
    flag: '🇩🇪'
  }
};

/**
 * Type-safe translation map structure
 * Enforces consistent translation structure across all languages
 */
export interface TranslationsMap {
  common: {
    loading: string;
    error: string;
    tryAgain: string;
    cancel: string;
    continue: string;
    save: string;
    delete: string;
    edit: string;
    close: string;
    confirm: string;
    processing: string;
  };
  nav: {
    products: string;
    orders: string;
    cart: string;
    login: string;
    register: string;
    logout: string;
    language: string;
    lightMode: string;
    darkMode: string;
    onlineShop: string;
  };
  auth: {
    signIn: string;
    signingIn: string;
    createAccount: string;
    creatingAccount: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    dontHaveAccount: string;
    alreadyHaveAccount: string;
  };
  products: {
    title: string;
    browseCollection: string;
    addNew: string;
    backToProducts: string;
    description: string;
    price: string;
    weight: string;
    category: string;
    supplier: string;
    quantity: string;
    totalPrice: string;
    imageUrl: string;
    noProducts: string;
    deleteConfirm: string;
    deleteWarning: string;
    productName: string;
    enterProductName: string;
    enterDescription: string;
    selectCategory: string;
    selectSupplier: string;
    imageUrlPlaceholder: string;
    createNew: string;
    addNewProduct: string;
    editProduct: string;
    updateProductInfo: string;
    view: string;
  };
  cart: {
    title: string;
    reviewItems: string;
    orderSummary: string;
    placeOrder: string;
    clearCart: string;
    empty: string;
    browseProducts: string;
    subtotal: string;
    items: string;
    itemCount: string;
    addToCart: string;
    remove: string;
    decreaseQuantity: string;
    increaseQuantity: string;
  };
  address: {
    title: string;
    country: string;
    countryPlaceholder: string;
    county: string;
    countyPlaceholder: string;
    city: string;
    cityPlaceholder: string;
    streetAddress: string;
    streetPlaceholder: string;
  };
  orders: {
    title: string;
    trackPurchases: string;
    orderDetails: string;
    createdAt: string;
    shippedFrom: string;
    noOrders: string;
    backToOrders: string;
  };
  validation: {
    required: string;
    email: string;
    minLength: string; // "Minimum length is {{length}} characters"
    maxLength: string; // "Maximum length is {{length}} characters"
    min: string; // "Minimum value is {{min}}"
    max: string; // "Maximum value is {{max}}"
    pattern: string;
    url: string;
  };
  notifications: {
    loginSuccess: string;
    logoutSuccess: string;
    registrationSuccess: string;
    addedToCart: string;
    productCreated: string;
    productUpdated: string;
    productDeleted: string;
    orderPlaced: string;
    cartCleared: string;
    genericError: string;
  };
}

/**
 * Utility type to extract all possible nested translation keys
 * Examples: "nav.products", "validation.required", "common.loading"
 */
export type TranslationKey = NestedKeyOf<TranslationsMap>;

/**
 * Utility type for extracting nested keys from an object type
 * @internal
 */
type NestedKeyOf<T> = T extends object
  ? {
      [K in keyof T & (string | number)]: T[K] extends object
        ? `${K}` | `${K}.${NestedKeyOf<T[K]>}`
        : `${K}`;
    }[keyof T & (string | number)]
  : never;
