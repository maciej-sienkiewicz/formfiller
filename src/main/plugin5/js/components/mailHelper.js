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
        UI.deactivateAllButtons();
        mailHelperButton.classList.add("active");
        UI.updateContent(`
            <div id="form-container">
                 <div class="form-group switch-group">
                    <label class="switch-label">Czy dodać kontekst poprzedniej wiadomości?</label>
                    <label class="switch">
                      <input type="checkbox" id="context-switch">
                      <span class="slider"></span>
                    </label>
                </div>
                <div class="form-group context-container" id="context-container" style="display: none;">
                    <label for="context-text" class="context-label">Kontekst poprzedniej wiadomości:</label>
                    <textarea id="context-text" class="alert-issue-input context-textarea" placeholder="Wpisz kontekst..."></textarea>
                </div>
                <!-- Kontener na opcję tłumaczenia – pojawi się dynamicznie -->
                <div id="translation-container"></div>
            </div>
            <div id="response-container" style="display: none;"></div>
                <div class="form-group">
                    <label for="issue-text">Jaką odpowiedź przygotować?</label>
                    <textarea id="issue-text" class="alert-issue-input" placeholder="Wpisz treść wiadomości..."></textarea>
                </div>
                <div class="form-group">
                    <label for="language-select">W jakim języku ma być wiadomość?</label>
                    <select id="language-select" class="alert-issue-input">
                        <option value="pl">Polski</option>
                        <option value="en">Angielski</option>
                        <option value="de">Niemiecki</option>
                    </select>
                </div>
                <div class="form-group">
                    <button id="send-request" class="alert-issue-button">Wyślij</button>
                </div>
        `);



        // Obsługa checkboxa – po zaznaczeniu wyświetlamy kontener kontekstu
        // oraz dodajemy opcję tłumaczenia poniżej pola kontekstu.
        document.getElementById("context-switch").addEventListener("change", async (event) => {
            const token = await new Promise((resolve, reject) => {
                chrome.storage.local.get("jwtToken", (result) => {
                    if (chrome.runtime.lastError) {
                        reject(chrome.runtime.lastError);
                    } else {
                        resolve(result.jwtToken);
                    }
                });
            });

            const contextContainer = document.getElementById("context-container");
            if (event.target.checked) {
                contextContainer.style.display = "flex";
                contextContainer.style.flexDirection = "column";
                contextContainer.style.alignItems = "center";
                let translationContainer = document.getElementById("translation-container");
                if (!translationContainer.innerHTML.trim()) {
                    translationContainer.innerHTML = `
                        <div class="form-group" id="translation-controls">
                            <label for="translation-label">Czy chcesz przetłumaczyć na język polski?</label>
                            <button id="translate-btn" class="alert-issue-button">Przetłumacz</button>
                        </div>
                        <div id ="translation-spinner"></div>
                        <div id="translation-response-container" class="response-container" style="margin-top:10px;"></div>
                    `;
                    const translateBttn = document.getElementById("translate-btn")
                    translateBttn.addEventListener("click", async () => {
                        const textToTranslate = document.getElementById("context-text").value;
                        document.getElementById("translation-spinner").innerHTML =
                            `<div class="spinner"></div>`;
                        try {
                            document.getElementById("context-text")
                            const response = await fetch("http://localhost:20266/api/form-filler/email-helper/translation", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${token}`
                                },
                                body: JSON.stringify({request: textToTranslate})
                            });
                            if (!response.ok) {
                                if (response.status === 401) {
                                    console.warn("⛔ Nieprawidłowe dane logowania (401)");
                                    document.getElementById("login-container").style.display = "flex";
                                    document.getElementById("main-container").style.display = "none";
                                    throw new Error(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
                                }
                                throw new Error("Błąd serwera: " + response.status);
                            }
                            const translationText = await response.text();
                            document.getElementById("translation-spinner").innerHTML = ""
                            const translationResponseContainer = document.getElementById("translation-response-container");
                            translationResponseContainer.innerHTML = translationText;
                            translationResponseContainer.style.display = "block";
                        } catch (error) {
                            alert(error.body);
                            console.error("Błąd: ", error);
                        }
                    });
                } else {
                    translationContainer.style.display = "block";
                }
            } else {
                contextContainer.style.display = "none";
                document.getElementById("context-text").value = "";
                const translationContainer = document.getElementById("translation-container");
                if (translationContainer) {
                    translationContainer.style.display = "none";
                }
            }
        });

        document.getElementById("send-request").addEventListener("click", async () => {
            const issueText = document.getElementById("issue-text").value;
            const language = document.getElementById("language-select").value;
            const contextSwitch = document.getElementById("context-switch").checked;
            const contextText = contextSwitch ? document.getElementById("context-text").value : null;

            if (!issueText) {
                alert("Proszę wpisać treść maila.");
                return;
            }

            UI.updateContent(`<div id="loading-spinner" class="spinner"></div>`);

            try {
                const token = await new Promise((resolve, reject) => {
                    chrome.storage.local.get("jwtToken", (result) => {
                        if (chrome.runtime.lastError) {
                            reject(chrome.runtime.lastError);
                        } else {
                            resolve(result.jwtToken);
                        }
                    });
                });

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
                    if (response.status === 401) {
                        console.warn("⛔ Nieprawidłowe dane logowania (401)");
                        document.getElementById("login-container").style.display = "flex";
                        document.getElementById("main-container").style.display = "none";
                        alert(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
                        throw new Error(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
                    }
                    throw new Error("Błąd serwera");
                }

                const data = await response.json();

                UI.updateContent(`
                    <div id="response-container" class="response-container">${data.content}</div>
                         <div class="form-group" id="result-translation-controls">
                            <label for="result-translation-label">Czy chcesz przetłumaczyć na język polski?</label>
                            <button id="result-translate-btn" class="alert-issue-button">Przetłumacz</button>
                        </div>
                        <div id="translation-response-container" class="response-container" style="margin-top:10px;"></div>
                    <div class="button-container">
                        <button id="copy-text" class="copy-button">Kopiuj</button>
                        <button id="restart" class="alert-issue-button">Zacznij od nowa</button>
                    </div>
                `);

                let resultTranslationButton = document.getElementById("result-translate-btn");
                resultTranslationButton.addEventListener("click", async () => {
                    const textToTranslate = data.content
                    if (!textToTranslate) {
                        alert("Proszę wpisać treść do przetłumaczenia");
                        return;
                    }
                    try {
                        const response = await fetch("http://localhost:20266/api/form-filler/email-helper/translation", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                "Authorization": `Bearer ${token}`
                            },
                            body: JSON.stringify({ request: textToTranslate })
                        });
                        if (!response.ok) {
                            if (response.status === 401) {
                                console.warn("⛔ Nieprawidłowe dane logowania (401)");
                                document.getElementById("login-container").style.display = "flex";
                                document.getElementById("main-container").style.display = "none";
                                throw new Error(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
                            }
                            throw new Error("Błąd serwera: " + response.status);
                        }
                        const translationText = await response.text();
                        const translationResponseContainer = document.getElementById("translation-response-container");
                        translationResponseContainer.innerHTML = translationText;
                        translationResponseContainer.style.display = "block";
                    } catch (error) {
                        alert(error);
                        console.error("Błąd: ", error.body);
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
                    alert("Skopiowano czysty tekst do schowka!");
                });

                document.getElementById("restart").addEventListener("click", () => {
                    mailHelperButton.click();
                });
            } catch (error) {
                alert("Wystąpił błąd podczas wysyłania żądania. Spróbuj ponownie.");
                console.error("Błąd: ", error);
            }
        });
        }});
}
