import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AddressDto } from '../../../core/types/dtos/location.dto';

export type AddressFormGroup = FormGroup<{
    country: FormControl<string>;
    city: FormControl<string>;
    county: FormControl<string>;
    streetAddress: FormControl<string>;
}>;

export function createAddressForm(address?: AddressDto): AddressFormGroup {
    return new FormGroup({
        country: new FormControl<string>(address?.country ?? '', {
            nonNullable: true,
            validators: [Validators.required, Validators.minLength(2)]
        }),
        city: new FormControl<string>(address?.city ?? '', {
            nonNullable: true,
            validators: [Validators.required, Validators.minLength(2)]
        }),
        county: new FormControl<string>(address?.county ?? '', {
            nonNullable: true,
            validators: [Validators.required, Validators.minLength(2)]
        }),
        streetAddress: new FormControl<string>(address?.streetAddress ?? '', {
            nonNullable: true,
            validators: [Validators.required, Validators.minLength(5)]
        })
    });
}
