import { UI } from "../ui.js";

const smsSenderButton = document.getElementById("sms-sender");

if (smsSenderButton) {
    smsSenderButton.addEventListener("click", () => {
        if (smsSenderButton.classList.contains('active')) {
            smsSenderButton.classList.remove('active');
            UI.resetContent();
        } else {
            UI.deactivateAllButtons();
            smsSenderButton.classList.add("active");
            chrome.storage.local.get(["userName", "userPhone"], async (result) => {
                const storedName = result.userName ? result.userName : "Jan Kowalski";
                const storedPhone = result.userPhone ? result.userPhone : "+48 123 456 789";
                UI.updateContent(`
                    <div id="sms-form-container">
                        <div class="form-group">
                            <label for="user-name">Twoje imię</label>
                            <div class="editable-field">
                                <!-- Pola użytkownika są read-only i szare -->
                                <input type="text" id="user-name" value="${storedName}" readonly style="background-color: #e0e0e0;" />
                                <button id="edit-user-name" class="edit-btn">✏️</button>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="user-phone">Twój numer telefonu</label>
                            <div class="editable-field">
                                <input type="text" id="user-phone" value="${storedPhone}" readonly style="background-color: #e0e0e0;" />
                                <button id="edit-user-phone" class="edit-btn">✏️</button>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="template-select">Wybierz szablon SMS</label>
                            <select id="template-select" class="form-control"></select>
                        </div>
                        <div class="form-group">
                            <label>Numer odbiorcy</label>
                            <div id="recipient-phones-container">
                                <div class="editable-field recipient-field">
                                    <!-- Pole odbiorcy – zawsze białe -->
                                    <input type="text" id="recipient-phone-0" placeholder="Wpisz numer odbiorcy" class="form-control recipient-phone" style="background-color: #ffffff;" />
                                    <button id="add-recipient-phone-0" class="add-btn">+</button>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <button id="send-sms" class="send-btn" disabled>Wyślij</button>
                        </div>
                        <div class="form-group">
                            <p>Tak będzie wyglądała Twoja wiadomość:</p>
                            <div id="template-preview"></div>
                        </div>
                    </div>
                    <div id="sms-response-container" style="display: none;"></div>
                `);

                let smsTemplates = [];

                // Aktualizacja podglądu wiadomości z podstawieniem zmiennych.
                const updateMessagePreview = () => {
                    const templateSelect = document.getElementById("template-select");
                    const selectedId = Number(templateSelect.value);
                    const selectedTemplate = smsTemplates.find(t => t.id === selectedId);
                    if (!selectedTemplate) {
                        document.getElementById("template-preview").textContent = "";
                        return;
                    }
                    let previewText = selectedTemplate.content;
                    const userName = document.getElementById("user-name").value.trim();
                    const userPhone = document.getElementById("user-phone").value.trim();
                    const recipientInputs = document.querySelectorAll("#recipient-phones-container .recipient-phone");
                    const recipientNumbers = Array.from(recipientInputs)
                        .map(input => input.value.trim())
                        .filter(val => val !== "");

                    chrome.tabs.query({active: true, currentWindow: true}, (tabs) => {
                        if (!tabs || !tabs[0]) return;
                        chrome.scripting.executeScript(
                            {
                                target: {tabId: tabs[0].id},
                                func: () => {
                                    // W kontekście strony – pobierz element zakładek
                                    const candidatesTabs = document.getElementById("recrutations-tabs");
                                    if (!candidatesTabs) return null;
                                    // Znajdź aktywną zakładkę
                                    const selectedTab = candidatesTabs.querySelector('li a[aria-selected="true"]');
                                    if (!selectedTab) return null;
                                    const currentTab = document.getElementById(selectedTab.getAttribute("aria-controls"));

                                    const job = currentTab.querySelector('[id^="profession_id_"][id$="_chosen"] .chosen-single span')?.textContent || "";
                                    const rateEls = currentTab.querySelectorAll('[id^="rate_"]');
                                    const price = rateEls[0].value;

                                    const rateGrossNetSelect = currentTab.querySelector('[id^="rate_gross_net_id_"]');
                                    const rate = rateGrossNetSelect
                                        ? rateGrossNetSelect.options[rateGrossNetSelect.selectedIndex].text
                                        : "";
                                    const rateTimeSelect = currentTab.querySelector('[id^="rate_time_id_"]');
                                    const rateTime = rateTimeSelect
                                        ? rateTimeSelect.options[rateTimeSelect.selectedIndex].text
                                        : "";
                                    const currencySpanEl = currentTab.querySelector('[id^="currency_id_"][id$="_chosen"] .chosen-single span');
                                    const currencySpan = currencySpanEl ? currencySpanEl.textContent : "";

                                    const dietInputEl = currentTab.querySelector('[id^="diet_"]');
                                    const dietInput = dietInputEl ? dietInputEl.value : "";

                                    const dietGrossNetSelect = currentTab.querySelector('[id^="diet_gross_net_id_"]');
                                    const dietGrossNet = dietGrossNetSelect
                                        ? dietGrossNetSelect.options[dietGrossNetSelect.selectedIndex].text
                                        : "";
                                    const dietTimeSelect = currentTab.querySelector('[id^="diet_time_id_"]');
                                    const dietTime = dietTimeSelect
                                        ? dietTimeSelect.options[dietTimeSelect.selectedIndex].text
                                        : "";

                                    const countryChosenDiv = currentTab.querySelectorAll('[id^="country_id_"][id$="_chosen"]')[0];
                                    let countrySpan = "";
                                    if (countryChosenDiv) {
                                        const sp = countryChosenDiv.querySelector('.chosen-single span');
                                        countrySpan = sp ? sp.textContent : "";
                                    }
                                    const cityInputEl = currentTab.querySelector('[id^="city_"]');
                                    const cityInput = cityInputEl ? cityInputEl.value : "";

                                    // Zwracamy obiekt z danymi do wtyczki.
                                    return {
                                        job,
                                        price,
                                        rate,
                                        rateTime,
                                        currencySpan,
                                        dietInput,
                                        dietGrossNet,
                                        dietTime,
                                        countrySpan,
                                        cityInput
                                    };
                                },
                            },
                            (results) => {
                                if (results && results[0] && results[0].result) {
                                    // Możesz użyć destrukturyzacji:
                                    const {
                                        job,
                                        price,
                                        rate,
                                        rateTime,
                                        currencySpan,
                                        dietInput,
                                        dietGrossNet,
                                        dietTime,
                                        countrySpan,
                                        cityInput,
                                    } = results[0].result;

                                    // Albo zaktualizować jakiś element w interfejsie wtyczki:
                                    const templatePreview = document.getElementById("template-preview");
                                    if (templatePreview) {
                                        var dietInfo = ""
                                        if (dietInput && dietInput.trim() !== "") {
                                            dietInfo = `Dodatkowo dieta w postaci: ${dietInput} / ${dietTime} (${dietGrossNet}).`;
                                        }

                                        templatePreview.textContent = previewText
                                            .replace("{{NAME}}", userName)
                                            .replace("{{PHONE_NUMBER}}", userPhone)
                                            .replace("{{JOB}}", job)
                                            .replace("{{CURRENCY}}", currencySpan)
                                            .replace("{{RATE}}", rate)
                                            .replace("{{PRICE}}", price)
                                            .replace("{{RATE_TIME}}", rateTime)
                                            .replace("{{CITY}}", cityInput)
                                            .replace("{{COUNTRY}}", countrySpan)
                                            .replace("{{DIET}}", dietInfo)
                                    }
                                }
                            }
                        );
                    });

                    const templatePreview = document.getElementById("template-preview");
                    templatePreview.textContent = previewText
                        .replace("{{NAME}}", userName)
                        .replace("{{PHONE_NUMBER}}", userPhone)
                };

                const token = await new Promise((resolve, reject) => {
                    chrome.storage.local.get("jwtToken", (result) => {
                        if (chrome.runtime.lastError) {
                            reject(chrome.runtime.lastError);
                        } else {
                            resolve(result.jwtToken);
                        }
                    });
                });

                // Pobieranie szablonów SMS.
                fetch("http://localhost:20266/api/form-filler/sms/templates", {
                    headers: {"Authorization": `Bearer ${token}`},
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log(smsTemplates)
                        smsTemplates = data.elements;
                        const templateSelect = document.getElementById("template-select");
                        templateSelect.innerHTML = "";
                        smsTemplates.forEach(template => {
                            const option = document.createElement("option");
                            option.value = template.id;
                            option.textContent = template.name;
                            templateSelect.appendChild(option);
                        });
                        updateMessagePreview();
                        templateSelect.addEventListener("change", updateMessagePreview);
                    })
                    .catch(error => console.error("Error fetching SMS templates:", error));

                // Nasłuch zmian w polach
                document.getElementById("user-name").addEventListener("input", updateMessagePreview);
                document.getElementById("user-phone").addEventListener("input", updateMessagePreview);
                document.getElementById("recipient-phone-0").addEventListener("input", updateMessagePreview);

                const checkEditing = () => {
                    const userNameInput = document.getElementById("user-name");
                    const userPhoneInput = document.getElementById("user-phone");
                    const sendButton = document.getElementById("send-sms");
                    if (!userNameInput.hasAttribute("readonly") || !userPhoneInput.hasAttribute("readonly")) {
                        sendButton.disabled = true;
                        sendButton.style.opacity = "0.6";
                    } else {
                        sendButton.disabled = false;
                        sendButton.style.opacity = "1";
                    }
                };

                const disableOthers = (currentInputId) => {
                    const container = document.getElementById("sms-form-container");
                    const interactives = container.querySelectorAll("input, select, button");
                    interactives.forEach(el => {
                        if (el.id !== currentInputId && el.id !== `edit-${currentInputId}`) {
                            el.disabled = true;
                            el.style.opacity = "0.6";
                        }
                    });
                };

                const enableAll = () => {
                    const container = document.getElementById("sms-form-container");
                    const interactives = container.querySelectorAll("input, select, button");
                    interactives.forEach(el => {
                        el.disabled = false;
                        el.style.opacity = "1";
                    });
                    // Przywracamy nasłuch na inputy
                    document.getElementById("user-name").addEventListener("input", updateMessagePreview);
                    document.getElementById("user-phone").addEventListener("input", updateMessagePreview);
                    const recipientInputs = document.querySelectorAll("#recipient-phones-container .recipient-phone");
                    recipientInputs.forEach(input => {
                        input.addEventListener("input", updateMessagePreview);
                    });
                };

                // Funkcja zmieniająca tryb edycji dla pól użytkownika.
                const toggleEdit = (inputId, buttonId) => {
                    const input = document.getElementById(inputId);
                    const button = document.getElementById(buttonId);
                    button.addEventListener("click", () => {
                        if (input.hasAttribute("readonly")) {
                            // Tryb edycji – usuwamy readonly i ustawiamy tło na białe.
                            input.removeAttribute("readonly");
                            if (input.id === "user-name" || input.id === "user-phone") {
                                input.style.backgroundColor = "#ffffff";
                            }
                            button.textContent = "💾";
                            disableOthers(inputId);
                        } else {
                            // Zapis – przywracamy readonly i tło na szare (dla pól użytkownika).
                            input.setAttribute("readonly", "true");
                            if (input.id === "user-name" || input.id === "user-phone") {
                                input.style.backgroundColor = "#e0e0e0";
                            }
                            button.textContent = "✏️";
                            const key = inputId === "user-name" ? "userName" : inputId === "user-phone" ? "userPhone" : null;
                            if (key) {
                                let obj = {};
                                obj[key] = input.value;
                                chrome.storage.local.set(obj, () => {
                                });
                            }
                            enableAll();
                        }
                        checkEditing();
                        updateMessagePreview();
                    });
                };

                toggleEdit("user-name", "edit-user-name");
                toggleEdit("user-phone", "edit-user-phone");
                checkEditing();

                // --- Logika dodawania/usuwania pól numerów odbiorców ---
                let recipientCount = 1; // indeks początkowego pola = 0

                // Upewnij się, że ostatni wiersz posiada przycisk "+"
                const updateLastRowButton = () => {
                    const container = document.getElementById("recipient-phones-container");
                    const allRows = container.querySelectorAll(".recipient-field");
                    if (allRows.length > 0) {
                        const lastRow = allRows[allRows.length - 1];
                        const existingButton = lastRow.querySelector("button");
                        if (!existingButton || existingButton.textContent !== "+") {
                            if (existingButton) {
                                existingButton.remove();
                            }
                            const plusButton = document.createElement("button");
                            plusButton.className = "add-btn";
                            plusButton.textContent = "+";
                            plusButton.addEventListener("click", addRecipientField);
                            lastRow.appendChild(plusButton);
                        }
                    }
                };

                const addRecipientField = () => {
                    const container = document.getElementById("recipient-phones-container");
                    const lastField = container.querySelector(".recipient-field:last-child");
                    const plusButton = lastField.querySelector("button.add-btn");
                    if (plusButton) {
                        // Zamień przycisk "+" na "-" umożliwiający usunięcie tego wiersza.
                        plusButton.remove();
                        const minusButton = document.createElement("button");
                        minusButton.className = "add-btn";
                        minusButton.textContent = "-";
                        minusButton.addEventListener("click", () => {
                            lastField.remove();
                            updateLastRowButton();
                        });
                        lastField.appendChild(minusButton);
                    }

                    // Dodaj nowy wiersz z polem i przyciskiem "+"
                    const newField = document.createElement("div");
                    newField.classList.add("editable-field", "recipient-field");
                    const input = document.createElement("input");
                    input.type = "text";
                    input.id = `recipient-phone-${recipientCount}`;
                    input.placeholder = "Wpisz numer odbiorcy";
                    input.className = "form-control recipient-phone";
                    input.style.backgroundColor = "#ffffff";
                    input.addEventListener("input", updateMessagePreview);
                    const newPlusButton = document.createElement("button");
                    newPlusButton.className = "add-btn";
                    newPlusButton.textContent = "+";
                    newPlusButton.addEventListener("click", addRecipientField);
                    newField.appendChild(input);
                    newField.appendChild(newPlusButton);
                    container.appendChild(newField);
                    recipientCount++;
                };

                document.getElementById("add-recipient-phone-0").addEventListener("click", addRecipientField);

                document.getElementById("send-sms").addEventListener("click", async () => {
                    const templateSelect = document.getElementById("template-select");
                    const templateId = parseInt(templateSelect.value, 10);
                    const sender = document.getElementById("user-phone").value.trim();
                    const name = document.getElementById("user-name").value.trim();

                    const recipientInputs = document.querySelectorAll("#recipient-phones-container .recipient-phone");
                    const recipientNumbers = Array.from(recipientInputs)
                        .map(input => input.value.trim())
                        .filter(val => val !== "");

                    if (!sender || recipientNumbers.length === 0) {
                        alert("Wypełnij wymagane pola.");
                        return;
                    }

                    UI.updateContent(`<div id="loading-spinner" class="spinner"></div>`);

                    try {
                        const response = await fetch("http://localhost:20266/api/form-filler/sms/send", {
                            method: "POST",
                            headers: {"Content-Type": "application/json", "Authorization": `Bearer ${token}`},
                            body: JSON.stringify({
                                name: name,
                                templateId: templateId,
                                sender: sender,
                                recipients: recipientNumbers
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

                        const data = await response.text();

                        UI.updateContent(`
                            <div id="sms-response-container">${data}</div>
                            <div class="button-container">
                                <button id="restart-sms" class="send-btn">Zacznij od nowa</button>
                            </div>
                        `);

                        document.getElementById("restart-sms").addEventListener("click", () => {
                            smsSenderButton.click();
                        });
                    } catch (error) {
                        alert("Wystąpił błąd podczas wysyłania SMS. Spróbuj ponownie.");
                        console.error("Błąd: ", error);
                    }
                });
            });
        }
    });
}
