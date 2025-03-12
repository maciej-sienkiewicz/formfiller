// Sprawdzanie czy sidebar już istnieje
if (!document.getElementById('my-extension-sidebar')) {
    // Tworzymy iframe z sidebarem - bez overlay'a dla tła
    const iframe = document.createElement('iframe');
    iframe.src = chrome.runtime.getURL('sidebar.html');
    iframe.id = 'my-extension-sidebar';
    iframe.style.position = 'fixed';
    iframe.style.top = '0';
    iframe.style.right = '-320px'; // Zaczynamy poza ekranem dla animacji
    iframe.style.width = '320px';
    iframe.style.height = '100%';
    iframe.style.border = 'none';
    iframe.style.zIndex = '10000';
    iframe.style.boxShadow = '-2px 0 15px rgba(0, 0, 0, 0.08)';
    iframe.style.backgroundColor = 'white';
    iframe.style.transition = 'right 0.3s ease';
    document.body.appendChild(iframe);

    // Nie zmieniamy szerokości i marginesu strony - zgodnie z wymaganiem
    // Strona pod wtyczką pozostaje niezmieniona

    // Animacja wejścia
    setTimeout(() => {
        iframe.style.right = '0';
    }, 50);

    // Obsługa klawisza ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeSidebar();
        }
    });
} else {
    closeSidebar();
}

// Funkcja zamykająca sidebar
function closeSidebar() {
    const sidebar = document.getElementById('my-extension-sidebar');

    if (sidebar) {
        sidebar.style.right = '-320px';

        // Usuwamy element po zakończeniu animacji
        setTimeout(() => {
            sidebar.remove();
        }, 300);
    }
}