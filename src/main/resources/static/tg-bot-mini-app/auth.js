document.addEventListener('DOMContentLoaded', () => {
    // 1. O'zgaruvchilarni eng yuqorida, bir joyda e'lon qilish
    const telegramApp = window.Telegram?.WebApp;
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const tabButtons = document.querySelectorAll('.tab-link');
    const tabPanels = document.querySelectorAll('.tab-content');

    // 2. Sahifa yuklanishi bilan darhol Telegram Web App obyektini tekshirish
    if (telegramApp) {
        console.log("✅ Sahifa yuklandi: Telegram Web App obyekti MAVJUD.");
        // BU ENG MUHIM LOG: initData shu yerda ko'rinishi kerak
        console.log("-> Boshlang'ich InitData qiymati:", telegramApp.initData);

        telegramApp.ready();
        telegramApp.expand();
    } else {
        console.warn("⚠️ DIQQAT: Sahifa yuklandi, lekin Telegram muhiti topilmadi. initData yuborilmaydi.");
    }

    // --- YORDAMCHI FUNKSIYALAR (O'zgarishsiz qoldirildi) ---

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

    function clearErrors(formElement) {
        formElement.querySelectorAll('.error-message').forEach(message => message.textContent = '');
        formElement.querySelectorAll('.is-invalid').forEach(field => field.classList.remove('is-invalid'));
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
                    if (errorMessageSlot) errorMessageSlot.textContent = fieldError.message;
                }
            });
        } else {
            const generalError = formElement.querySelector('.general-error-message');
            const message = errorPayload?.message || 'Noma\'lum xatolik yuz berdi.';
            if (generalError) {
                generalError.textContent = message;
                generalError.style.display = 'block';
            } else if (telegramApp) {
                telegramApp.showAlert(message);
            }
        }
    }

    // --- ASOSIY SO'ROVNI YUBORISH FUNKSIYASI ---

    async function handleSubmit(event, endpoint, successMessage) {
        event.preventDefault();
        const formElement = event.target;
        clearErrors(formElement);

        const submitButton = formElement.querySelector('.submit-btn');
        const originalText = submitButton.textContent;
        submitButton.disabled = true;
        submitButton.textContent = 'Yuborilmoqda…';

        // 3. Sarlavhani (header) yaratish. U tashqaridagi 'telegramApp' o'zgaruvchisidan foydalanadi.
        const initDataHeader = telegramApp?.initData ? { 'Telegram-Init-Data': telegramApp.initData } : {};

        // MUAMMONI ANIQLASH UCHUN LOG:
        console.log("🚀 So'rov yuborishga tayyorlanmoqda...");
        console.log("-> Yuborilayotgan sarlavha (header) obyekti:", initDataHeader);

        const formData = new FormData(formElement);
        const payload = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...initDataHeader
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.success) {
                if (telegramApp) {
                    telegramApp.showAlert(successMessage, () => telegramApp.close());
                } else {
                    alert(successMessage);
                }
            } else {
                displayErrors(formElement, data);
            }
        } catch (error) {
            // Xatolikni konsolga to'liq chiqarish
            console.error("❗️ Fetch so'rovida xatolik yuz berdi:", error);
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

    // --- HODISALARNI BOG'LASH ---

    loginForm.addEventListener('submit', event =>
        handleSubmit(event, '/api/public/telegram/login', 'Muvaffaqiyatli tizimga kirdingiz!')
    );

    registerForm.addEventListener('submit', event =>
        handleSubmit(event, '/api/public/telegram/register', 'Hisobingiz muvaffaqiyatli yaratildi!')
    );
});