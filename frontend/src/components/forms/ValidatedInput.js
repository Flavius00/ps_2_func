
import React from 'react';
import './ValidatedInput.css';

const ValidatedInput = ({
                            type = 'text',
                            name,
                            value,
                            onChange,
                            onBlur,
                            error,
                            label,
                            placeholder,
                            required = false,
                            disabled = false,
                            className = '',
                            ...props
                        }) => {
    const handleChange = (e) => {
        const newValue = type === 'number' ?
            (e.target.value === '' ? '' : parseFloat(e.target.value) || 0) :
            e.target.value;
        onChange(name, newValue);
    };

    const handleBlur = () => {
        if (onBlur) {
            onBlur(name);
        }
    };

    const inputId = `input-${name}`;
    const hasError = error && error.trim() !== '';

    return (
        <div className={`validated-input-group ${className}`}>
            {label && (
                <label htmlFor={inputId} className="validated-input-label">
                    {label}
                    {required && <span className="required-indicator">*</span>}
                </label>
            )}

            <input
                id={inputId}
                type={type}
                name={name}
                value={value || ''}
                onChange={handleChange}
                onBlur={handleBlur}
                placeholder={placeholder}
                disabled={disabled}
                className={`validated-input ${hasError ? 'error' : ''} ${disabled ? 'disabled' : ''}`}
                aria-invalid={hasError}
                aria-describedby={hasError ? `${inputId}-error` : undefined}
                {...props}
            />

            {hasError && (
                <div id={`${inputId}-error`} className="validated-input-error" role="alert">
                    <span className="error-icon">⚠️</span>
                    {error}
                </div>
            )}
        </div>
    );
};

export default ValidatedInput;