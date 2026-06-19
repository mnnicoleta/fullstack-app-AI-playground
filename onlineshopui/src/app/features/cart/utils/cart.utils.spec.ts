import { describe, it, expect } from 'vitest';
import { toCreateOrderDto } from './cart.utils';
import { CartItem } from '../types/cart-item.type';
import { AddressDto } from '../../../core/types/dtos/location.dto';

describe('cart.utils', () => {
    describe('toCreateOrderDto', () => {
        it('should return null when cart is empty', () => {
            const address: AddressDto = {
                country: 'Romania',
                city: 'Cluj-Napoca',
                county: 'Cluj',
                streetAddress: '123 Main Street'
            };

            const result = toCreateOrderDto([], address);

            expect(result).toBeNull();
        });

        it('should create order DTO with items and address', () => {
            const cartItems: CartItem[] = [
                { productId: 'product-1', quantity: 2 },
                { productId: 'product-2', quantity: 1 }
            ];

            const address: AddressDto = {
                country: 'Romania',
                city: 'Cluj-Napoca',
                county: 'Cluj',
                streetAddress: '123 Main Street, Apt 4B'
            };

            const result = toCreateOrderDto(cartItems, address);

            expect(result).not.toBeNull();
            expect(result?.items).toHaveLength(2);
            expect(result?.items[0].productId).toBe('product-1');
            expect(result?.items[0].quantity).toBe(2);
            expect(result?.items[1].productId).toBe('product-2');
            expect(result?.items[1].quantity).toBe(1);
            expect(result?.address).toEqual(address);
        });

        it('should include address with all required fields', () => {
            const cartItems: CartItem[] = [
                { productId: 'product-1', quantity: 1 }
            ];

            const address: AddressDto = {
                country: 'USA',
                city: 'New York',
                county: 'Manhattan',
                streetAddress: '456 Broadway, Suite 100'
            };

            const result = toCreateOrderDto(cartItems, address);

            expect(result?.address.country).toBe('USA');
            expect(result?.address.city).toBe('New York');
            expect(result?.address.county).toBe('Manhattan');
            expect(result?.address.streetAddress).toBe('456 Broadway, Suite 100');
        });

        it('should map all cart items correctly', () => {
            const cartItems: CartItem[] = [
                { productId: 'laptop-123', quantity: 3 },
                { productId: 'mouse-456', quantity: 5 },
                { productId: 'keyboard-789', quantity: 1 }
            ];

            const address: AddressDto = {
                country: 'Romania',
                city: 'Bucharest',
                county: 'Ilfov',
                streetAddress: '789 Main Boulevard'
            };

            const result = toCreateOrderDto(cartItems, address);

            expect(result?.items).toHaveLength(3);
            expect(result?.items[0].productId).toBe('laptop-123');
            expect(result?.items[0].quantity).toBe(3);
            expect(result?.items[1].productId).toBe('mouse-456');
            expect(result?.items[1].quantity).toBe(5);
            expect(result?.items[2].productId).toBe('keyboard-789');
            expect(result?.items[2].quantity).toBe(1);
        });
    });
});
