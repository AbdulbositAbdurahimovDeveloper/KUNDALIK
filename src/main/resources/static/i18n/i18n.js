document.addEventListener('DOMContentLoaded', () => {
    // --- GLOBAL STATE ---
    let allLanguages = [];
    let allKeys = [];

    // --- DOM SELECTORS ---
    const views = document.querySelectorAll('.view');
    const navButtons = document.querySelectorAll('.nav-btn');
    const loader = document.getElementById('loader');
    const cancelModalButtons = document.querySelectorAll('.cancel-modal');

    // Language elements
    const languagesTableBody = document.getElementById('languages-table-body');
    const languageModal = document.getElementById('language-modal');
    const languageForm = document.getElementById('language-form');

    // Key elements
    const keysTableBody = document.getElementById('keys-table-body');
    const keyModal = document.getElementById('key-modal');
    const keyForm = document.getElementById('key-form');

    // Single Translation elements
    const translationsTableBody = document.getElementById('translations-table-body');
    const translationModal = document.getElementById('translation-modal');
    const translationForm = document.getElementById('translation-form');
    const translationLanguageSelect = document.getElementById('translation-language');
    const translationKeySelect = document.getElementById('translation-key');

    // Bulk Translation elements
    const bulkTranslationModal = document.getElementById('bulk-translation-modal');
    const bulkTranslationForm = document.getElementById('bulk-translation-form');
    const bulkTranslationFields = document.getElementById('bulk-translation-fields');

    // --- API HELPER ---
    const API_BASE_URL = '/api/v1';
    const showLoader = () => loader.classList.remove('loader-hidden');
    const hideLoader = () => loader.classList.add('loader-hidden');

    const apiRequest = async (endpoint, method = 'GET', body = null) => {
        showLoader();
        try {
            const token = localStorage.getItem('accessToken');
            const headers = {'Content-Type': 'application/json'};
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const options = {method, headers};
            if (body) options.body = JSON.stringify(body);

            const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

            if (response.status === 401) {
                alert("Avtorizatsiyadan o'tilmagan yoki token eskirgan. Tizimga qayta kiring.");
                throw new Error('Unauthorized');
            }
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'API so\'rovida xatolik');
            }
            if (response.status === 204) return null;
            const responseData = await response.json();
            return responseData.data;
        } catch (error) {
            console.error('API Error:', error.message);
            if (error.message !== 'Unauthorized') alert(`Xatolik: ${error.message}`);
            return null;
        } finally {
            hideLoader();
        }
    };

    // --- RENDER FUNCTIONS ---
    const renderLanguages = (languages) => {
        languagesTableBody.innerHTML = languages.map(lang => `
            <tr>
                <td>${lang.id}</td><td>${lang.code}</td><td>${lang.name}</td>
                <td><span class="status-badge ${lang.active ? 'status-active' : 'status-inactive'}">${lang.active ? 'Aktiv' : 'Nofaol'}</span></td>
                <td>${lang.defaultLanguage ? '<span class="default-badge">✓</span>' : ''}</td>
                <td class="action-buttons"><button class="btn edit-language" data-id="${lang.id}">Tahrir</button><button class="btn btn-danger delete-language" data-id="${lang.id}">O'chirish</button></td>
            </tr>`).join('');
    };

    const renderKeys = (keys) => {
        keysTableBody.innerHTML = keys.map(key => `
            <tr>
                <td>${key.id}</td><td><code>${key.key}</code></td><td>${key.description || '---'}</td>
                <td class="action-buttons"><button class="btn translate-key" data-id="${key.id}" title="Tarjimalarni kiritish">Tarjima</button><button class="btn edit-key" data-id="${key.id}">Tahrir</button><button class="btn btn-danger delete-key" data-id="${key.id}">O'chirish</button></td>
            </tr>`).join('');
    };

    const renderTranslations = (translations) => {
        translationsTableBody.innerHTML = translations.map(t => `
            <tr>
                <td>${t.id}</td><td>${t.language.name} (${t.language.code})</td><td><code>${t.messageKey.key}</code></td>
                <td>${t.text.substring(0, 50)}${t.text.length > 50 ? '...' : ''}</td>
                <td class="action-buttons"><button class="btn edit-translation" data-id="${t.id}">Tahrir</button><button class="btn btn-danger delete-translation" data-id="${t.id}">O'chirish</button></td>
            </tr>`).join('');
    };

    // --- FETCH DATA ---
    const fetchAndRenderLanguages = async () => {
        const data = await apiRequest('/languages?size=1000');
        if (data) {
            allLanguages = data.content;
            renderLanguages(allLanguages);
        }
    };
    const fetchAndRenderKeys = async () => {
        const data = await apiRequest('/message-keys?size=1000');
        if (data) {
            allKeys = data.content;
            renderKeys(allKeys);
        }
    };
    const fetchAndRenderTranslations = async () => {
        const data = await apiRequest('/translations?size=1000');
        if (data) renderTranslations(data.content);
    };

    // --- NAVIGATION ---
    navButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.id.replace('nav-', '') + '-view';
            navButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            views.forEach(view => view.classList.remove('active'));
            document.getElementById(targetId).classList.add('active');
            if (targetId === 'languages-view') fetchAndRenderLanguages();
            if (targetId === 'keys-view') fetchAndRenderKeys();
            if (targetId === 'translations-view') fetchAndRenderTranslations();
        });
    });

    // --- MODAL CONTROLS ---
    cancelModalButtons.forEach(btn => btn.addEventListener('click', () => btn.closest('dialog').close()));

    // --- EVENT LISTENERS & FORM SUBMISSIONS ---
    // LANGUAGES
    document.getElementById('show-language-modal').addEventListener('click', () => {
        languageForm.reset();
        languageForm.querySelector('input[type=hidden]').value = '';
        languageForm.querySelector('h3').textContent = 'Yangi Til';
        document.getElementById('language-code').disabled = false;
        languageModal.showModal();
    });
    languageForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('language-id').value;
        const body = {
            code: document.getElementById('language-code').value,
            name: document.getElementById('language-name').value,
            active: document.getElementById('language-active').checked,
            defaultLanguage: document.getElementById('language-default').checked
        };
        const result = id ? await apiRequest(`/languages/${id}`, 'PUT', {
            name: body.name,
            active: body.active
        }) : await apiRequest('/languages', 'POST', body);
        if (result !== null) {
            languageModal.close();
            fetchAndRenderLanguages();
        }
    });
    languagesTableBody.addEventListener('click', async (e) => {
        const target = e.target;
        const id = target.dataset.id;
        if (target.classList.contains('edit-language')) {
            const lang = allLanguages.find(l => l.id == id);
            if (lang) {
                document.getElementById('language-id').value = lang.id;
                document.getElementById('language-code').value = lang.code;
                document.getElementById('language-name').value = lang.name;
                document.getElementById('language-active').checked = lang.active;
                document.getElementById('language-default').checked = lang.defaultLanguage;
                languageForm.querySelector('h3').textContent = 'Tilni Tahrirlash';
                document.getElementById('language-code').disabled = true;
                languageModal.showModal();
            }
        }
        if (target.classList.contains('delete-language')) {
            if (confirm(`Rostdan ham ushbu tilni o'chirmoqchimisiz?`)) {
                await apiRequest(`/languages/${id}`, 'DELETE');
                fetchAndRenderLanguages();
            }
        }
    });

    // KEYS
    document.getElementById('show-key-modal').addEventListener('click', () => {
        keyForm.reset();
        keyForm.querySelector('input[type=hidden]').value = '';
        keyForm.querySelector('h3').textContent = 'Yangi Kalit';
        document.getElementById('key-code').disabled = false;
        keyModal.showModal();
    });
    keyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('key-id').value;
        const body = {
            key: document.getElementById('key-code').value,
            description: document.getElementById('key-description').value
        };
        if (id) {
            const result = await apiRequest(`/message-keys/${id}`, 'PUT', {description: body.description});
            if (result !== null) {
                keyModal.close();
                fetchAndRenderKeys();
            }
        } else {
            const newKey = await apiRequest('/message-keys', 'POST', body);
            if (newKey) {
                keyModal.close();
                fetchAndRenderKeys();
                openBulkTranslateModal(newKey.id);
            }
        }
    });
    keysTableBody.addEventListener('click', async (e) => {
        const target = e.target;
        const id = target.dataset.id;
        if (target.classList.contains('edit-key')) {
            const key = allKeys.find(k => k.id == id);
            if (key) {
                document.getElementById('key-id').value = key.id;
                document.getElementById('key-code').value = key.key;
                document.getElementById('key-description').value = key.description;
                keyForm.querySelector('h3').textContent = 'Kalitni Tahrirlash';
                document.getElementById('key-code').disabled = true;
                keyModal.showModal();
            }
        }
        if (target.classList.contains('delete-key')) {
            if (confirm(`O'chirishdan oldin bu kalitga bog'liq barcha tarjimalarni o'chiring.`)) {
                await apiRequest(`/message-keys/${id}`, 'DELETE');
                fetchAndRenderKeys();
            }
        }
        if (target.classList.contains('translate-key')) {
            openBulkTranslateModal(id);
        }
    });

    // SINGLE TRANSLATION
    const populateTranslationModalSelects = () => {
        translationLanguageSelect.innerHTML = '<option value="">Tilni tanlang</option>';
        allLanguages.filter(l => l.active).forEach(lang => {
            translationLanguageSelect.innerHTML += `<option value="${lang.id}">${lang.name} (${lang.code})</option>`;
        });
        translationKeySelect.innerHTML = '<option value="">Kalitni tanlang</option>';
        allKeys.forEach(key => {
            translationKeySelect.innerHTML += `<option value="${key.id}">${key.key}</option>`;
        });
    };
    document.getElementById('show-translation-modal').addEventListener('click', async () => {
        await Promise.all([fetchAndRenderLanguages(), fetchAndRenderKeys()]);
        populateTranslationModalSelects();
        translationForm.reset();
        translationForm.querySelector('h3').textContent = 'Yangi Tarjima';
        document.getElementById('translation-id').value = '';
        translationLanguageSelect.disabled = false;
        translationKeySelect.disabled = false;
        translationModal.showModal();
    });
    translationForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('translation-id').value;
        const body = {
            languageId: translationLanguageSelect.value,
            messageKeyId: translationKeySelect.value,
            text: document.getElementById('translation-text').value
        };
        const result = id ? await apiRequest(`/translations/${id}`, 'PUT', {text: body.text}) : await apiRequest('/translations', 'POST', body);
        if (result !== null) {
            translationModal.close();
            fetchAndRenderTranslations();
        }
    });
    translationsTableBody.addEventListener('click', async (e) => {
        const target = e.target;
        const id = target.dataset.id;
        if (target.classList.contains('edit-translation')) {
            const translation = await apiRequest(`/translations/${id}`);
            if (translation) {
                await Promise.all([fetchAndRenderLanguages(), fetchAndRenderKeys()]);
                populateTranslationModalSelects();
                translationForm.reset();
                translationForm.querySelector('h3').textContent = 'Tarjimani Tahrirlash';
                document.getElementById('translation-id').value = translation.id;
                translationLanguageSelect.value = translation.language.id;
                translationKeySelect.value = translation.messageKey.id;
                document.getElementById('translation-text').value = translation.text;
                translationLanguageSelect.disabled = true;
                translationKeySelect.disabled = true;
                translationModal.showModal();
            }
        }
        if (target.classList.contains('delete-translation')) {
            if (confirm(`Rostdan ham ushbu tarjimani o'chirmoqchimisiz?`)) {
                await apiRequest(`/translations/${id}`, 'DELETE');
                fetchAndRenderTranslations();
            }
        }
    });

    // BULK TRANSLATION
    const openBulkTranslateModal = async (keyId) => {
        const [keysData, languagesData, translationsData] = await Promise.all([apiRequest('/message-keys?size=1000'), apiRequest('/languages?size=1000'), apiRequest('/translations?size=5000')]);
        if (!keysData || !languagesData) return;
        allKeys = keysData.content;
        allLanguages = languagesData.content;
        const key = allKeys.find(k => k.id == keyId);
        if (!key) return;
        const activeLanguages = allLanguages.filter(l => l.active);
        const existingTranslations = translationsData ? translationsData.content.filter(t => t.messageKey.id == keyId) : [];
        document.getElementById('bulk-translation-modal-title').textContent = `Tarjimalar: ${key.key}`;
        document.getElementById('bulk-translation-key-id').value = key.id;
        bulkTranslationFields.innerHTML = '';
        activeLanguages.forEach(lang => {
            const existing = existingTranslations.find(t => t.language.id == lang.id);
            const text = existing ? existing.text : '';
            bulkTranslationFields.innerHTML += `<div class="form-group"><label for="lang-${lang.id}">${lang.name} (${lang.code})</label><textarea id="lang-${lang.id}" data-lang-id="${lang.id}" rows="3">${text}</textarea></div>`;
        });
        bulkTranslationModal.showModal();
    };
    bulkTranslationForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const messageKeyId = document.getElementById('bulk-translation-key-id').value;
        const translations = Array.from(bulkTranslationFields.querySelectorAll('textarea')).map(ta => ({
            languageId: ta.dataset.langId,
            text: ta.value
        }));
        const result = await apiRequest('/translations/bulk-upsert', 'POST', {messageKeyId, translations});
        if (result !== null) {
            bulkTranslationModal.close();
            if (document.getElementById('translations-view').classList.contains('active')) {
                fetchAndRenderTranslations();
            }
        }
    });

    // --- INITIAL LOAD ---
    fetchAndRenderLanguages();
});