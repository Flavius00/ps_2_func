// frontend/src/hooks/useFormValidation.js
// Custom hook pentru gestionarea validării formularelor

import { useState, useCallback, useMemo } from 'react';
import { validateForm, validateFieldOnChange } from '../utils/validation';

export const useFormValidation = (initialValues, validationRules) => {
    const [values, setValues] = useState(initialValues);
    const [errors, setErrors] = useState({});
    const [touched, setTouched] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    // FIXED: Memorizăm validationRules pentru a evita re-crearea la fiecare render
    const memoizedValidationRules = useMemo(() => validationRules, []);

    // Actualizează valoarea unui câmp și validează-l
    const handleChange = useCallback((name, value) => {
        setValues(prev => ({
            ...prev,
            [name]: value
        }));

        // Validează câmpul în timp real doar dacă a fost atins
        setTouched(prevTouched => {
            if (prevTouched[name]) {
                const result = validateFieldOnChange(name, value, memoizedValidationRules);
                setErrors(prevErrors => ({
                    ...prevErrors,
                    [name]: result.isValid ? '' : result.message
                }));
            }
            return prevTouched;
        });
    }, [memoizedValidationRules]);

    // Marchează câmpul ca fiind atins (pentru validare în timp real)
    const handleBlur = useCallback((name) => {
        setTouched(prev => ({
            ...prev,
            [name]: true
        }));

        // Validează câmpul la blur
        setValues(currentValues => {
            const result = validateFieldOnChange(name, currentValues[name], memoizedValidationRules);
            setErrors(prevErrors => ({
                ...prevErrors,
                [name]: result.isValid ? '' : result.message
            }));
            return currentValues;
        });
    }, [memoizedValidationRules]);

    // Validează întregul formular
    const validateAllFields = useCallback(() => {
        let isValid = true;

        setValues(currentValues => {
            const result = validateForm(currentValues, memoizedValidationRules);
            setErrors(result.errors);

            // Marchează toate câmpurile ca fiind atinse
            const allTouched = Object.keys(memoizedValidationRules).reduce((acc, field) => {
                acc[field] = true;
                return acc;
            }, {});
            setTouched(allTouched);

            isValid = result.isValid;
            return currentValues;
        });

        return isValid;
    }, [memoizedValidationRules]);

    // Resetează formularul
    const resetForm = useCallback(() => {
        setValues(initialValues);
        setErrors({});
        setTouched({});
        setIsSubmitting(false);
    }, []); // FIXED: Removed initialValues from dependencies

    // Setează valorile formularului (pentru editare)
    const setFormValues = useCallback((newValues) => {
        setValues(newValues);
        setErrors({});
        setTouched({});
    }, []);

    // Returnează true dacă formularul este valid
    const isFormValid = useCallback(() => {
        const result = validateForm(values, memoizedValidationRules);
        return result.isValid;
    }, [values, memoizedValidationRules]);

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

    // FIXED: Memorizăm funcția de validare
    const memoizedValidationFn = useCallback(validationFn, []);

    const handleChange = useCallback((newValue) => {
        setValue(newValue);

        if (touched && memoizedValidationFn) {
            const result = memoizedValidationFn(newValue);
            setError(result.isValid ? '' : result.message);
        }
    }, [touched, memoizedValidationFn]);

    const handleBlur = useCallback(() => {
        setTouched(true);

        setValue(currentValue => {
            if (memoizedValidationFn) {
                const result = memoizedValidationFn(currentValue);
                setError(result.isValid ? '' : result.message);
            }
            return currentValue;
        });
    }, [memoizedValidationFn]);

    const validate = useCallback(() => {
        setTouched(true);

        let isValid = true;
        setValue(currentValue => {
            if (memoizedValidationFn) {
                const result = memoizedValidationFn(currentValue);
                setError(result.isValid ? '' : result.message);
                isValid = result.isValid;
            }
            return currentValue;
        });

        return isValid;
    }, [memoizedValidationFn]);

    const reset = useCallback(() => {
        setValue(initialValue);
        setError('');
        setTouched(false);
    }, []); // FIXED: Removed initialValue from dependencies

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