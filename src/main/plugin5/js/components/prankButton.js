import {UI} from "../ui.js";
import {Prank} from "../prank.js";

export const prankButton = document.getElementById('do-a-prank-button');

prankButton.addEventListener('click', () => {
    UI.deactivateAllButtons();
    prankButton.classList.add('active');

    Prank.doPrank()
});