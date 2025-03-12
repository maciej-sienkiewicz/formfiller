import {UI} from "../ui.js";

document.getElementById("main-container").style.display = "flex";

const productConfigButton = document.getElementById('product-configuration');

productConfigButton.addEventListener("click", () => {
    // 1. Pobieramy aktywną kartę
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        // 2. Wstrzykujemy kod, który w kontekście strony pobierze listę produktów
        chrome.scripting.executeScript(
            {
                target: { tabId: tabs[0].id },
                func: () => {
                    const spanElements = document.querySelectorAll("span");
                    const przedmiotyValues = [];

                    spanElements.forEach((span) => {
                        if (span.textContent.trim() === "Przedmioty") {
                            const parent = span.parentElement;
                            // Zakładamy, że przedmioty są w <p><p>...</p></p>
                            const przedmiotyElements = parent.querySelectorAll("p > p");
                            przedmiotyElements.forEach((przedmiot) => {
                                const value = przedmiot.textContent.trim();
                                if (value) {
                                    przedmiotyValues.push(value);
                                }
                            });
                        }
                    });

                    // Usuwamy duplikaty
                    const uniquePrzedmioty = [...new Set(przedmiotyValues)];
                    return uniquePrzedmioty;
                },
            },
            async (injectionResults) => {
                if (!injectionResults || !injectionResults.length) {
                    return;
                }

                // Produkty znalezione na stronie
                const pageItems = injectionResults[0].result || [];

                // 3. Wczytujemy z chrome.storage.local poprzednie wartości
                const { przedmiotyMap } = await chrome.storage.local.get(["przedmiotyMap"]);
                const storedItems = przedmiotyMap || {};

                // 4. Przygotowujemy listę do wyświetlenia
                //    - Najpierw produkty z `pageItems` (w takiej kolejności, jak wystąpiły)
                //    - Potem produkty, które istnieją w storage, ale nie ma ich na stronie
                const allItemsOrdered = [...pageItems];

                // Dodajemy resztę z storage (tylko te klucze, których nie ma w `pageItems`)
                Object.keys(storedItems).forEach((key) => {
                    if (!allItemsOrdered.includes(key)) {
                        allItemsOrdered.push(key);
                    }
                });

                // 5. Budujemy HTML:
                //    - klasa "product-list" na <ul> (do stylizacji)
                //    - klasa "product-row" na <li>
                let html = `
            <h3>Lista produktów</h3>
            <ul class="product-list">
          `;

                allItemsOrdered.forEach((itemName) => {
                    const refNumber = storedItems[itemName] || "";
                    // itemName – unikalna nazwa
                    // refNumber – numer referencyjny (jeśli wcześniej zapisałeś)
                    html += `
              <li class="product-row">
                <label class="product-label">${itemName}:</label>
                <input 
                  class="product-input"
                  type="text" 
                  data-key="${itemName}" 
                  value="${refNumber}"
                  placeholder="Wpisz nr referencyjny"
                  maxlength="20"
                />
              </li>
            `;
                });

                html += `
            </ul>
            <button id="save-button" class="save-button">Zapisz</button>
          `;

                // 6. Wstawiamy ten kod do popupu
                UI.updateContent(html);

                // 7. Obsługa przycisku "Zapisz"
                const saveButton = document.getElementById("save-button");
                if (saveButton) {
                    saveButton.addEventListener("click", async () => {
                        const inputs = document.querySelectorAll("input[data-key]");

                        let hasError = false;

                        // Sprawdzamy każde pole
                        inputs.forEach((inp) => {
                            const val = inp.value.trim();
                            if (val.length > 20) {
                                inp.classList.add("error-input");
                                hasError = true;
                            } else {
                                inp.classList.remove("error-input");
                            }
                        });

                        // Jeśli wystąpił błąd w którymś polu, to nie zapisujemy
                        if (hasError) {
                            alert("Nie można zapisać – co najmniej jedno pole przekracza 20 znaków. Popraw to i spróbuj ponownie.");
                            return;
                        }

                        // Jeśli nie ma błędów, dopiero teraz zapisujemy
                        const newData = { ...storedItems };
                        inputs.forEach((inp) => {
                            const key = inp.getAttribute("data-key");
                            newData[key] = inp.value.trim();
                        });

                        await chrome.storage.local.set({ przedmiotyMap: newData });
                        alert("Zapisano w storage!");
                    });
                }
            }
        );
    });
});