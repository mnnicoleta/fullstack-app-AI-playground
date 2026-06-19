import { describe, it, expect } from 'vitest';
import { createAddressForm } from './address-form.utils';
import { AddressDto } from '../../../core/types/dtos/location.dto';

describe('address-form.utils', () => {
    describe('createAddressForm', () => {
        it('should create form with empty values when no address provided', () => {
            const form = createAddressForm();

            expect(form.value.country).toBe('');
            expect(form.value.city).toBe('');
            expect(form.value.county).toBe('');
            expect(form.value.streetAddress).toBe('');
        });

        it('should create form with pre-populated values when address provided', () => {
            const address: AddressDto = {
                country: 'Romania',
                city: 'Cluj-Napoca',
                county: 'Cluj',
                streetAddress: '123 Main Street'
            };

            const form = createAddressForm(address);

            expect(form.value.country).toBe('Romania');
            expect(form.value.city).toBe('Cluj-Napoca');
            expect(form.value.county).toBe('Cluj');
            expect(form.value.streetAddress).toBe('123 Main Street');
        });

        it('should mark all fields as required', () => {
            const form = createAddressForm();

            expect(form.controls.country.hasError('required')).toBe(true);
            expect(form.controls.city.hasError('required')).toBe(true);
            expect(form.controls.county.hasError('required')).toBe(true);
            expect(form.controls.streetAddress.hasError('required')).toBe(true);
        });

        it('should validate minLength for country (2 characters)', () => {
            const form = createAddressForm();

            form.controls.country.setValue('A');
            expect(form.controls.country.hasError('minlength')).toBe(true);

            form.controls.country.setValue('AB');
            expect(form.controls.country.hasError('minlength')).toBe(false);
        });

        it('should validate minLength for city (2 characters)', () => {
            const form = createAddressForm();

            form.controls.city.setValue('C');
            expect(form.controls.city.hasError('minlength')).toBe(true);

            form.controls.city.setValue('CJ');
            expect(form.controls.city.hasError('minlength')).toBe(false);
        });

        it('should validate minLength for county (2 characters)', () => {
            const form = createAddressForm();

            form.controls.county.setValue('C');
            expect(form.controls.county.hasError('minlength')).toBe(true);

            form.controls.county.setValue('CL');
            expect(form.controls.county.hasError('minlength')).toBe(false);
        });

        it('should validate minLength for streetAddress (5 characters)', () => {
            const form = createAddressForm();

            form.controls.streetAddress.setValue('1234');
            expect(form.controls.streetAddress.hasError('minlength')).toBe(true);

            form.controls.streetAddress.setValue('12345');
            expect(form.controls.streetAddress.hasError('minlength')).toBe(false);
        });

        it('should be invalid when all fields are empty', () => {
            const form = createAddressForm();

            expect(form.valid).toBe(false);
        });

        it('should be valid when all fields are filled correctly', () => {
            const form = createAddressForm();

            form.controls.country.setValue('Romania');
            form.controls.city.setValue('Cluj-Napoca');
            form.controls.county.setValue('Cluj');
            form.controls.streetAddress.setValue('123 Main Street, Apt 4B');

            expect(form.valid).toBe(true);
        });

        it('should be invalid when one field is missing', () => {
            const form = createAddressForm();

            form.controls.country.setValue('Romania');
            form.controls.city.setValue('Cluj-Napoca');
            form.controls.county.setValue('Cluj');
            // streetAddress not set

            expect(form.valid).toBe(false);
        });

        it('should be invalid when field values are too short', () => {
            const form = createAddressForm();

            form.controls.country.setValue('R');  // Too short
            form.controls.city.setValue('Cluj-Napoca');
            form.controls.county.setValue('Cluj');
            form.controls.streetAddress.setValue('123 Main Street');

            expect(form.valid).toBe(false);
        });
    });
});
