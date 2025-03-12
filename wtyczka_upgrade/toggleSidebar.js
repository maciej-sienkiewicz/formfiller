if (!document.getElementById('my-extension-sidebar')) {
    const iframe = document.createElement('iframe');
    iframe.src = chrome.runtime.getURL('sidebar.html');
    iframe.id = 'my-extension-sidebar';
    iframe.style.position = 'fixed';
    iframe.style.top = '0';
    iframe.style.right = '0';
    iframe.style.width = '300px';
    iframe.style.height = '100%';
    iframe.style.border = 'none';
    iframe.style.zIndex = '10000';
    iframe.style.backgroundColor = 'white';
    document.body.appendChild(iframe);

    // Zmniejsz szerokość strony, aby nie zasłaniać treści
    document.body.style.width = "calc(100% - 300px)";
} else {
    const sidebar = document.getElementById('my-extension-sidebar');
    sidebar.remove();
    document.body.style.width = "";
}
