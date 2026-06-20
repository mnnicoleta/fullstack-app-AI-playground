/**
 * Represents a product supplier with contact information.
 * Used in product responses and supplier management operations.
 */
export type SupplierDto = {
  id: string;
  name: string;
  description: string;
  contactEmail: string;
  contactPhone: string;
  address: string;
};
