import { TranslationsMap } from '../../types/i18n.types';

/**
 * English translations (default language)
 */
export const EN_TRANSLATIONS: TranslationsMap = {
  common: {
    loading: 'Loading...',
    error: 'An error occurred',
    tryAgain: 'Try Again',
    cancel: 'Cancel',
    continue: 'Continue',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    close: 'Close',
    confirm: 'Confirm',
    processing: 'Processing...'
  },
  nav: {
    products: 'Products',
    orders: 'Orders',
    cart: 'Cart',
    login: 'Login',
    register: 'Register',
    logout: 'Logout',
    language: 'Language',
    lightMode: 'Light Mode',
    darkMode: 'Dark Mode',
    onlineShop: 'Online Shop'
  },
  auth: {
    signIn: 'Sign In',
    signingIn: 'Signing In...',
    createAccount: 'Create Account',
    creatingAccount: 'Creating Account...',
    email: 'Email',
    password: 'Password',
    firstName: 'First Name',
    lastName: 'Last Name',
    dontHaveAccount: "Don't have an account?",
    alreadyHaveAccount: 'Already have an account?'
  },
  products: {
    title: 'Products',
    browseCollection: 'Browse our collection of products',
    addNew: 'Add New Product',
    backToProducts: 'Back to Products',
    description: 'Description',
    price: 'Price',
    weight: 'Weight',
    category: 'Category',
    supplier: 'Supplier',
    quantity: 'Quantity',
    totalPrice: 'Total Price:',
    imageUrl: 'Image URL',
    noProducts: 'No products available',
    deleteConfirm: 'Delete Product',
    deleteWarning: 'Are you sure you want to delete this product? This action cannot be undone.',
    productName: 'Product Name',
    enterProductName: 'Enter product name',
    enterDescription: 'Enter product description',
    selectCategory: 'Select a category',
    selectSupplier: 'Select a supplier',
    imageUrlPlaceholder: 'https://example.com/image.jpg',
    createNew: 'Create New Product',
    addNewProduct: 'Add a new product to your catalog',
    editProduct: 'Edit Product',
    updateProductInfo: 'Update product information',
    view: 'View'
  },
  cart: {
    title: 'Your Cart',
    reviewItems: 'Review items before placing your order.',
    orderSummary: 'Order Summary',
    placeOrder: 'Place Order',
    clearCart: 'Clear Cart',
    empty: 'Your cart is empty.',
    browseProducts: 'Browse Products',
    subtotal: 'Subtotal',
    items: 'Items',
    itemCount: '{{count}} item(s)',
    addToCart: 'Add to Cart',
    remove: 'Remove',
    decreaseQuantity: 'Decrease quantity',
    increaseQuantity: 'Increase quantity'
  },
  address: {
    title: 'Shipping Address',
    country: 'Country',
    countryPlaceholder: 'e.g., Romania',
    county: 'County/State',
    countyPlaceholder: 'e.g., Cluj',
    city: 'City',
    cityPlaceholder: 'e.g., Cluj-Napoca',
    streetAddress: 'Street Address',
    streetPlaceholder: 'e.g., 123 Main Street, Apt 4B'
  },
  orders: {
    title: 'Your Orders',
    trackPurchases: 'Track your recent purchases.',
    orderDetails: 'Order Details',
    createdAt: 'Ordered on',
    shippedFrom: 'Shipped from',
    noOrders: 'You have no orders yet.',
    backToOrders: 'Back to Orders'
  },
  validation: {
    required: 'This field is required',
    email: 'Please enter a valid email address',
    minLength: 'Minimum length is {{length}} characters',
    maxLength: 'Maximum length is {{length}} characters',
    min: 'Minimum value is {{min}}',
    max: 'Maximum value is {{max}}',
    pattern: 'Please enter a valid format',
    url: 'Please enter a valid URL'
  },
  notifications: {
    loginSuccess: 'Successfully logged in!',
    logoutSuccess: 'Successfully logged out',
    registrationSuccess: 'Account created successfully',
    addedToCart: 'Added to cart',
    productCreated: 'Product created successfully',
    productUpdated: 'Product updated successfully',
    productDeleted: 'Product deleted successfully',
    orderPlaced: 'Order placed successfully!',
    cartCleared: 'Cart cleared',
    genericError: 'Something went wrong. Please try again.'
  }
};
