window.onload = function(e: Event): void {
    setGuestCountChangeHandlers();
}

function setGuestCountChangeHandlers(): void {
    const radioButtons: NodeListOf<HTMLInputElement>
            = document.querySelectorAll("input[name='guest_count']");
    radioButtons.forEach((radioButton: HTMLInputElement): void => {
        radioButton.addEventListener("change", onGuestCountChange);
    });
}

function onGuestCountChange(e: Event): void {
    updateSelectedGuestCount();
}

function updateSelectedGuestCount(): void {
    const options: NodeListOf<HTMLElement> = document.querySelectorAll(".guest-count-option");
    options.forEach((option: HTMLElement): void => {
        const radioButton: HTMLInputElement | null = option.querySelector("input[name='guest_count']");
        if (radioButton == null) {
            return;
        }
        if (radioButton.checked) {
            option.classList.add("selected");
        } else {
            option.classList.remove("selected");
        }
    });
}
