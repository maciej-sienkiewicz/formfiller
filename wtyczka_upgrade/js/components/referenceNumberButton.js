import { UI } from "../ui.js";

/**
 * Na początku nasłuchuj komunikatu z content scriptu:
 * Gdy user kliknie w element z aria-label="Edytuj etykiety",
 * odblokowujemy przycisk w popupie.
 */
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === "enableSetValueButton") {
        const setValueButton = document.getElementById("set-value-button");
        if (setValueButton) {
            setValueButton.disabled = false; // odblokuj
        }
    }
});

// Drugi przycisk w panelu bocznym (popupie)
const overrideButton = document.getElementById("override-reference-numbers");

if (overrideButton) {
    overrideButton.addEventListener("click", () => {
        // 1) Wstawiamy do #content panel z opisem + button "Ustaw wartość"
        UI.updateContent(`
      <div class="override-container">
        <p class="override-intro">
          Zautomatyzujemy Twój proces uzupełniania etykiet.<br/>
          Jeśli już skonfigurowałeś numery referencyjne dla produktów – możesz zacząć działać.<br/>
          Jeśli tego jeszcze nie zrobiłeś – kliknij po lewej stronie opcję 
          "<strong>Konfiguruj etykiety produktów</strong>" i nadaj oczekiwane wartości dla produktów.
        </p>
        <button id="set-value-button" class="save-button">
          Ustaw wartość
        </button>
      </div>
    `);

        // Po wstawieniu: łapiemy przycisk i podpinamy logikę
        const setValueButton = document.getElementById("set-value-button");
        if (setValueButton) {
            attachSetValueLogic(setValueButton);
        }
    });
}

/**
 * Funkcja: logika "Ustaw wartość"
 * - wstrzykuje skrypt, który pobiera `przedmiotyMap` z `chrome.storage.local`,
 * - ustawia pola `referenceNumber` i `additionalInfoOnLabel`,
 * - zmienia etykietę przycisku na "Zapisz wartość" i podmienia listener.
 */
function attachSetValueLogic(buttonEl) {
    function handleSetValueClick() {
        // Pobieramy z local storage zmapowane przedmioty
        chrome.storage.local.get(["przedmiotyMap"], (res) => {
            const storedMap = res.przedmiotyMap || {};

            // Wstrzykujemy kod do aktywnej karty
            chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
                chrome.scripting.executeScript(
                    {
                        target: { tabId: tabs[0].id },
                        func: async (map) => {
                            // Szukamy w DOM "Przedmioty"
                            const spanElements = document.querySelectorAll("span");
                            const allProducts = [];
                            spanElements.forEach((span) => {
                                if (span.textContent.trim() === "Przedmioty") {
                                    const parent = span.parentElement;
                                    const przedmiotyElements = parent.querySelectorAll("p > p");
                                    przedmiotyElements.forEach((przedmiot) => {
                                        const value = przedmiot.textContent.trim();
                                        if (value) {
                                            allProducts.push(value);
                                        }
                                    });
                                }
                            });

                            if (!allProducts.length) {
                                return "No products";
                            }

                            // Dla uproszczenia weź pierwszy produkt:
                            const spanElement = Array.from(document.querySelector("h5")?.parentElement?.querySelectorAll("span") || [])
                                .find(span => span.textContent.trim() === "Przedmioty");

                            const firstProduct = spanElement?.parentElement?.querySelector("p")?.textContent.trim();
                            const storedValue = map[firstProduct] || "";

                            // Pola na stronie
                            const referenceInput = document.querySelector(
                                'input[name="referenceNumber"]'
                            );
                            const additionalInfoInput = document.querySelector(
                                'input[name="additionalInfoOnLabel"]'
                            );
                            const saveBtn = Array.from(document.querySelectorAll("button")).find(
                                (b) => b.textContent.trim() === "Zapisz"
                            );

                            if (!referenceInput || !additionalInfoInput || !saveBtn) {
                                return "No fields or save button found";
                            }


                            referenceInput.value = storedValue;
                            referenceInput.dispatchEvent(new Event('input', { bubbles: true }));

                            await new Promise(resolve => setTimeout(resolve, 300));

                            additionalInfoInput.focus();
                            additionalInfoInput.value = storedValue;
                            additionalInfoInput.dispatchEvent(new Event("input", { bubbles: true }));

                            return "done";
                        },
                        args: [storedMap],
                    },
                    (injectionResults) => {
                        if (chrome.runtime.lastError) {
                            console.error(chrome.runtime.lastError.message);
                            return;
                        }
                        const result = injectionResults[0]?.result;

                        if (result === "No fields or save button found" || result === "No products") {
                            // Jeśli nie ma pól albo nie ma przedmiotów, możesz ewentualnie zablokować przycisk
                            // (Jak w poprzednich przykładach), plus wstrzyknąć nasłuch w "aria-label=Zmodyfikuj zamówienie" itd.
                            // Pomijam tutaj – bo to zależy, co chcesz zrobić w tym przypadku.
                            return;
                        }

                        // Jeśli "done" – zmieniamy napis na "Zapisz wartość"
                        buttonEl.classList.remove("save-button");       // usuń niebieską
                        buttonEl.classList.add("save-button-green");    // dodaj zieloną
                        buttonEl.textContent = "Zapisz wartość";                        // Usuwamy stary listener
                        buttonEl.removeEventListener("click", handleSetValueClick);

                        // Podpinamy logikę "Zapisz wartość"
                        attachSaveValueLogic(buttonEl);
                    }
                );
            });
        });
    }

    buttonEl.addEventListener("click", handleSetValueClick);
}

/**
 * Funkcja: logika "Zapisz wartość"
 * - klika w "Zapisz" na stronie,
 * - po 200 ms sprawdza, czy `referenceInput` istnieje,
 *   jeśli nie -> wyłącza przycisk i nasłuchuje kliknięcia "Edytuj etykiety",
 *   jeśli tak -> przycisk nadal aktywny.
 */
function attachSaveValueLogic(buttonEl) {
    function handleSaveValueClick() {
        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            // 1) Wstrzykujemy skrypt, który klika "Zapisz"
            chrome.scripting.executeScript(
                {
                    target: { tabId: tabs[0].id },
                    func: () => {
                        const saveButton = Array.from(document.querySelectorAll("button")).find(
                            (btn) => btn.textContent.trim() === "Zapisz"
                        );
                        if (saveButton) {
                            saveButton.click();
                            return "saveClicked";
                        }
                        return "noSaveButtonFound";
                    }
                },
                () => {
                    // 2) Przywracamy tekst
                    buttonEl.classList.remove("save-button-green");
                    buttonEl.classList.add("save-button");
                    buttonEl.textContent = "Ustaw wartość";

                    // 3) Usuwamy listener do "Zapisz wartość"
                    buttonEl.removeEventListener("click", handleSaveValueClick);

                    // 4) Po 200 ms sprawdzamy, czy referenceInput istnieje
                    setTimeout(() => {
                        chrome.scripting.executeScript(
                            {
                                target: { tabId: tabs[0].id },
                                func: () => {
                                    return !!document.querySelector('input[name="referenceNumber"]');
                                }
                            },
                            (res) => {
                                const stillExists = res?.[0]?.result; // boolean

                                if (!stillExists) {
                                    // 5) Wyłącz przycisk
                                    buttonEl.disabled = true;

                                    // 6) Wstrzyknij listener, który nasłuchuje "Edytuj etykiety"
                                    chrome.scripting.executeScript({
                                        target: { tabId: tabs[0].id },
                                        func: () => {
                                            document.body.addEventListener("click", (e) => {
                                                // Używamy closest, bo to div z aria-label
                                                const el = e.target.closest('div[aria-label="Zmodyfikuj zamówienie"]');
                                                if (el) {
                                                    // Wysyłamy komunikat do popupu
                                                    chrome.runtime.sendMessage({ type: "enableSetValueButton" });
                                                }
                                            }, { capture: true });
                                        }
                                    });
                                }

                                // 7) Przywracamy logikę "Ustaw wartość"
                                attachSetValueLogic(buttonEl);
                            }
                        );
                    }, 200);
                }
            );
        });
    }
    buttonEl.addEventListener("click", handleSaveValueClick);
}
