import { FormControl, FormGroup } from '@angular/forms';

/**
 * Form controls for product creation and editing.
 * All fields are required including supplier selection.
 */
export type ProductFormControls = {
    name: FormControl<string>;
    description: FormControl<string>;
    price: FormControl<number>;
    weight: FormControl<number>;
    categoryId: FormControl<string>;
    supplierId: FormControl<string>;  // Added in V2 for supplier support
    imageUrl: FormControl<string>;
};

export type ProductFormGroup = FormGroup<ProductFormControls>;
