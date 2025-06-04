// frontend/src/hooks/useFormValidation.js
// Custom hook pentru gestionarea validării formularelor

import { useState, useCallback } from 'react';
import { validateForm, validateFieldOnChange } from '../utils/validation';

export const useFormValidation = (initialValues, validationRules) => {
    const [values, setValues] = useState(initialValues);
    const [errors, setErrors] = useState({});
    const [touched, setTouched] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Actualizează valoarea unui câmp și validează-l
    const handleChange = useCallback((name, value) => {
        setValues(prev => ({
            ...prev,
            [name]: value
        }));

        // Validează câmpul în timp real doar dacă a fost atins
        if (touched[name]) {
            const result = validateFieldOnChange(name, value, validationRules);
            setErrors(prev => ({
                ...prev,
                [name]: result.isValid ? '' : result.message
            }));
        }
    }, [validationRules, touched]);

    // Marchează câmpul ca fiind atins (pentru validare în timp real)
    const handleBlur = useCallback((name) => {
        setTouched(prev => ({
            ...prev,
            [name]: true
        }));

        // Validează câmpul la blur
        const result = validateFieldOnChange(name, values[name], validationRules);
        setErrors(prev => ({
            ...prev,
            [name]: result.isValid ? '' : result.message
        }));
    }, [values, validationRules]);

    // Validează întregul formular
    const validateAllFields = useCallback(() => {
        const result = validateForm(values, validationRules);
        setErrors(result.errors);

        // Marchează toate câmpurile ca fiind atinse
        const allTouched = Object.keys(validationRules).reduce((acc, field) => {
            acc[field] = true;
            return acc;
        }, {});
        setTouched(allTouched);

        return result.isValid;
    }, [values, validationRules]);

    // Resetează formularul
    const resetForm = useCallback(() => {
        setValues(initialValues);
        setErrors({});
        setTouched({});
        setIsSubmitting(false);
    }, [initialValues]);

    // Setează valorile formularului (pentru editare)
    const setFormValues = useCallback((newValues) => {
        setValues(newValues);
        setErrors({});
        setTouched({});
    }, []);

    // Returnează true dacă formularul este valid
    const isFormValid = useCallback(() => {
        const result = validateForm(values, validationRules);
        return result.isValid;
    }, [values, validationRules]);

    // Setează starea de submit
    const setSubmitting = useCallback((submitting) => {
        setIsSubmitting(submitting);
    }, []);

    return {
        values,
        errors,
        touched,
        isSubmitting,
        handleChange,
        handleBlur,
        validateAllFields,
        resetForm,
        setFormValues,
        isFormValid,
        setSubmitting
    };
};

// Hook pentru validarea în timp real a unui singur câmp
export const useFieldValidation = (initialValue, validationFn) => {
    const [value, setValue] = useState(initialValue);
    const [error, setError] = useState('');
    const [touched, setTouched] = useState(false);

    const handleChange = useCallback((newValue) => {
        setValue(newValue);

        if (touched && validationFn) {
            const result = validationFn(newValue);
            setError(result.isValid ? '' : result.message);
        }
    }, [touched, validationFn]);

    const handleBlur = useCallback(() => {
        setTouched(true);

        if (validationFn) {
            const result = validationFn(value);
            setError(result.isValid ? '' : result.message);
        }
    }, [value, validationFn]);

    const validate = useCallback(() => {
        setTouched(true);

        if (validationFn) {
            const result = validationFn(value);
            setError(result.isValid ? '' : result.message);
            return result.isValid;
        }

        return true;
    }, [value, validationFn]);

    const reset = useCallback(() => {
        setValue(initialValue);
        setError('');
        setTouched(false);
    }, [initialValue]);

    return {
        value,
        error,
        touched,
        handleChange,
        handleBlur,
        validate,
        reset,
        isValid: !error && touched
    };
};