import { SupplierDto } from '../../types/dtos/supplier.dto';

/**
 * Mock supplier data for development and testing.
 * Matches seed data from backend V2.1__populate_supplier_mock_data.sql
 */
export const MOCK_SUPPLIERS: SupplierDto[] = [
    {
        id: '50771e01-0000-0000-0000-000000000001',
        name: 'TechVision Electronics',
        description: 'Leading supplier of consumer electronics and computer hardware',
        contactEmail: 'contact@techvision.com',
        contactPhone: '+1-555-0101',
        address: '123 Tech Street, San Francisco, CA 94105'
    },
    {
        id: '50771e01-0000-0000-0000-000000000002',
        name: 'GlobalThreads Textiles',
        description: 'International textile and apparel wholesale distributor',
        contactEmail: 'sales@globalthreads.com',
        contactPhone: '+1-555-0202',
        address: '456 Fashion Avenue, New York, NY 10001'
    },
    {
        id: '50771e01-0000-0000-0000-000000000003',
        name: 'HomeStyle Distributors',
        description: 'Home goods, furniture, and garden supplies distributor',
        contactEmail: 'info@homestyle.com',
        contactPhone: '+1-555-0303',
        address: '789 Garden Lane, Portland, OR 97205'
    },
    {
        id: '50771e01-0000-0000-0000-000000000004',
        name: 'ActiveGear Sports',
        description: 'Sports equipment and athletic apparel supplier',
        contactEmail: 'support@activegear.com',
        contactPhone: '+1-555-0404',
        address: '321 Sports Drive, Denver, CO 80202'
    }
];

// Helper to get supplier by category (for realistic mock data)
export function getSupplierByCategory(categoryName: string): SupplierDto {
    const categoryMap: Record<string, SupplierDto> = {
        'Electronics': MOCK_SUPPLIERS[0], // TechVision Electronics
        'Clothing': MOCK_SUPPLIERS[1],    // GlobalThreads Textiles
        'Home & Garden': MOCK_SUPPLIERS[2], // HomeStyle Distributors
        'Sports': MOCK_SUPPLIERS[3]        // ActiveGear Sports
    };
    return categoryMap[categoryName] || MOCK_SUPPLIERS[0]; // Default to TechVision
}
