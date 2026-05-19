// validation.js - Validation côté client

// Tab switching function
function showTab(tabName) {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const tabs = document.querySelectorAll('.tab');

    if (tabName === 'login') {
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
        tabs[0].classList.add('active');
        tabs[1].classList.remove('active');
    } else {
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
        tabs[0].classList.remove('active');
        tabs[1].classList.add('active');
    }

    // Clear alerts when switching tabs
    hideAlert();
}

// Alert functions
function showAlert(message, type) {
    const alert = document.getElementById('alert');
    alert.textContent = message;
    alert.className = `alert alert-${type} show`;

    // Auto-hide after 5 seconds
    setTimeout(() => {
        hideAlert();
    }, 5000);
}

function hideAlert() {
    const alert = document.getElementById('alert');
    alert.classList.remove('show');
}

// Password strength checker
function checkPasswordStrength(password) {
    let strength = 0;

    if (password.length >= 8) strength++;
    if (password.match(/[a-z]+/)) strength++;
    if (password.match(/[A-Z]+/)) strength++;
    if (password.match(/[0-9]+/)) strength++;
    if (password.match(/[$@#&!]+/)) strength++;

    const strengthBar = document.getElementById('passwordStrength');
    strengthBar.className = 'password-strength-bar';

    if (strength <= 2) {
        strengthBar.classList.add('strength-weak');
        return 'Faible';
    } else if (strength <= 4) {
        strengthBar.classList.add('strength-medium');
        return 'Moyen';
    } else {
        strengthBar.classList.add('strength-strong');
        return 'Fort';
    }
}

// Real-time password strength display
document.addEventListener('DOMContentLoaded', function() {
    const passwordInput = document.getElementById('reg_password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            checkPasswordStrength(this.value);
        });
    }

    // Form validation
    const loginForm = document.getElementById('login');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            let isValid = true;
            const username = document.getElementById('login_username').value.trim();
            const password = document.getElementById('login_password').value.trim();

            if (!username) {
                showFieldError('login_username', 'Email ou login requis');
                isValid = false;
            } else {
                clearFieldError('login_username');
            }

            if (!password) {
                showFieldError('login_password', 'Mot de passe requis');
                isValid = false;
            } else {
                clearFieldError('login_password');
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    const registerForm = document.getElementById('register');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            let isValid = true;

            // Nom validation
            const nom = document.getElementById('reg_nom').value.trim();
            if (!nom) {
                showFieldError('reg_nom', 'Nom requis');
                isValid = false;
            } else if (nom.length < 2) {
                showFieldError('reg_nom', 'Nom trop court (min 2 caractères)');
                isValid = false;
            } else {
                clearFieldError('reg_nom');
            }

            // Prénoms validation
            const prenoms = document.getElementById('reg_prenoms').value.trim();
            if (!prenoms) {
                showFieldError('reg_prenoms', 'Prénoms requis');
                isValid = false;
            } else if (prenoms.length < 2) {
                showFieldError('reg_prenoms', 'Prénoms trop courts (min 2 caractères)');
                isValid = false;
            } else {
                clearFieldError('reg_prenoms');
            }

            // Email validation
            const email = document.getElementById('reg_email').value.trim();
            const emailRegex = /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/;
            if (!email) {
                showFieldError('reg_email', 'Email requis');
                isValid = false;
            } else if (!emailRegex.test(email)) {
                showFieldError('reg_email', 'Email invalide');
                isValid = false;
            } else {
                clearFieldError('reg_email');
            }

            // Contact validation
            const contact = document.getElementById('reg_contact').value.trim();
            if (!contact) {
                showFieldError('reg_contact', 'Téléphone requis');
                isValid = false;
            } else if (contact.length < 8) {
                showFieldError('reg_contact', 'Téléphone invalide');
                isValid = false;
            } else {
                clearFieldError('reg_contact');
            }

            // Login validation
            const login = document.getElementById('reg_login').value.trim();
            if (!login) {
                showFieldError('reg_login', 'Login requis');
                isValid = false;
            } else if (login.length < 3) {
                showFieldError('reg_login', 'Login trop court (min 3 caractères)');
                isValid = false;
            } else {
                clearFieldError('reg_login');
            }

            // Password validation
            const password = document.getElementById('reg_password').value;
            if (!password) {
                showFieldError('reg_password', 'Mot de passe requis');
                isValid = false;
            } else if (password.length < 6) {
                showFieldError('reg_password', 'Mot de passe trop court (min 6 caractères)');
                isValid = false;
            } else {
                clearFieldError('reg_password');
            }

            // Confirm password validation
            const confirmPassword = document.getElementById('reg_confirm_password').value;
            if (password !== confirmPassword) {
                showFieldError('reg_confirm_error', 'Les mots de passe ne correspondent pas');
                isValid = false;
            } else {
                clearFieldError('reg_confirm_error');
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    // Check URL parameters for messages
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    const success = urlParams.get('success');

    if (error) {
        let message = '';
        switch(error) {
            case 'invalid_credentials':
                message = 'Email/Login ou mot de passe incorrect';
                break;
            case 'user_exists':
                message = 'Cet email ou login existe déjà';
                break;
            case 'db_error':
                message = 'Erreur technique. Veuillez réessayer';
                break;
            default:
                message = decodeURIComponent(error);
        }
        showAlert(message, 'error');
    }

    if (success === 'registered') {
        showAlert('Inscription réussie ! Vous pouvez maintenant vous connecter', 'success');
        showTab('login');
    }
});

function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('error');
        const errorSpan = document.getElementById(`${fieldId}_error`);
        if (errorSpan) {
            errorSpan.textContent = message;
        }
    }
}

function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.remove('error');
        const errorSpan = document.getElementById(`${fieldId}_error`);
        if (errorSpan) {
            errorSpan.textContent = '';
        }
    }
}