import { UI } from "../ui.js";

const mailHelperButton = document.getElementById("mail-helper");

if (mailHelperButton) {
    mailHelperButton.addEventListener("click", () => {
        if (mailHelperButton.classList.contains('active')) {
            mailHelperButton.classList.remove('active');
            UI.resetContent();
        } else {
            UI.deactivateAllButtons();
            mailHelperButton.classList.add("active");
            UI.updateContent(`
                <div class="section-header">
                    <div class="section-icon">
                        <i class="fa-solid fa-envelope"></i>
                    </div>
                    <div>
                        <h3 class="section-title">Asystent email</h3>
                        <p class="section-subtitle">Szybkie generowanie profesjonalnych odpowiedzi</p>
                    </div>
                </div>
                
                <div class="card">
                    <div class="form-group switch-group">
                        <label class="switch-label">Uwzględnić kontekst wcześniejszej wiadomości?</label>
                        <label class="switch">
                            <input type="checkbox" id="context-switch">
                            <span class="slider"></span>
                        </label>
                    </div>
                    
                    <div id="context-container" style="display: none; transition: opacity 0.3s, max-height 0.3s;">
                        <div class="form-group">
                            <label for="context-text">Kontekst wiadomości</label>
                            <textarea id="context-text" placeholder="Wklej tutaj poprzednią wiadomość..."></textarea>
                        </div>
                        
                        <div id="translation-container"></div>
                    </div>
                </div>
                
                <div class="card">
                    <div class="form-group">
                        <label for="issue-text">Treść odpowiedzi</label>
                        <textarea id="issue-text" placeholder="Opisz co chcesz przekazać..."></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="language-select">Język</label>
                        <select id="language-select">
                            <option value="pl">Polski</option>
                            <option value="en">Angielski</option>
                            <option value="de">Niemiecki</option>
                        </select>
                    </div>
                    
                    <button id="send-request" class="btn-primary">
                        <i class="fa-solid fa-paper-plane"></i> Generuj
                    </button>
                </div>
                
                <div id="response-container" style="display: none;"></div>
            `);

            // Dodanie wyjaśnienia jak to działa
            setTimeout(() => {
                const element = document.querySelector('.section-subtitle');
                if (element) {
                    element.innerHTML = 'Opisz treść odpowiedzi, a AI ją sformułuje';
                }
            }, 100);

            // Poprawiona obsługa przełącznika kontekstu
            document.getElementById("context-switch").addEventListener("change", async (event) => {
                const contextContainer = document.getElementById("context-container");

                if (event.target.checked) {
                    // Najpierw pokazujemy kontener, ale z zerową przezroczystością i wysokością
                    contextContainer.style.display = "block";
                    contextContainer.style.opacity = "0";
                    contextContainer.style.maxHeight = "0";

                    // Po małym opóźnieniu dodajemy właściwości
                    setTimeout(() => {
                        contextContainer.style.opacity = "1";
                        contextContainer.style.maxHeight = "1000px";
                    }, 10);

                    // Inicjalizacja kontenera tłumaczenia, jeśli jeszcze nie istnieje
                    let translationContainer = document.getElementById("translation-container");
                    if (!translationContainer.innerHTML.trim()) {
                        translationContainer.innerHTML = `
                            <div class="form-group">
                                <button id="translate-btn" class="btn-secondary">
                                    <i class="fa-solid fa-language"></i> Przetłumacz na polski
                                </button>
                            </div>
                            <div id="translation-spinner"></div>
                            <div id="translation-response-container" style="display: none;"></div>
                        `;

                        const translateBtn = document.getElementById("translate-btn");
                        translateBtn.addEventListener("click", async () => {
                            const token = await getToken();
                            const textToTranslate = document.getElementById("context-text").value;

                            if (!textToTranslate.trim()) {
                                showNotification("Wprowadź tekst do tłumaczenia", "warning");
                                return;
                            }

                            document.getElementById("translation-spinner").innerHTML = `<div class="spinner"></div>`;
                            translateBtn.disabled = true;
                            translateBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Tłumaczę...';

                            try {
                                const response = await fetch("http://localhost:20266/api/form-filler/email-helper/translation", {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json",
                                        "Authorization": `Bearer ${token}`
                                    },
                                    body: JSON.stringify({request: textToTranslate})
                                });

                                if (!response.ok) {
                                    handleResponseError(response.status);
                                }

                                const translationText = await response.text();
                                document.getElementById("translation-spinner").innerHTML = "";

                                const translationResponseContainer = document.getElementById("translation-response-container");
                                translationResponseContainer.innerHTML = `
                                    <div class="card">
                                        <div class="card-title">
                                            <i class="fa-solid fa-language"></i> Tłumaczenie
                                        </div>
                                        <div class="card-body">${translationText}</div>
                                    </div>
                                `;

                                translationResponseContainer.style.display = "block";

                                // Przywrócenie przycisku
                                translateBtn.disabled = false;
                                translateBtn.innerHTML = '<i class="fa-solid fa-language"></i> Przetłumacz na polski';
                            } catch (error) {
                                showNotification(error.message || "Błąd tłumaczenia", "error");
                                console.error("Błąd: ", error);

                                translateBtn.disabled = false;
                                translateBtn.innerHTML = '<i class="fa-solid fa-language"></i> Przetłumacz na polski';
                            }
                        });
                    } else {
                        translationContainer.style.display = "block";
                    }
                } else {
                    // Najpierw zmniejszamy przezroczystość i wysokość
                    contextContainer.style.opacity = "0";
                    contextContainer.style.maxHeight = "0";

                    // Po zakończeniu animacji ukrywamy element
                    setTimeout(() => {
                        contextContainer.style.display = "none";
                        document.getElementById("context-text").value = "";
                        const translationContainer = document.getElementById("translation-container");
                        if (translationContainer) {
                            translationContainer.style.display = "none";
                        }
                    }, 300);
                }
            });

            document.getElementById("send-request").addEventListener("click", async () => {
                const issueText = document.getElementById("issue-text").value;
                const language = document.getElementById("language-select").value;
                const contextSwitch = document.getElementById("context-switch").checked;
                const contextText = contextSwitch ? document.getElementById("context-text").value : null;

                if (!issueText) {
                    showNotification("Opisz treść wiadomości", "warning");
                    return;
                }

                // Zapamiętaj aktualny przycisk
                const sendButton = document.getElementById("send-request");
                const originalButtonText = sendButton.innerHTML;

                // Wyłącz przycisk i pokaż ładowanie
                sendButton.disabled = true;
                sendButton.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Generuję...';

                // Pokaż minimalistyczny ekran ładowania
                const issueTextArea = document.getElementById("issue-text");
                const languageSelect = document.getElementById("language-select");
                const contextSwitch2 = document.getElementById("context-switch");

                // Wyłącz wszystkie kontrolki
                issueTextArea.disabled = true;
                languageSelect.disabled = true;
                contextSwitch2.disabled = true;

                // Dodaj pasek postępu pod przyciskiem
                const formGroup = sendButton.parentNode;
                const progressBar = document.createElement('div');
                progressBar.className = 'progress-bar';
                progressBar.innerHTML = '<div class="progress-fill"></div>';
                formGroup.appendChild(progressBar);

                // Animacja paska postępu
                const progressFill = progressBar.querySelector('.progress-fill');
                progressFill.style.width = '0%';

                setTimeout(() => {
                    progressFill.style.width = '80%';
                    progressFill.style.transition = 'width 2s ease-in-out';
                }, 50);

                try {
                    const token = await getToken();

                    const response = await fetch("http://localhost:20266/api/form-filler/email-helper", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify({
                            context: contextText,
                            language: language,
                            content: issueText
                        })
                    });

                    if (!response.ok) {
                        handleResponseError(response.status);
                    }

                    const data = await response.json();

                    // Uzupełnij pasek postępu do 100%
                    progressFill.style.width = '100%';

                    setTimeout(() => {
                        UI.updateContent(`
                            <div class="section-header">
                                <div class="section-icon">
                                    <i class="fa-solid fa-check"></i>
                                </div>
                                <div>
                                    <h3 class="section-title">Email wygenerowany</h3>
                                    <p class="section-subtitle">Gotowe do skopiowania</p>
                                </div>
                            </div>
                            
                            <div class="message-preview">
                                <div class="message-header">
                                    <div class="message-icon">
                                        <i class="fa-solid fa-envelope"></i>
                                    </div>
                                    <div class="message-title">Treść wiadomości</div>
                                </div>
                                <div class="message-content" id="response-container">${data.content}</div>
                            </div>
                            
                            <div class="button-container">
                                <button id="result-translate-btn" class="btn-secondary">
                                    <i class="fa-solid fa-language"></i> Przetłumacz
                                </button>
                                <button id="copy-text" class="btn-primary">
                                    <i class="fa-solid fa-copy"></i> Kopiuj
                                </button>
                                <button id="restart" class="btn-secondary">
                                    <i class="fa-solid fa-redo"></i> Nowy
                                </button>
                            </div>
                            
                            <div id="translation-response-container" style="display: none; margin-top: 16px;"></div>
                        `);

                        let resultTranslationButton = document.getElementById("result-translate-btn");
                        resultTranslationButton.addEventListener("click", async () => {
                            const textToTranslate = data.content;

                            if (!textToTranslate) {
                                showNotification("Brak tekstu do tłumaczenia", "warning");
                                return;
                            }

                            try {
                                resultTranslationButton.disabled = true;
                                resultTranslationButton.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Tłumaczę...';

                                const response = await fetch("http://localhost:20266/api/form-filler/email-helper/translation", {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json",
                                        "Authorization": `Bearer ${token}`
                                    },
                                    body: JSON.stringify({ request: textToTranslate })
                                });

                                if (!response.ok) {
                                    handleResponseError(response.status);
                                }

                                const translationText = await response.text();

                                const translationResponseContainer = document.getElementById("translation-response-container");
                                translationResponseContainer.innerHTML = `
                                    <div class="message-preview">
                                        <div class="message-header">
                                            <div class="message-icon">
                                                <i class="fa-solid fa-language"></i>
                                            </div>
                                            <div class="message-title">Tłumaczenie na polski</div>
                                        </div>
                                        <div class="message-content">${translationText}</div>
                                    </div>
                                `;

                                // Pokaż rezultat
                                translationResponseContainer.style.display = "block";

                                resultTranslationButton.disabled = false;
                                resultTranslationButton.innerHTML = '<i class="fa-solid fa-language"></i> Przetłumacz';

                            } catch (error) {
                                showNotification(error.message || "Błąd tłumaczenia", "error");
                                console.error("Błąd: ", error);

                                resultTranslationButton.disabled = false;
                                resultTranslationButton.innerHTML = '<i class="fa-solid fa-language"></i> Przetłumacz';
                            }
                        });

                        document.getElementById("copy-text").addEventListener("click", () => {
                            const responseContainer = document.getElementById("response-container");
                            const plainText = responseContainer.innerText;
                            const tempTextArea = document.createElement("textarea");
                            tempTextArea.value = plainText;
                            document.body.appendChild(tempTextArea);
                            tempTextArea.select();
                            document.execCommand("copy");
                            document.body.removeChild(tempTextArea);

                            showNotification("Skopiowano do schowka", "success");
                        });

                        document.getElementById("restart").addEventListener("click", () => {
                            mailHelperButton.click();
                        });
                    }, 300); // Czas na dokończenie animacji paska postępu

                } catch (error) {
                    // Przywróć formę do stanu początkowego
                    const sendButton = document.getElementById("send-request");
                    if (sendButton) {
                        sendButton.disabled = false;
                        sendButton.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Generuj';
                    }

                    const issueTextArea = document.getElementById("issue-text");
                    const languageSelect = document.getElementById("language-select");
                    const contextSwitch2 = document.getElementById("context-switch");

                    if (issueTextArea) issueTextArea.disabled = false;
                    if (languageSelect) languageSelect.disabled = false;
                    if (contextSwitch2) contextSwitch2.disabled = false;

                    const progressBar = document.querySelector('.progress-bar');
                    if (progressBar) progressBar.remove();

                    showNotification("Wystąpił błąd. Spróbuj ponownie.", "error");
                    console.error("Błąd: ", error);
                }
            });
        }
    });
}

// Pomocnicza funkcja do pobierania tokena
async function getToken() {
    return new Promise((resolve, reject) => {
        chrome.storage.local.get("jwtToken", (result) => {
            if (chrome.runtime.lastError) {
                reject(chrome.runtime.lastError);
            } else {
                resolve(result.jwtToken);
            }
        });
    });
}

// Pomocnicze funkcje dla lepszego UX
function showNotification(message, type = "info") {
    // Usuwamy istniejące powiadomienia
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => {
        notification.remove();
    });

    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;

    let icon = 'info-circle';
    if (type === 'success') icon = 'check-circle';
    if (type === 'warning') icon = 'exclamation-triangle';
    if (type === 'error') icon = 'times-circle';

    notification.innerHTML = `
        <i class="fa-solid fa-${icon}"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(notification);

    // Animacja wejścia
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateY(0)';
    }, 10);

    // Automatyczne usunięcie po 2 sekundach
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            notification.remove();
        }, 200);
    }, 2000);
}

function handleResponseError(status) {
    if (status === 401) {
        document.getElementById("login-container").style.display = "flex";
        document.getElementById("main-container").style.display = "none";
        throw new Error("Twoja sesja wygasła - spróbuj zalogować się ponownie.");
    }
    throw new Error(`Błąd serwera: ${status}`);
}