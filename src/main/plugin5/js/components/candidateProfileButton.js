import { UI } from "../ui.js";

const candidateProfileButton = document.getElementById('candidate-profile-button');

candidateProfileButton.addEventListener("click", () => {
    if (candidateProfileButton.classList.contains('active')) {
        candidateProfileButton.classList.remove('active');
        UI.resetContent();
        return;
    }

    UI.deactivateAllButtons();
    candidateProfileButton.classList.add("active");
    showUploadInterface();
});

function showUploadInterface() {
    UI.updateContent(`
        <div id="instructions-container">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fa-solid fa-user-tie"></i>
                </div>
                <div>
                    <h3 class="section-title">Profil kandydata</h3>
                    <p class="section-subtitle">Szybko wypełnij profil na podstawie CV</p>
                </div>
            </div>
            
            <button id="upload-file-btn" class="btn-primary">
                <i class="fa-solid fa-cloud-upload-alt"></i> Prześlij plik CV
            </button>
            
            <h3>Instrukcja korzystania z wtyczki</h3>
            <ol>
                <li>
                    <strong>Otwórz formularz kandydata:</strong>
                    Ta opcja działa poprawnie wyłącznie w sytuacji, gdy masz już otwarty widok z formularzem.
                </li>
                <li>
                    <strong>Upewnij się, że formularz jest pusty:</strong>
                    Poza automatycznie uzupełnionymi danymi wrażliwymi pozostałe pola powinny pozostać niewypełnione.
                    W przeciwnym razie ręcznie wprowadzone informacje mogą zostać nadpisane lub spowodować błędy.
                </li>
                <li>
                    <strong>Reaguj na ewentualne błędy:</strong>
                    Jeśli dane zostały przetworzone niepoprawnie, popraw je ręcznie lub otwórz nową kartę i spróbuj jeszcze raz.
                </li>
                <li>
                    <strong>Obsługiwane formaty plików:</strong>
                    .pdf, .docx, .doc, .rtf
                </li>
                <li>
                    <strong>Pamiętaj:</strong>
                    Jest to tylko program komputerowy i nie ponosi odpowiedzialności za ewentualne błędy w danych.
                </li>
            </ol>
        </div>
    `);

    setupFileUpload();
}

function setupFileUpload() {
    const uploadFileBtn = document.getElementById("upload-file-btn");
    uploadFileBtn.addEventListener("click", () => {
        const fileInput = createFileInput();
        fileInput.click();
        fileInput.addEventListener("change", handleFileUpload);
    });
}

function createFileInput() {
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.style.display = "none";
    fileInput.accept = ".pdf,.docx,.doc,.rtf";
    document.body.appendChild(fileInput);
    return fileInput;
}

async function handleFileUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    UI.updateContent(`
        <div class="processing-container">
            <div class="spinner"></div>
            <p>Rozpoczynam przetwarzanie pliku...</p>
            <div class="progress-bar">
                <div class="progress-fill"></div>
            </div>
        </div>
    `);

    // Animacja paska postępu
    const progressFill = document.querySelector('.progress-fill');
    progressFill.style.width = '0%';

    setTimeout(() => {
        progressFill.style.width = '80%';
        progressFill.style.transition = 'width 2s ease-in-out';
    }, 100);

    try {
        const token = await getToken();
        const result = await uploadFile(file, token);
        await processFormData(result, token);

        progressFill.style.width = '100%';

        setTimeout(() => {
            UI.updateContent(`
                <div class="section-header">
                    <div class="section-icon">
                        <i class="fa-solid fa-check-circle"></i>
                    </div>
                    <div>
                        <h3 class="section-title">Profil kandydata uzupełniony</h3>
                        <p class="section-subtitle">Upewnij się, że wszystkie dane zostały wprowadzone poprawnie</p>
                    </div>
                </div>
                
                <div class="alert alert-success">
                    <i class="fa-solid fa-check-circle"></i>
                    <div>Dane z CV zostały pomyślnie przetworzone i wprowadzone do formularza.</div>
                </div>
                
                <button id="upload-another-btn" class="btn-primary">
                    <i class="fa-solid fa-cloud-upload-alt"></i> Prześlij inny plik
                </button>
            `);

            document.getElementById("upload-another-btn").addEventListener("click", () => {
                candidateProfileButton.click();
            });
        }, 300);

        await updateStats(token);
    } catch (error) {
        handleError(error);
    }
}

async function getToken() {
    return new Promise((resolve, reject) => {
        chrome.storage.local.get("jwtToken", (result) => {
            if (chrome.runtime.lastError) reject(chrome.runtime.lastError);
            else resolve(result.jwtToken);
        });
    });
}

async function uploadFile(file, token) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch("http://localhost:20266/api/form-filler/upload", {
        method: "POST",
        body: formData,
        headers: { "Authorization": `Bearer ${token}` },
    });

    if (!response.ok) {
        handleHttpError(response.status);
    }

    const candidateProfile = await response.json();
    return candidateProfile;
}

async function processFormData(data, token) {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        chrome.scripting.executeScript({
            target: { tabId: tabs[0].id },
            func: (formData) => {
                const candidatesTabs = document.getElementById("candidates-tabs");
                const selectedTab = candidatesTabs
                    .querySelector('li a[aria-selected="true"]')
                    ?.closest("li")
                    .querySelector('a');
                const ariaControlsValue = selectedTab.getAttribute("aria-controls");
                const currentTab = document.querySelector(`div[role="tabpanel"]#${ariaControlsValue}`);

                const flexDiv = Array.from(currentTab.querySelectorAll("h1"))
                    .find((header) => header.textContent.trim() === "FLEXIBILITY")
                    .parentElement;

                const valuesToSelect = formData.flexibility.value.driverLicence.value

                const selectElement = flexDiv.querySelector('select[id^="flexibility"]');

                if (selectElement) {
                    Array.from(selectElement.options).forEach(option => {
                        option.selected = false;
                    });

                    valuesToSelect.forEach(value => {
                        const optionToSelect = Array.from(selectElement.options).find(
                            option => option.text.trim() === value
                        );
                        if (optionToSelect) {
                            optionToSelect.selected = true;
                        }
                    });

                    selectElement.dispatchEvent(new Event('chosen:updated', {bubbles: true}));
                }

                const personalInfo = formData.personalInfo;
                const fieldMappings = {
                    firstName: "candidate[first_name]",
                    lastName: "candidate[last_name]",
                    citizenship: "profile[nationality_id]",
                    birthDate: "candidate[birth_date]",
                    birthPlace: "profile[birthplace]",
                    email: "profile[mail]",
                    phoneCode: "profile[phone_code_id]",
                    phoneNumber: "profile[phone]",
                    building: "candidate[building]",
                    local: "candidate[local]",
                    street: "candidate[street]",
                    postalCode: "candidate[postcode]",
                    city: "candidate[city]",
                };

                for (const field in personalInfo) {
                    const fieldData = personalInfo[field];
                    const value = fieldData.value;

                    if (!value) {
                        continue;
                    }

                    const formFieldName = fieldMappings[field];
                    if (!formFieldName) continue;

                    const formElements = currentTab.querySelectorAll(`[name="${formFieldName}"]`);
                    if (formElements.length === 0) continue;

                    const formElement = formElements[0];
                    if (formElement.tagName.toLowerCase() === "select") {
                        Array.from(formElement.options).forEach((option) => {
                            if (option.textContent.trim() === value || option.value === value) {
                                formElement.value = option.value;
                            }
                        });
                    } else {
                        formElement.value = value;
                    }
                }

                const languageData = formData.languages.value || [];
                const addLanguageButton = currentTab.querySelector(".option-add-language");

                for (let i = 0; i < languageData.length; i++) {
                    addLanguageButton.click();
                }

                const languageFields = Array.from(currentTab.querySelectorAll("select[name='languages[]']"));
                const languageLevelFields = Array.from(currentTab.querySelectorAll("select[name='languages_levels[]']"));

                const startIndex = 2;

                languageData.forEach((lang, index) => {
                    const languageField = languageFields[startIndex + index];
                    const languageLevelField = languageLevelFields[startIndex + index];

                    const languageOption = Array.from(languageField.options).find(option => option.text.trim() === lang.name);
                    if (languageOption) {
                        languageField.value = languageOption.value;
                    }

                    const languageLevelOption = Array.from(languageLevelField.options).find(option => option.text.trim() === lang.languageLevel);
                    if (languageLevelOption) {
                        languageLevelField.value = languageLevelOption.value;
                    }

                    [languageField, languageLevelField].forEach(field => {
                        const event = new Event("chosen:updated", {bubbles: true});
                        field.dispatchEvent(event);
                    });
                });

                const experienceData = formData.professionalExperience.value || [];
                const professionDiv = Array.from(currentTab.querySelectorAll("h1"))
                    .find((header) => header.textContent.trim() === "PROFESSIONAL EXPERIENCE")
                    .parentElement;

                for (let i = 0; i < experienceData.length; i++) {
                    currentTab.querySelectorAll(".option-add-tpl")[1].click();
                }

                const dateFromInputs = professionDiv.querySelectorAll("input[class='form-control'][name$='[from]']");
                const dateToInputs = professionDiv.querySelectorAll("input[class='form-control'][name$='[to]']");
                const cityInputs = professionDiv.querySelectorAll("input[class='experience-city form-control']");
                const companyInputs = professionDiv.querySelectorAll("input[class='form-control'][name$='[company]']");
                const professionFields = professionDiv.querySelectorAll("select[class='profilemaker-experience-jobs form-control chosen-select']");
                const countryFields = professionDiv.querySelectorAll("select[class='experience-country form-control chosen-select']");
                const professionsDe = professionDiv.querySelectorAll("input[name^='experience'][name$='[en][job_other]']");
                const professionsEng = professionDiv.querySelectorAll("input[name^='experience'][name$='[de][job_other]']");
                const addNewTask = professionDiv.querySelectorAll('.option-experience-task-add');

                let initIndex = 1;
                let initTasks = 0;

                experienceData.forEach((exp) => {
                    if (dateFromInputs[initIndex]) {
                        dateFromInputs[initIndex].value = exp.dateFrom.value || "";
                        dateFromInputs[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (dateToInputs[initIndex]) {
                        dateToInputs[initIndex].value = exp.dateTo.value || "";
                        dateToInputs[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (cityInputs[initIndex]) {
                        cityInputs[initIndex].value = exp.city.value || "";
                        cityInputs[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (companyInputs[initIndex]) {
                        companyInputs[initIndex].value = exp.company.value || "";
                        companyInputs[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (countryFields[initIndex]) {
                        const countryOption = Array.from(countryFields[initIndex].options).find(
                            (option) => option.text.trim() === exp.country.value
                        );
                        if (countryOption) {
                            countryFields[initIndex].value = countryOption.value;
                            const event = new Event("chosen:updated", {bubbles: true});
                            countryFields[initIndex].dispatchEvent(event);
                        }
                    }

                    if (professionFields[initIndex]) {
                        const professionOption = Array.from(professionFields[initIndex].options).find(
                            (option) => option.text.trim() === exp.profession.value
                        );

                        if (professionOption) {
                            professionFields[initIndex].value = professionOption.value;
                            const event = new Event("chosen:updated", {bubbles: true});
                            professionFields[initIndex].dispatchEvent(event);
                        } else {
                            professionsDe[initIndex].value = exp.professionDe.value;
                            professionsEng[initIndex].value = exp.professionEng.value;

                            professionsDe[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                            professionsEng[initIndex].dispatchEvent(new Event("input", {bubbles: true}));
                        }
                    }

                    const experienceTasks = exp.experienceTaskDe.value.length

                    for (let i = 0; i < experienceTasks; i++) {
                        addNewTask[initIndex].click();
                    }

                    const taskEng = professionDiv.querySelectorAll("input[name^='experience'][name*='[task_other][new][en]']");
                    const taskDe = professionDiv.querySelectorAll("input[name^='experience'][name*='[task_other][new][de]']");

                    for (let i = 0; i < experienceTasks; i++) {
                        taskEng[initTasks].value = exp.experienceTaskEng.value[i].name;
                        taskDe[initTasks].value = exp.experienceTaskDe.value[i].name;
                        initTasks += 1;
                    }

                    initIndex += 1;
                });

                currentTab.querySelectorAll(".chosen-select").forEach((element) => {
                    const event = new Event("chosen:updated", {bubbles: true});
                    element.dispatchEvent(event);
                });

                const loadTasksButton = currentTab.querySelector(".option-load-tasks");
                if (loadTasksButton) {
                    loadTasksButton.click();
                }

                const educationData = formData.education.value || [];

                const educationHeader = Array.from(currentTab.querySelectorAll("h1"))
                    .find((header) => header.textContent.trim() === "EDUCATION");
                const educationSection = educationHeader.parentElement;

                for (let i = 0; i < educationData.length; i++) {
                    educationSection.querySelector(".btn.btn-outline-primary.btn-block.option-add-tpl").click();
                }

                const yearInputs = educationSection.querySelectorAll("input[name^='education'][name$='[year]']");
                const schoolFields = educationSection.querySelectorAll("select[name^='education'][name$='[school]']");
                const professionInputs = educationSection.querySelectorAll("input[name^='education'][name$='[profession]']");
                const schoolTypeEnInputs = educationSection.querySelectorAll("input[name^='education'][name$='[school_type][en]']");
                const schoolTypeDeInputs = educationSection.querySelectorAll("input[name^='education'][name$='[school_type][de]']");

                educationData.forEach((edu, index) => {
                    if (yearInputs[index + 1]) {
                        yearInputs[index + 1].value = edu.fromTo || "";
                        yearInputs[index + 1].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (schoolFields[index + 1]) {
                        const schoolOption = Array.from(schoolFields[index + 1].options).find(
                            (option) => option.text.trim() === edu.school
                        );
                        if (schoolOption) {
                            schoolFields[index + 1].value = schoolOption.value;
                        }
                        const event = new Event("chosen:updated", {bubbles: true});
                        schoolFields[index + 1].dispatchEvent(event);
                    }

                    if (professionInputs[index + 1]) {
                        professionInputs[index + 1].value = edu.profession || "";
                        professionInputs[index + 1].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (schoolTypeEnInputs[index + 1]) {
                        schoolTypeEnInputs[index + 1].value = edu.typeOfSchoolEng || "";
                        schoolTypeEnInputs[index + 1].dispatchEvent(new Event("input", {bubbles: true}));
                    }

                    if (schoolTypeDeInputs[index + 1]) {
                        schoolTypeDeInputs[index + 1].value = edu.typeOfSchoolDe || "";
                        schoolTypeDeInputs[index + 1].dispatchEvent(new Event("input", {bubbles: true}));
                    }
                });
            },
            args: [data]
        });
    });
}

async function updateStats(token) {
    try {
        const response = await fetch("http://localhost:20266/api/form-filler/stats", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            handleHttpError(response.status);
        }
    } catch (error) {
        console.error("❌ Błąd pobierania statystyk:", error);
    }
}

function handleHttpError(status) {
    if (status === 401) {
        document.getElementById("login-container").style.display = "flex";
        document.getElementById("main-container").style.display = "none";
        throw new Error("Twoja sesja wygasła - spróbuj zalogować się ponownie.");
    }
    throw new Error(`Błąd serwera: ${status}`);
}

function handleError(error) {
    console.error("Błąd podczas ładowania danych kandydata:", error);
    UI.updateContent(`
        <div class="section-header">
            <div class="section-icon">
                <i class="fa-solid fa-exclamation-triangle"></i>
            </div>
            <div>
                <h3 class="section-title">Wystąpił błąd</h3>
                <p class="section-subtitle">Nie udało się przetworzyć pliku</p>
            </div>
        </div>
        
        <div class="alert alert-danger">
            <i class="fa-solid fa-times-circle"></i>
            <div>Wystąpił błąd podczas ładowania danych: ${error.message}</div>
        </div>
        
        <button id="try-again-btn" class="btn-primary">
            <i class="fa-solid fa-redo"></i> Spróbuj ponownie
        </button>
    `);

    document.getElementById("try-again-btn").addEventListener("click", () => {
        candidateProfileButton.click();
    });
}

export default handleError;