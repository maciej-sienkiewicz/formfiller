const Auth = {
    checkLogin: function (shouldChangePassword) {
        chrome.storage.local.get("jwtToken", (data) => {
            if (data.jwtToken) {
                document.getElementById("login-container").style.display = "none";
                if(shouldChangePassword === "true") {
                    document.getElementById("password-change").style.display = "flex";
                } else {
                    document.getElementById("main-container").style.display = "flex";
                }
            } else {
                document.getElementById("login-container").style.display = "flex";
                document.getElementById("main-container").style.display = "none";
            }
        });
    },

    login: async function (username, password) {
        try {
            const response = await fetch("https://potentai.pl/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || "Błąd logowania");
            }


            const token = data.token;
            const shouldChangePassword = data.shouldChangePassword

            chrome.storage.local.set({ jwtToken: token }, () => {
                Auth.checkLogin(shouldChangePassword); // 🔥 Przełącz na główny widok
            });
        } catch (error) {
            console.error("❌ Błąd logowania:", error);
            document.getElementById("login-error").style.display = "block";
            document.getElementById("login-error").textContent = "Nieprawidłowe dane logowania!";
        }
    },

    logout: function () {
        chrome.storage.local.remove("jwtToken", () => {
            Auth.checkLogin();
        });
    }
};

// 🔥 Sprawdzenie logowania na starcie
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("password-change").style.display = "none";

    Auth.checkLogin();

    document.getElementById("login-button").addEventListener("click", function () {
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        Auth.login(username, password);
    });

    document.getElementById("logout-button").addEventListener("click", function () {
        Auth.logout();
    });

    document.getElementById("password-change-button").addEventListener("click", async function () {
        const password = document.getElementById("new-password").value;
        const repeatPassword = document.getElementById("repeat-password").value;

        if(password !== repeatPassword) {
            throw new Error("Wprowadzone hasła różnia się od siebie");
        }

        const response = await fetch("https://potentai.pl/api/change", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ password }),
        });

        if (!response.ok) {
            if (response.status === 401) {
                console.warn("⛔ Nieprawidłowe dane logowania (401)");
                document.getElementById("login-container").style.display = "flex";
                document.getElementById("main-container").style.display = "none";
                alert(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
                throw new Error(`Twoja sesja wygasła - spróbuj zalogować się ponownie.`);
            }
            throw new Error(`Błąd serwera: ${response.status}`);
        }

        document.getElementById("password-change").style.display = "none";
        document.getElementById("main-container").style.display = "flex";
    })
});
