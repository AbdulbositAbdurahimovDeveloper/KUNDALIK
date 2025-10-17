document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginButton = document.getElementById('login-button');
    const errorMessage = document.getElementById('error-message');

    // Agar foydalanuvchi allaqachon kirgan bo'lsa, uni panelga yo'naltirish
    if (localStorage.getItem('accessToken')) {
        window.location.href = '/admin-panel/index.html';
    }

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const email = emailInput.value;
        const password = passwordInput.value;

        // Xatolik xabarini yashirish
        errorMessage.textContent = '';
        errorMessage.style.display = 'none';

        // Tugmani nofaol qilish va matnini o'zgartirish
        loginButton.disabled = true;
        loginButton.textContent = 'Kirilmoqda...';

        try {
            const response = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (!response.ok || !data.success) {
                throw new Error(data.message || 'Login yoki parol xato');
            }

            // MUHIM TEKSHIRUV: Foydalanuvchi ADMINmi?
            const authorities = data.data.authorities || [];
            if (!authorities.includes('ROLE_ADMIN')) {
                throw new Error("Sizda ushbu panelga kirish huquqi yo'q.");
            }

            // Tokenlarni saqlash
            localStorage.setItem('accessToken', data.data.access_token);
            localStorage.setItem('refreshToken', data.data.refresh_token);

            // Asosiy panelga yo'naltirish
            window.location.href = '/admin-panel/index.html';

        } catch (error) {
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        } finally {
            // Tugmani yana faol qilish
            loginButton.disabled = false;
            loginButton.textContent = 'Kirish';
        }
    });
});