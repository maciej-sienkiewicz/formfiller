import { contentContainer } from "./domManager.js"

export const UI = {
    updateContent: function (contentHtml) {
        contentContainer.innerHTML = contentHtml;
    },

    resetContent: function () {
        contentContainer.innerHTML = '';
    },

    deactivateAllButtons: function () {
        const buttons = document.querySelectorAll('.menu-item');
        buttons.forEach(button => button.classList.remove('active'));
    }
};
