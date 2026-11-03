/**
 * CraveBite Universal Real-Time Form & Phone Number Validator
 * Automatically applies to all pages and forms across the CraveBite platform.
 */

(function initCraveBiteValidator() {
    function setupValidation() {
        // 1. Setup Phone Number Inputs
        setupPhoneInputs();

        // 2. Setup Email Inputs
        setupEmailInputs();

        // 3. Setup Password Inputs
        setupPasswordInputs();

        // 4. Setup Card Number & CVV Inputs
        setupCardInputs();

        // 5. Setup Form Submissions
        setupAllForms();
    }

    // Helper: Add or get validation message container
    function getMsgElement(input) {
        let msg = input.parentElement.querySelector('.crave-validation-msg');
        if (!msg) {
            msg = document.createElement('span');
            msg.className = 'crave-validation-msg text-[11px] font-bold mt-1 block transition-all';
            // Insert after input or after input's container
            if (input.nextSibling) {
                input.parentNode.insertBefore(msg, input.nextSibling);
            } else {
                input.parentNode.appendChild(msg);
            }
        }
        return msg;
    }

    function setValid(input, text = '✓ Valid') {
        input.classList.remove('border-red-500', 'ring-1', 'ring-red-500', 'border-gray-200', 'bg-red-50/20');
        input.classList.add('border-green-500', 'ring-1', 'ring-green-500');
        const msg = getMsgElement(input);
        msg.className = 'crave-validation-msg text-[11px] font-bold text-green-600 mt-1 block';
        msg.innerText = text;
    }

    function setInvalid(input, text) {
        input.classList.remove('border-green-500', 'ring-1', 'ring-green-500', 'border-gray-200');
        input.classList.add('border-red-500', 'ring-1', 'ring-red-500', 'bg-red-50/20');
        const msg = getMsgElement(input);
        msg.className = 'crave-validation-msg text-[11px] font-bold text-red-500 mt-1 block';
        msg.innerText = text;
    }

    function clearValidation(input) {
        input.classList.remove('border-green-500', 'border-red-500', 'ring-1', 'ring-green-500', 'ring-red-500', 'bg-red-50/20');
        input.classList.add('border-gray-200');
        const msg = input.parentElement.querySelector('.crave-validation-msg');
        if (msg) msg.innerText = '';
    }

    // ==========================================
    // 1. PHONE NUMBER VALIDATION & TEXT BLOCKING
    // ==========================================
    function isPhoneField(input) {
        if (!input || input.tagName !== 'INPUT') return false;
        const type = (input.getAttribute('type') || '').toLowerCase();
        const id = (input.id || '').toLowerCase();
        const name = (input.name || '').toLowerCase();
        const placeholder = (input.placeholder || '').toLowerCase();

        return type === 'tel' ||
               id.includes('phone') ||
               name.includes('phone') ||
               placeholder.includes('077') ||
               placeholder.includes('071') ||
               placeholder.includes('phone');
    }

    function setupPhoneInputs() {
        const inputs = document.querySelectorAll('input');
        inputs.forEach(input => {
            if (!isPhoneField(input) || input.dataset.phoneBound) return;
            input.dataset.phoneBound = 'true';
            input.setAttribute('inputmode', 'numeric');
            input.setAttribute('maxlength', '10');

            // BLOCK non-digit text entry immediately on keydown
            input.addEventListener('keydown', function(e) {
                const allowedKeys = [
                    'Backspace', 'Tab', 'Enter', 'Escape', 'Delete',
                    'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown',
                    'Home', 'End'
                ];

                // Allow shortcuts (Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X)
                if (e.ctrlKey || e.metaKey) return;

                if (allowedKeys.includes(e.key)) return;

                // Only allow numbers 0-9
                if (!/^[0-9]$/.test(e.key)) {
                    e.preventDefault();
                    setInvalid(input, '⚠️ Only numbers (0-9) are allowed in phone field.');
                    setTimeout(() => {
                        validatePhoneValue(input);
                    }, 1200);
                    return;
                }

                // If already 10 digits and not replacing a selection, block further digits
                const selectedText = input.value.substring(input.selectionStart, input.selectionEnd);
                if (input.value.replace(/\D/g, '').length >= 10 && !selectedText) {
                    e.preventDefault();
                }
            });

            // Strip any non-digit character on paste or input
            input.addEventListener('input', function() {
                const cleaned = input.value.replace(/\D/g, '').slice(0, 10);
                if (input.value !== cleaned) {
                    input.value = cleaned;
                }
                validatePhoneValue(input);
            });

            input.addEventListener('blur', function() {
                validatePhoneValue(input, true);
            });
        });
    }

    function validatePhoneValue(input, isBlur = false) {
        const val = input.value.trim();
        const required = input.hasAttribute('required');

        if (!val) {
            if (required && isBlur) {
                setInvalid(input, 'Phone number is required.');
            } else {
                clearValidation(input);
            }
            return false;
        }

        if (val.length < 10) {
            setInvalid(input, `Phone number must be 10 digits (currently ${val.length}/10).`);
            return false;
        }

        if (!/^0\d{9}$/.test(val)) {
            setInvalid(input, 'Must be a valid 10-digit number starting with 0 (e.g. 0712345678).');
            return false;
        }

        setValid(input, '✓ Valid 10-digit phone number');
        return true;
    }

    // ==========================================
    // 2. EMAIL VALIDATION
    // ==========================================
    function setupEmailInputs() {
        const emailInputs = document.querySelectorAll('input[type="email"], input[id*="email"], input[name*="email"]');
        emailInputs.forEach(input => {
            if (input.dataset.emailBound) return;
            input.dataset.emailBound = 'true';

            input.addEventListener('input', () => validateEmail(input));
            input.addEventListener('blur', () => validateEmail(input, true));
        });
    }

    function validateEmail(input, isBlur = false) {
        const val = input.value.trim();
        const required = input.hasAttribute('required');

        if (!val) {
            if (required && isBlur) {
                setInvalid(input, 'Email address is required.');
            } else {
                clearValidation(input);
            }
            return false;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
        if (!emailRegex.test(val)) {
            setInvalid(input, 'Please enter a valid email format (e.g. name@example.com).');
            return false;
        }

        setValid(input, '✓ Valid email format');
        return true;
    }

    // ==========================================
    // 3. PASSWORD VALIDATION
    // ==========================================
    function setupPasswordInputs() {
        const passInputs = document.querySelectorAll('input[type="password"]');
        passInputs.forEach(input => {
            // Skip CVV fields
            if (input.id.includes('cvv') || input.name.includes('cvv')) return;
            if (input.dataset.passBound) return;
            input.dataset.passBound = 'true';

            input.addEventListener('input', () => validatePassword(input));
            input.addEventListener('blur', () => validatePassword(input, true));
        });
    }

    function validatePassword(input, isBlur = false) {
        const val = input.value;
        const required = input.hasAttribute('required');

        if (!val) {
            if (required && isBlur) {
                setInvalid(input, 'Password is required.');
            } else {
                clearValidation(input);
            }
            return false;
        }

        if (val.length < 6) {
            setInvalid(input, `Password must be at least 6 characters (${val.length}/6).`);
            return false;
        }

        setValid(input, '✓ Strong password length');
        return true;
    }

    // ==========================================
    // 4. PAYMENT CARD NUMBER & CVV VALIDATION
    // ==========================================
    function setupCardInputs() {
        // Card Number
        const cardNumInputs = document.querySelectorAll('#card-number, #cardNumber, input[placeholder*="4111"]');
        cardNumInputs.forEach(input => {
            if (input.dataset.cardNumBound) return;
            input.dataset.cardNumBound = 'true';
            input.setAttribute('maxlength', '19');

            input.addEventListener('keydown', function(e) {
                if (['Backspace', 'Tab', 'Enter', 'Escape', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(e.key)) return;
                if (e.ctrlKey || e.metaKey) return;
                if (!/^[0-9]$/.test(e.key)) {
                    e.preventDefault();
                }
            });

            input.addEventListener('input', function() {
                let v = input.value.replace(/\D/g, '').slice(0, 16);
                let formatted = v.match(/.{1,4}/g)?.join(' ') || v;
                input.value = formatted;

                if (v.length === 16) {
                    setValid(input, '✓ Valid 16-digit card number');
                } else if (v.length > 0) {
                    setInvalid(input, `Card must have 16 digits (${v.length}/16).`);
                } else {
                    clearValidation(input);
                }
            });
        });

        // CVV
        const cvvInputs = document.querySelectorAll('#card-cvv, #cvv, input[name="cvv"]');
        cvvInputs.forEach(input => {
            if (input.dataset.cvvBound) return;
            input.dataset.cvvBound = 'true';
            input.setAttribute('maxlength', '4');

            input.addEventListener('keydown', function(e) {
                if (['Backspace', 'Tab', 'Enter', 'Escape', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(e.key)) return;
                if (e.ctrlKey || e.metaKey) return;
                if (!/^[0-9]$/.test(e.key)) {
                    e.preventDefault();
                }
            });

            input.addEventListener('input', function() {
                let v = input.value.replace(/\D/g, '').slice(0, 4);
                input.value = v;
                if (v.length >= 3 && v.length <= 4) {
                    setValid(input, '✓ Valid CVV');
                } else if (v.length > 0) {
                    setInvalid(input, 'CVV must be 3 or 4 digits.');
                } else {
                    clearValidation(input);
                }
            });
        });

        // Expiry Date (MM/YY)
        const expInputs = document.querySelectorAll('#card-expiryDate, #expiryDate, input[placeholder="MM/YY"]');
        expInputs.forEach(input => {
            if (input.dataset.expBound) return;
            input.dataset.expBound = 'true';
            input.setAttribute('maxlength', '5');

            input.addEventListener('keydown', function(e) {
                if (['Backspace', 'Tab', 'Enter', 'Escape', 'Delete', 'ArrowLeft', 'ArrowRight'].includes(e.key)) return;
                if (e.ctrlKey || e.metaKey) return;
                if (!/^[0-9]$/.test(e.key)) {
                    e.preventDefault();
                }
            });

            input.addEventListener('input', function() {
                let v = input.value.replace(/\D/g, '').slice(0, 4);
                if (v.length >= 2) {
                    let month = parseInt(v.slice(0, 2), 10);
                    if (month > 12) v = '12' + v.slice(2);
                    if (month === 0) v = '01' + v.slice(2);
                    v = v.slice(0, 2) + '/' + v.slice(2);
                }
                input.value = v;

                if (v.length === 5) {
                    setValid(input, '✓ Valid MM/YY');
                } else if (v.length > 0) {
                    setInvalid(input, 'Expiry format must be MM/YY.');
                } else {
                    clearValidation(input);
                }
            });
        });
    }

    // ==========================================
    // 5. GLOBAL FORM SUBMIT INTERCEPTION
    // ==========================================
    function setupAllForms() {
        document.querySelectorAll('form').forEach(form => {
            if (form.dataset.validatorAttached) return;
            form.dataset.validatorAttached = 'true';

            form.addEventListener('submit', function(e) {
                let hasError = false;
                let firstErrorElem = null;

                // Validate phone fields in form
                form.querySelectorAll('input').forEach(input => {
                    if (isPhoneField(input)) {
                        const val = input.value.trim();
                        if (val || input.hasAttribute('required')) {
                            if (!validatePhoneValue(input, true)) {
                                hasError = true;
                                if (!firstErrorElem) firstErrorElem = input;
                            }
                        }
                    } else if (input.type === 'email' || input.id.includes('email') || input.name.includes('email')) {
                        const val = input.value.trim();
                        if (val || input.hasAttribute('required')) {
                            if (!validateEmail(input, true)) {
                                hasError = true;
                                if (!firstErrorElem) firstErrorElem = input;
                            }
                        }
                    } else if (input.type === 'password' && !input.id.includes('cvv')) {
                        const val = input.value;
                        if (val || input.hasAttribute('required')) {
                            if (!validatePassword(input, true)) {
                                hasError = true;
                                if (!firstErrorElem) firstErrorElem = input;
                            }
                        }
                    }
                });

                if (hasError) {
                    e.preventDefault();
                    e.stopPropagation();
                    if (firstErrorElem) {
                        firstErrorElem.focus();
                        firstErrorElem.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }
                }
            }, true);
        });
    }

    // Run on load and observe DOM for dynamically added modals
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', setupValidation);
    } else {
        setupValidation();
    }

    // MutationObserver to attach validation to dynamically opened modals
    const observer = new MutationObserver(() => {
        setupValidation();
    });
    observer.observe(document.body, { childList: true, subtree: true });

    // Expose helpers globally
    window.CraveValidator = {
        setupValidation,
        validatePhoneValue,
        validateEmail,
        validatePassword,
        setValid,
        setInvalid,
        clearValidation
    };
})();
