import { TranslationsMap } from '../../types/i18n.types';

/**
 * German translations (Deutsch)
 */
export const DE_TRANSLATIONS: TranslationsMap = {
  common: {
    loading: 'Lädt...',
    error: 'Ein Fehler ist aufgetreten',
    tryAgain: 'Erneut versuchen',
    cancel: 'Abbrechen',
    continue: 'Weiter',
    save: 'Speichern',
    delete: 'Löschen',
    edit: 'Bearbeiten',
    close: 'Schließen',
    confirm: 'Bestätigen',
    processing: 'Verarbeitung...'
  },
  nav: {
    products: 'Produkte',
    orders: 'Bestellungen',
    cart: 'Warenkorb',
    login: 'Anmelden',
    register: 'Registrieren',
    logout: 'Abmelden',
    language: 'Sprache',
    lightMode: 'Heller Modus',
    darkMode: 'Dunkler Modus',
    onlineShop: 'Online-Shop'
  },
  auth: {
    signIn: 'Anmelden',
    signingIn: 'Wird angemeldet...',
    createAccount: 'Konto erstellen',
    creatingAccount: 'Konto wird erstellt...',
    email: 'E-Mail',
    password: 'Passwort',
    firstName: 'Vorname',
    lastName: 'Nachname',
    dontHaveAccount: 'Noch kein Konto?',
    alreadyHaveAccount: 'Bereits ein Konto?'
  },
  products: {
    title: 'Produkte',
    browseCollection: 'Durchsuchen Sie unsere Produktkollektion',
    addNew: 'Neues Produkt hinzufügen',
    backToProducts: 'Zurück zu Produkten',
    description: 'Beschreibung',
    price: 'Preis',
    weight: 'Gewicht',
    category: 'Kategorie',
    supplier: 'Lieferant',
    quantity: 'Menge',
    totalPrice: 'Gesamtpreis:',
    imageUrl: 'Bild-URL',
    noProducts: 'Keine Produkte verfügbar',
    deleteConfirm: 'Produkt löschen',
    deleteWarning: 'Sind Sie sicher, dass Sie dieses Produkt löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.',
    productName: 'Produktname',
    enterProductName: 'Produktname eingeben',
    enterDescription: 'Produktbeschreibung eingeben',
    selectCategory: 'Kategorie auswählen',
    selectSupplier: 'Lieferant auswählen',
    imageUrlPlaceholder: 'https://beispiel.de/bild.jpg',
    createNew: 'Neues Produkt erstellen',
    addNewProduct: 'Fügen Sie ein neues Produkt zu Ihrem Katalog hinzu',
    editProduct: 'Produkt bearbeiten',
    updateProductInfo: 'Produktinformationen aktualisieren',
    view: 'Ansehen'
  },
  cart: {
    title: 'Ihr Warenkorb',
    reviewItems: 'Überprüfen Sie die Artikel vor der Bestellung.',
    orderSummary: 'Bestellübersicht',
    placeOrder: 'Bestellung aufgeben',
    clearCart: 'Warenkorb leeren',
    empty: 'Ihr Warenkorb ist leer.',
    browseProducts: 'Produkte durchsuchen',
    subtotal: 'Zwischensumme',
    items: 'Artikel',
    itemCount: '{{count}} Artikel',
    addToCart: 'In den Warenkorb',
    remove: 'Entfernen',
    decreaseQuantity: 'Menge verringern',
    increaseQuantity: 'Menge erhöhen'
  },
  address: {
    title: 'Lieferadresse',
    country: 'Land',
    countryPlaceholder: 'z.B. Deutschland',
    county: 'Bundesland',
    countyPlaceholder: 'z.B. Bayern',
    city: 'Stadt',
    cityPlaceholder: 'z.B. München',
    streetAddress: 'Straßenadresse',
    streetPlaceholder: 'z.B. Hauptstraße 123, Wohnung 4B'
  },
  orders: {
    title: 'Ihre Bestellungen',
    trackPurchases: 'Verfolgen Sie Ihre letzten Einkäufe.',
    orderDetails: 'Bestelldetails',
    createdAt: 'Bestellt am',
    shippedFrom: 'Versandt von',
    noOrders: 'Sie haben noch keine Bestellungen.',
    backToOrders: 'Zurück zu Bestellungen'
  },
  validation: {
    required: 'Dieses Feld ist erforderlich',
    email: 'Bitte geben Sie eine gültige E-Mail-Adresse ein',
    minLength: 'Mindestlänge beträgt {{length}} Zeichen',
    maxLength: 'Maximale Länge beträgt {{length}} Zeichen',
    min: 'Mindestwert ist {{min}}',
    max: 'Maximalwert ist {{max}}',
    pattern: 'Bitte geben Sie ein gültiges Format ein',
    url: 'Bitte geben Sie eine gültige URL ein'
  },
  notifications: {
    loginSuccess: 'Erfolgreich angemeldet!',
    logoutSuccess: 'Erfolgreich abgemeldet',
    registrationSuccess: 'Konto erfolgreich erstellt',
    addedToCart: 'Zum Warenkorb hinzugefügt',
    productCreated: 'Produkt erfolgreich erstellt',
    productUpdated: 'Produkt erfolgreich aktualisiert',
    productDeleted: 'Produkt erfolgreich gelöscht',
    orderPlaced: 'Bestellung erfolgreich aufgegeben!',
    cartCleared: 'Warenkorb geleert',
    genericError: 'Etwas ist schiefgelaufen. Bitte versuchen Sie es erneut.'
  }
};
