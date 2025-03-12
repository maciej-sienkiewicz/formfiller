import {UI} from "../ui.js";

const reportIssueButton = document.getElementById('report-issue-button');
reportIssueButton.addEventListener('click', () => {
    if (reportIssueButton.classList.contains('active')) {
        reportIssueButton.classList.remove('active');
        UI.resetContent();
    } else {
        UI.deactivateAllButtons();
        reportIssueButton.classList.add('active');
        UI.updateContent(`
            <p>Przykro nam, że coś się przydarzyło! Też tego nie lubimy.</p>
            <p>Jeżeli to dla Ciebie pilne to możesz do nas zadzwonić: <strong>+48 888 915 358</strong>.</p>
            <p>Jeżeli wolisz napisać maila - śmiało! <a href="mailto:awaria@formfiller.com">awaria@formfiller.com</a></p>
        `);
    }
});