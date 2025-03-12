import { UI } from "./ui.js";

export const Prank = {
    doPrank: function () {
        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            chrome.scripting.executeScript({
                target: { tabId: tabs[0].id },
                func: () => {
                    // Dodaj klasę 'shake' do elementu <html> i usuń po animacji
                    const htmlElement = document.documentElement;

                    // Dodaj styl CSS, jeśli jeszcze nie istnieje
                    if (!document.getElementById('shake-style')) {
                        const style = document.createElement('style');
                        style.id = 'shake-style';
                        style.textContent = `
                        @keyframes shake {
                            0% { transform: translate(1px, 1px) rotate(0deg); }
                            10% { transform: translate(-1px, -2px) rotate(-1deg); }
                            20% { transform: translate(-3px, 0px) rotate(1deg); }
                            30% { transform: translate(3px, 2px) rotate(0deg); }
                            40% { transform: translate(1px, -1px) rotate(1deg); }
                            50% { transform: translate(-1px, 2px) rotate(-1deg); }
                            60% { transform: translate(-3px, 1px) rotate(0deg); }
                            70% { transform: translate(3px, 1px) rotate(-1deg); }
                            80% { transform: translate(-1px, -1px) rotate(1deg); }
                            90% { transform: translate(1px, 2px) rotate(0deg); }
                            100% { transform: translate(1px, -2px) rotate(-1deg); }
                        }

                        html.shake {
                            animation: shake 0.5s ease-in-out;
                        }
                    `;
                        document.head.appendChild(style);
                    }

                    // Dodaj klasę 'shake' do elementu <html>
                    htmlElement.classList.add('shake');

                    // Usuń klasę po czasie trwania animacji
                    setTimeout(() => {
                        htmlElement.classList.remove('shake');
                    }, 500); // Czas trwania animacji
                },
            });
        });

        // Zaktualizuj treść w kontenerze wtyczki
        UI.updateContent(`
        <p>Ups! Ktoś Cię szturchnął!</p>
    `);
    }
}
