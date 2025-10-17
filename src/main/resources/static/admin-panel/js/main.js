document.addEventListener('DOMContentLoaded', () => {
    // ... (Faylning boshi o'zgarishsiz qoladi: xavfsizlik tekshiruvi, elementlarni topish, logout va kesh) ...
    if (!localStorage.getItem('accessToken')) {
        window.location.href = '/admin-panel/login.html';
        return;
    }
    const pageTitle = document.getElementById('page-title');
    const contentArea = document.getElementById('content-area');
    const navLinks = document.querySelectorAll('.nav-link');
    document.getElementById('logout-button').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/admin-panel/login.html';
    });
    let appCache = {languages: null, translations: null};

    // ... (loadLanguagesPage va renderLanguageModal funksiyalari o'zgarishsiz qoladi) ...
    async function loadLanguagesPage() { /* ... avvalgi kod ... */
        pageTitle.textContent = 'Tillar';
        contentArea.innerHTML = `<div class="page-header"><button id="add-language-btn" class="btn btn-primary">Yangi til qo\'shish</button></div><div id="table-container">Yuklanmoqda...</div>`;
        try {
            const pageData = await fetchWithAuth('/languages?size=100');
            const languages = pageData.content;
            const tableContainer = document.getElementById('table-container');
            if (languages.length === 0) {
                tableContainer.innerHTML = '<p>Hozircha tillar mavjud emas.</p>';
                return;
            }
            tableContainer.innerHTML = `<table class="data-table"><thead><tr><th>ID</th><th>Kod</th><th>Nomi</th><th>Holati</th><th>Asosiy til</th><th>Amallar</th></tr></thead><tbody> ${languages.map(lang => `<tr data-id="${lang.id}"><td>${lang.id}</td><td>${lang.code}</td><td>${lang.name}</td><td><label class="switch"><input type="checkbox" class="status-toggle" data-id="${lang.id}" ${lang.active ? 'checked' : ''}><span class="slider"></span></label></td><td>${lang.defaultLanguage ? '✅' : ''}</td><td class="actions"><button class="btn btn-sm btn-edit-lang" data-id="${lang.id}">Tahrirlash</button><button class="btn btn-sm btn-default" data-id="${lang.id}" ${lang.defaultLanguage ? 'disabled' : ''}>Asosiy qilish</button><button class="btn btn-sm btn-danger btn-delete-lang" data-id="${lang.id}">O\'chirish</button></td></tr>`).join('')}</tbody></table>`;
        } catch (error) {
            contentArea.innerHTML = `<p class="error-text">Ma\'lumotlarni yuklashda xatolik: ${error.message}</p>`;
        }
    }

    function renderLanguageModal(language = null) { /* ... avvalgi kod ... */
        const isEdit = language !== null;
        const title = isEdit ? "Tilni Tahrirlash" : "Yangi Til Yaratish";
        const modalHTML = `<div class="modal-overlay" id="language-modal-overlay"><div class="modal-content"><div class="modal-header"><h2>${title}</h2><button class="close-modal-btn">&times;</button></div><form id="language-form"><input type="hidden" id="languageId" value="${isEdit ? language.id : ''}"><div class="input-group"><label for="code">Kod</label><input type="text" id="code" value="${isEdit ? language.code : ''}" ${isEdit ? 'disabled' : ''} required></div><div class="input-group"><label for="name">Nomi</label><input type="text" id="name" value="${isEdit ? language.name : ''}" required></div><div class="input-group-checkbox"><label for="active">Faol</label><input type="checkbox" id="active" ${isEdit ? (language.active ? 'checked' : '') : 'checked'}></div>${!isEdit ? `<div class="input-group-checkbox"><label for="defaultLanguage">Asosiy til</label><input type="checkbox" id="defaultLanguage"></div>` : ''}<div class="modal-footer"><button type="button" class="btn btn-secondary close-modal-btn">Bekor</button><button type="submit" class="btn btn-primary">${isEdit ? "Saqlash" : "Yaratish"}</button></div></form></div></div>`;
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        document.querySelectorAll('.close-modal-btn, #language-modal-overlay').forEach(el => {
            el.addEventListener('click', e => {
                if (e.target === e.currentTarget) el.closest('.modal-overlay').remove()
            });
        });
        document.getElementById('language-form').addEventListener('submit', async e => {
            e.preventDefault();
            const id = document.getElementById('languageId').value;
            const payload = {
                name: document.getElementById('name').value,
                active: document.getElementById('active').checked
            };
            try {
                if (isEdit) {
                    await fetchWithAuth(`/languages/${id}`, {method: 'PUT', body: JSON.stringify(payload)});
                } else {
                    payload.code = document.getElementById('code').value;
                    payload.defaultLanguage = document.getElementById('defaultLanguage').checked;
                    await fetchWithAuth('/languages', {method: 'POST', body: JSON.stringify(payload)});
                }
                document.getElementById('language-modal-overlay').remove();
                loadLanguagesPage();
            } catch (error) {
                alert(`Xatolik: ${error.message}`);
            }
        });
    }

    // ... (loadKeysPage va renderKeyModal funksiyalari o'zgarishsiz qoladi) ...
    async function loadKeysPage() { /* ... avvalgi kod ... */
        pageTitle.textContent = 'Kalit So\'zlar';
        contentArea.innerHTML = `<div class="page-header"><button id="add-key-btn" class="btn btn-primary">Yangi kalit qo\'shish</button></div><div id="table-container">Yuklanmoqda...</div>`;
        try {
            const [keysResponse, languagesResponse, translationsResponse] = await Promise.all([fetchWithAuth('/message-keys?size=1000'), fetchWithAuth('/languages?size=100'), fetchWithAuth('/translations?size=2000')]);
            const keys = keysResponse.content;
            appCache.languages = languagesResponse.content;
            appCache.translations = translationsResponse.content;
            const tableContainer = document.getElementById('table-container');
            if (keys.length === 0) {
                tableContainer.innerHTML = '<p>Hozircha kalit so\'zlar mavjud emas.</p>';
                return;
            }
            tableContainer.innerHTML = `<table class="data-table"><thead><tr><th style="width: 50px;"></th><th>ID</th><th>Kalit</th><th>Tavsif</th><th>Amallar</th></tr></thead><tbody> ${keys.map(key => `<tr class="main-row" data-id="${key.id}"><td><span class="toggle-translations" data-id="${key.id}">▶</span></td><td>${key.id}</td><td>${key.key}</td><td>${key.description || '<em>-</em>'}</td><td class="actions"><button class="btn btn-sm btn-edit-key" data-id="${key.id}">Tahrirlash</button><button class="btn btn-sm btn-danger btn-delete-key" data-id="${key.id}">O\'chirish</button></td></tr>`).join('')}</tbody></table>`;
        } catch (error) {
            contentArea.innerHTML = `<p class="error-text">Ma\'lumotlarni yuklashda xatolik: ${error.message}</p>`;
        }
    }

    function renderKeyModal(key = null) { /* ... avvalgi kod ... */
        const isEdit = key !== null;
        const title = isEdit ? "Kalitni Tahrirlash" : "Yangi Kalit Yaratish";
        const modalHTML = `<div class="modal-overlay" id="key-modal-overlay"><div class="modal-content"><div class="modal-header"><h2>${title}</h2><button class="close-modal-btn">&times;</button></div><form id="key-form"><input type="hidden" id="keyId" value="${isEdit ? key.id : ''}"><div class="input-group"><label for="key">Kalit</label><input type="text" id="key" value="${isEdit ? key.key : ''}" ${isEdit ? 'disabled' : ''} required pattern="^[a-zA-Z0-9._-]+$"></div><div class="input-group"><label for="description">Tavsif</label><textarea id="description" rows="3">${isEdit ? (key.description || '') : ''}</textarea></div><div class="modal-footer"><button type="button" class="btn btn-secondary close-modal-btn">Bekor</button><button type="submit" class="btn btn-primary">${isEdit ? "Saqlash" : "Yaratish"}</button></div></form></div></div>`;
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        document.querySelectorAll('.close-modal-btn, #key-modal-overlay').forEach(el => {
            el.addEventListener('click', e => {
                if (e.target === e.currentTarget) el.closest('.modal-overlay').remove()
            });
        });
        document.getElementById('key-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('keyId').value;
            const description = document.getElementById('description').value;
            try {
                if (isEdit) {
                    await fetchWithAuth(`/message-keys/${id}`, {method: 'PUT', body: JSON.stringify({description})});
                } else {
                    const newKey = document.getElementById('key').value;
                    await fetchWithAuth('/message-keys', {
                        method: 'POST',
                        body: JSON.stringify({key: newKey, description})
                    });
                }
                document.getElementById('key-modal-overlay').remove();
                loadKeysPage();
            } catch (error) {
                alert(`Xatolik: ${error.message}`);
            }
        });
    }


    // =================================================================
    // TARJIMALAR SAHIFASI UCHUN FUNKSIYALAR (YANGILANDI!)
    // =================================================================
    async function loadTranslationsPage() {
        pageTitle.textContent = 'Tarjimalar';
        contentArea.innerHTML = '<p>Ma\'lumotlar yuklanmoqda...</p>';

        try {
            // 1. Barcha kerakli ma'lumotlarni bir vaqtda olamiz
            const [keysResponse, languagesResponse, translationsResponse] = await Promise.all([
                fetchWithAuth('/message-keys?size=1000'),
                fetchWithAuth('/languages?size=100'),
                fetchWithAuth('/translations?size=2000')
            ]);

            const allKeys = keysResponse.content;
            const allLanguages = languagesResponse.content;
            const allTranslations = translationsResponse.content;

            const activeLanguages = allLanguages.filter(lang => lang.active);
            const activeLangCount = activeLanguages.length;

            // 2. Tarjimalarni kalit IDsi bo'yicha guruhlaymiz (optimallashtirish)
            const translationsByKey = new Map();
            allTranslations.forEach(t => {
                const keyId = t.messageKey.id;
                if (!translationsByKey.has(keyId)) {
                    translationsByKey.set(keyId, []);
                }
                translationsByKey.get(keyId).push(t);
            });

            // 3. Select > Option'larni status bilan birga yaratamiz
            const optionsHTML = allKeys.map(key => {
                const keyTranslations = translationsByKey.get(key.id) || [];
                const translationCount = keyTranslations.length;

                let statusClass = 'status-red'; // Standart holat - tarjima yo'q
                if (translationCount > 0 && translationCount < activeLangCount) {
                    statusClass = 'status-yellow'; // Chala tarjima
                } else if (translationCount >= activeLangCount) {
                    statusClass = 'status-green'; // To'liq tarjima
                }

                return `<option value="${key.id}" class="${statusClass}">${key.key}</option>`;
            }).join('');

            // 4. Asosiy UI'ni chizamiz
            contentArea.innerHTML = `
                <div class="translation-container">
                    <div class="input-group">
                        <label for="key-selector">Tarjima qilish uchun kalitni tanlang</label>
                        <select id="key-selector">
                            <option value="">-- Tanlang --</option>
                            ${optionsHTML}
                        </select>
                    </div>
                    <div id="translation-form-container" style="display: none;">
                        <form id="translation-form"></form>
                    </div>
                </div>`;

            // 5. Event Listener'larni o'rnatamiz (bu qism deyarli o'zgarmadi)
            const keySelector = document.getElementById('key-selector');
            const translationFormContainer = document.getElementById('translation-form-container');
            const translationForm = document.getElementById('translation-form');

            keySelector.addEventListener('change', async (e) => {
                const selectedKeyId = e.target.value;
                if (!selectedKeyId) {
                    translationFormContainer.style.display = 'none';
                    return;
                }

                // Oldindan yuklangan keshdan foydalanamiz, yangi so'rov yubormaymiz!
                const existingTranslations = translationsByKey.get(parseInt(selectedKeyId)) || [];

                translationForm.innerHTML = '';
                activeLanguages.forEach(lang => {
                    const existing = existingTranslations.find(t => t.language.id === lang.id);
                    const text = existing ? existing.text : '';
                    translationForm.innerHTML += `<div class="input-group"><label>${lang.name} (${lang.code})</label><textarea data-lang-id="${lang.id}" rows="2">${text}</textarea></div>`;
                });
                translationForm.innerHTML += `<div class="form-footer"><div id="success-message" class="success-message"></div><button type="submit" class="btn btn-primary">Barcha tarjimalarni saqlash</button></div>`;
                translationFormContainer.style.display = 'block';
            });
            translationForm.addEventListener('submit', async (e) => { /* ... avvalgidek qoladi ... */
                e.preventDefault();
                const selectedKeyId = keySelector.value;
                const textareas = translationForm.querySelectorAll('textarea');
                const successMessage = document.getElementById('success-message');
                const translationsPayload = Array.from(textareas).map(textarea => ({
                    languageId: parseInt(textarea.dataset.langId),
                    text: textarea.value
                }));
                const bulkRequest = {messageKeyId: parseInt(selectedKeyId), translations: translationsPayload};
                try {
                    await fetchWithAuth('/translations/bulk-upsert', {
                        method: 'POST',
                        body: JSON.stringify(bulkRequest)
                    });
                    successMessage.textContent = 'Tarjimalar muvaffaqiyatli saqlandi!';
                    successMessage.style.display = 'block';
                    setTimeout(() => {
                        successMessage.style.display = 'none';
                    }, 3000);
                } catch (error) {
                    alert(`Saqlashda xatolik: ${error.message}`);
                }
            });
        } catch (error) {
            contentArea.innerHTML = `<p class="error-text">Sahifani yuklashda xatolik: ${error.message}</p>`;
        }
    }

    // ... (Faylning qolgan qismi: loadPage va Event Listener'lar o'zgarishsiz qoladi) ...
    async function loadPage(pageName) {
        switch (pageName) {
            case 'languages':
                await loadLanguagesPage();
                break;
            case 'keys':
                await loadKeysPage();
                break;
            case 'translations':
                await loadTranslationsPage();
                break;
            default:
                contentArea.innerHTML = '<p>Sahifa topilmadi.</p>';
        }
    }

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            loadPage(link.getAttribute('data-page'));
        });
    });
    contentArea.addEventListener('click', async (e) => {
        const target = e.target;
        const id = target.dataset.id;
        if (target.id === 'add-language-btn') renderLanguageModal();
        if (target.classList.contains('btn-edit-lang')) {
            const lang = await fetchWithAuth(`/languages/${id}`);
            renderLanguageModal(lang);
        }
        if (target.classList.contains('btn-delete-lang')) {
            if (confirm("...")) {
                await fetchWithAuth(`/languages/${id}`, {method: 'DELETE'});
                loadLanguagesPage();
            }
        }
        if (target.classList.contains('status-toggle')) {
            await fetchWithAuth(`/languages/${id}/active?active=${target.checked}`, {method: 'PATCH'});
        }
        if (target.classList.contains('btn-default')) {
            await fetchWithAuth(`/languages/${id}/default`, {method: 'PATCH'});
            loadLanguagesPage();
        }
        if (target.id === 'add-key-btn') renderKeyModal();
        if (target.classList.contains('btn-edit-key')) {
            const key = await fetchWithAuth(`/message-keys/${id}`);
            renderKeyModal(key);
        }
        if (target.classList.contains('btn-delete-key')) {
            if (confirm("...")) {
                await fetchWithAuth(`/message-keys/${id}`, {method: 'DELETE'});
                loadKeysPage();
            }
        }
        if (target.classList.contains('toggle-translations')) {
            const mainRow = target.closest('.main-row');
            const detailsRow = mainRow.nextElementSibling;
            if (detailsRow && detailsRow.classList.contains('translation-details-row')) {
                detailsRow.remove();
                target.textContent = '▶';
            } else {
                const keyId = target.dataset.id;
                const activeLanguages = appCache.languages.filter(l => l.active);
                const keyTranslations = appCache.translations.filter(t => t.messageKey.id == keyId);
                let detailsHTML = '<ul>';
                activeLanguages.forEach(lang => {
                    const translation = keyTranslations.find(t => t.language.id === lang.id);
                    const text = translation ? translation.text : '<em>— Tarjima kiritilmagan —</em>';
                    detailsHTML += `<li><strong>${lang.name}:</strong> ${text}</li>`;
                });
                detailsHTML += '</ul>';
                const newRow = document.createElement('tr');
                newRow.className = 'translation-details-row';
                newRow.innerHTML = `<td colspan="5">${detailsHTML}</td>`;
                mainRow.after(newRow);
                target.textContent = '▼';
            }
        }
    });
    loadPage('languages');
});