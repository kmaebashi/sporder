"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
window.onload = function (e) {
    setGuestCountChangeHandlers();
    setGuestCountSubmitHandler();
};
function setGuestCountChangeHandlers() {
    const radioButtons = document.querySelectorAll("input[name='guest_count']");
    radioButtons.forEach((radioButton) => {
        radioButton.addEventListener("change", onGuestCountChange);
    });
}
function setGuestCountSubmitHandler() {
    const form = document.querySelector(".guest-count-form");
    form === null || form === void 0 ? void 0 : form.addEventListener("submit", onGuestCountSubmit);
}
function onGuestCountChange(e) {
    updateSelectedGuestCount();
}
function onGuestCountSubmit(e) {
    return __awaiter(this, void 0, void 0, function* () {
        e.preventDefault();
        const info = createGuestCountInfo();
        if (info == null) {
            return;
        }
        const response = yield fetch("setguestcount", {
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
    });
}
function createGuestCountInfo() {
    const page = document.querySelector(".guest-count-page");
    const guestCountRadio = document.querySelector("input[name='guest_count']:checked");
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
function redirectToMenu() {
    const rtId = getGuestCountRtId();
    if (rtId == null) {
        return;
    }
    const params = new URLSearchParams();
    params.set("rt_id", rtId);
    params.set("category_id", "1");
    window.location.href = "menu?" + params.toString();
}
function getGuestCountRtId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("rt_id");
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
