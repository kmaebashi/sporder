window.onload = function(e: Event): void {
    setGuestCountChangeHandlers();
    setGuestCountSubmitHandler();
}

interface GuestCountInfo {
    orderGroupId: string;
    guestCount: number;
}

function setGuestCountChangeHandlers(): void {
    const radioButtons: NodeListOf<HTMLInputElement>
            = document.querySelectorAll("input[name='guest_count']");
    radioButtons.forEach((radioButton: HTMLInputElement): void => {
        radioButton.addEventListener("change", onGuestCountChange);
    });
}

function setGuestCountSubmitHandler(): void {
    const form: Element | null = document.querySelector(".guest-count-form");
    form?.addEventListener("submit", onGuestCountSubmit);
}

function onGuestCountChange(e: Event): void {
    updateSelectedGuestCount();
}

async function onGuestCountSubmit(e: Event): Promise<void> {
    e.preventDefault();

    const info: GuestCountInfo | null = createGuestCountInfo();
    if (info == null) {
        return;
    }

    const response: Response = await fetch("setguestcount", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(info)
    });
    if (!response.ok) {
        throw new Error("人数の送信に失敗しました。");
    }
    redirectToMenu();
}

function createGuestCountInfo(): GuestCountInfo | null {
    const page: Element | null = document.querySelector(".guest-count-page");
    const guestCountRadio: Element | null = document.querySelector("input[name='guest_count']:checked");
    if (!(page instanceof HTMLElement)
            || !(guestCountRadio instanceof HTMLInputElement)
            || page.dataset.orderGroupId == null
            || page.dataset.orderGroupId == "") {
        return null;
    }

    return {
        orderGroupId: page.dataset.orderGroupId,
        guestCount: Number(guestCountRadio.value)
    };
}

function redirectToMenu(): void {
    const rtId: string | null = getGuestCountRtId();
    if (rtId == null) {
        return;
    }

    const params: URLSearchParams = new URLSearchParams();
    params.set("rt_id", rtId);
    params.set("category_id", "1");
    window.location.href = "menu?" + params.toString();
}

function getGuestCountRtId(): string | null {
    const params: URLSearchParams = new URLSearchParams(window.location.search);
    return params.get("rt_id");
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
