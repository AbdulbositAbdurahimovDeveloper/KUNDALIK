document.addEventListener('DOMContentLoaded', () => {
    // Telegram Web App obyektini xavfsiz tarzda olish
    const telegramApp = window.Telegram?.WebApp;
    if (telegramApp) {
        telegramApp.ready();
        telegramApp.expand();
    }

    const tabButtons = document.querySelectorAll('.tab-link');
    const tabPanels = document.querySelectorAll('.tab-content');

    function activateTab(targetId) {
        tabButtons.forEach(button => {
            const isActive = button.dataset.tab === targetId;
            button.classList.toggle('active', isActive);
            button.setAttribute('aria-selected', String(isActive));
        });

        tabPanels.forEach(panel => {
            const isActive = panel.id === targetId;
            panel.classList.toggle('active', isActive);
            panel.hidden = !isActive;
        });
    }

    tabButtons.forEach(button => {
        button.addEventListener('click', () => activateTab(button.dataset.tab));
    });

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    function clearErrors(formElement) {
        formElement.querySelectorAll('.error-message').forEach(message => {
            message.textContent = '';
        });
        formElement.querySelectorAll('.is-invalid').forEach(field => {
            field.classList.remove('is-invalid');
        });
        const generalError = formElement.querySelector('.general-error-message');
        if (generalError) {
            generalError.textContent = '';
            generalError.style.display = 'none';
        }
    }

    function displayErrors(formElement, errorPayload) {
        if (errorPayload?.error?.fieldErrors?.length > 0) {
            errorPayload.error.fieldErrors.forEach(fieldError => {
                const input = formElement.querySelector(`[name="${fieldError.field}"]`);
                if (input) {
                    input.classList.add('is-invalid');
                    const errorMessageSlot = input.closest('.form-group')?.querySelector('.error-message');
                    if (errorMessageSlot) {
                        errorMessageSlot.textContent = fieldError.message;
                    }
                }
            });
        } else {
            const generalError = formElement.querySelector('.general-error-message');
            const message = errorPayload?.message || 'Noma\'lum xatolik yuz berdi. Qaytadan urunib ko\'ring.';
            if (generalError) {
                generalError.textContent = message;
                generalError.style.display = 'block';
            } else if (telegramApp) {
                telegramApp.showAlert(message);
            }
        }
    }

    async function handleSubmit(event, endpoint, successMessage) {
        event.preventDefault();
        const formElement = event.target;
        clearErrors(formElement);

        const submitButton = formElement.querySelector('.submit-btn');
        const originalText = submitButton.textContent;
        submitButton.disabled = true;
        submitButton.textContent = 'Yuborilmoqda…';

        // initData faqat Telegram muhitida mavjud bo'ladi
        const initDataHeader = telegramApp?.initData ? { 'Telegram-Init-Data': telegramApp.initData } : {};

        const formData = new FormData(formElement);
        const payload = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...initDataHeader // Brauzerda bu bo'sh obyekt bo'ladi
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.success) {
                if (telegramApp) {
                    telegramApp.showAlert(successMessage, () => telegramApp.close());
                } else {
                    alert(successMessage); // Brauzerda test uchun
                }
            } else {
                displayErrors(formElement, data);
            }
        } catch (error) {
            const generalError = formElement.querySelector('.general-error-message');
            if (generalError) {
                generalError.textContent = 'Server bilan bog\'lanishda xatolik. Internet aloqasini tekshiring.';
                generalError.style.display = 'block';
            }
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        }
    }

    loginForm.addEventListener('submit', event =>
        handleSubmit(event, '/api/public/telegram/login', 'Muvaffaqiyatli tizimga kirdingiz!')
    );

    registerForm.addEventListener('submit', event =>
        handleSubmit(event, '/api/public/telegram/register', 'Hisobingiz muvaffaqiyatli yaratildi!')
    );
});