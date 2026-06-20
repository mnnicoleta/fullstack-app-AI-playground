import { SupplierDto } from './supplier.dto';

export type ProductCategoryDto = {
    id: string;
    name: string;
    description: string;
};

/**
 * Complete product DTO returned by API endpoints.
 * Includes nested category and supplier objects.
 */
export type ProductDto = {
    id: string;
    name: string;
    description: string;
    price: number;
    weight: number;
    category: ProductCategoryDto;
    supplier: SupplierDto;  // Required supplier relationship
    imageUrl: string;
};

/**
 * Product creation request DTO.
 * Uses IDs for category and supplier references (lazy loading).
 */
export type CreateProductRequest = Omit<ProductDto, 'id' | 'category' | 'supplier'> & {
    categoryId: string;
    supplierId: string;  // Required supplier ID
};

export type UpdateProductRequest = Partial<ProductDto> & {
    categoryId?: string;
    supplierId?: string;  // Optional supplier ID
};
