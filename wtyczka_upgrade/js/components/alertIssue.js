import { UI } from "../ui.js";  // Zakładamy, że UI.updateContent() jest dostępne

// Obsługa przycisku alert-issue
const alertIssueButton = document.getElementById("alert-issue");

if (alertIssueButton) {
    alertIssueButton.addEventListener("click", () => {
        // Wstawiamy do #content panel z opisem, polem tekstowym i przyciskiem "Wyślij"
        UI.updateContent(`
      <div class="alert-issue-container">
        <p class="alert-issue-intro">
          Jeżeli napotkałeś się na jakiś problem to daj nam znać!<br/>
          Naprawimy go jak najszybciej to możliwe.
        </p>
        <input type="text" id="issue-text" class="alert-issue-input" placeholder="Opisz problem...">
        <button id="send-issue" class="alert-issue-button">Wyślij</button>
      </div>
    `);

        // Po wstawieniu widoku, podpinamy listener do przycisku "Wyślij"
        const sendIssueButton = document.getElementById("send-issue");
        if (sendIssueButton) {
            sendIssueButton.addEventListener("click", () => {
                UI.updateContent(`
          <div class="alert-issue-container">
            <p class="alert-issue-thanks">
              Dzięki! Zajmiemy się tym jak najszybciej!
            </p>
          </div>
        `);
            });
        }
    });
}
