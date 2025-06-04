
import React from 'react';
import './ValidatedInput.css';

const ValidatedTextarea = ({
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
                               rows = 4,
                               maxLength = null,
                               showCharCount = false,
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

    const inputId = `textarea-${name}`;
    const hasError = error && error.trim() !== '';
    const charCount = value ? value.length : 0;
    const isNearLimit = maxLength && charCount > maxLength * 0.8;
    const isOverLimit = maxLength && charCount > maxLength;

    return (
        <div className={`validated-input-group ${className}`}>
            {label && (
                <label htmlFor={inputId} className="validated-input-label">
                    {label}
                    {required && <span className="required-indicator">*</span>}
                </label>
            )}

            <textarea
                id={inputId}
                name={name}
                value={value || ''}
                onChange={handleChange}
                onBlur={handleBlur}
                placeholder={placeholder}
                disabled={disabled}
                rows={rows}
                maxLength={maxLength}
                className={`validated-input ${hasError ? 'error' : ''} ${disabled ? 'disabled' : ''}`}
                aria-invalid={hasError}
                aria-describedby={hasError ? `${inputId}-error` : undefined}
                {...props}
            />

            {showCharCount && maxLength && (
                <div className={`char-count ${isNearLimit ? 'warning' : ''} ${isOverLimit ? 'error' : ''}`}>
                    {charCount}/{maxLength} caractere
                </div>
            )}

            {hasError && (
                <div id={`${inputId}-error`} className="validated-input-error" role="alert">
                    <span className="error-icon">⚠️</span>
                    {error}
                </div>
            )}
        </div>
    );
};

export default ValidatedTextarea;