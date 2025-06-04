
import React from 'react';
import './ValidatedInput.css';

const ValidatedSelect = ({
                             name,
                             value,
                             onChange,
                             onBlur,
                             error,
                             label,
                             required = false,
                             disabled = false,
                             className = '',
                             options = [],
                             placeholder = 'Selectează o opțiune...',
                             ...props
                         }) => {
    const handleChange = (e) => {
        onChange(name, e.target.value);
    };

    const handleBlur = () => {
        if (onBlur) {
            onBlur(name);
        }
    };

    const inputId = `select-${name}`;
    const hasError = error && error.trim() !== '';

    return (
        <div className={`validated-input-group ${className}`}>
            {label && (
                <label htmlFor={inputId} className="validated-input-label">
                    {label}
                    {required && <span className="required-indicator">*</span>}
                </label>
            )}

            <select
                id={inputId}
                name={name}
                value={value || ''}
                onChange={handleChange}
                onBlur={handleBlur}
                disabled={disabled}
                className={`validated-input ${hasError ? 'error' : ''} ${disabled ? 'disabled' : ''}`}
                aria-invalid={hasError}
                aria-describedby={hasError ? `${inputId}-error` : undefined}
                {...props}
            >
                {placeholder && (
                    <option value="" disabled>
                        {placeholder}
                    </option>
                )}
                {options.map((option, index) => (
                    <option key={index} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>

            {hasError && (
                <div id={`${inputId}-error`} className="validated-input-error" role="alert">
                    <span className="error-icon">⚠️</span>
                    {error}
                </div>
            )}
        </div>
    );
};

export default ValidatedSelect;