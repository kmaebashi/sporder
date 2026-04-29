"use strict";
window.onload = function (e) {
    setGuestCountChangeHandlers();
};
function setGuestCountChangeHandlers() {
    const radioButtons = document.querySelectorAll("input[name='guest_count']");
    radioButtons.forEach((radioButton) => {
        radioButton.addEventListener("change", onGuestCountChange);
    });
}
function onGuestCountChange(e) {
    updateSelectedGuestCount();
}
function updateSelectedGuestCount() {
    const options = document.querySelectorAll(".guest-count-option");
    options.forEach((option) => {
        const radioButton = option.querySelector("input[name='guest_count']");
        if (radioButton == null) {
            return;
        }
        if (radioButton.checked) {
            option.classList.add("selected");
        }
        else {
            option.classList.remove("selected");
        }
    });
}
