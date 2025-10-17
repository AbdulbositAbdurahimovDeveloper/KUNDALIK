const API_BASE_URL = '/api/v1';

async function fetchWithAuth(endpoint, options = {}) {
    const token = localStorage.getItem('accessToken');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        // Token yaroqsiz yoki muddati o'tgan
        localStorage.clear();
        window.location.href = '/admin-panel/login.html';
        throw new Error('Sessiya muddati tugadi. Iltimos, qayta kiring.');
    }

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'API so\'rovida xatolik');
    }

    // NO_CONTENT (204) statusida body bo'lmaydi
    if (response.status === 204) {
        return null;
    }

    const responseData = await response.json();
    return responseData.data; // ResponseDTO ning ichidagi 'data' qismini qaytaramiz
}