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
    setMenuItemClickHandlers();
    setOrderCountClickHandlers();
};
function setMenuItemClickHandlers() {
    const menuItems = document.querySelectorAll(".menu-item");
    menuItems.forEach((menuItem) => {
        menuItem.addEventListener("click", onMenuItemClick);
    });
}
function setOrderCountClickHandlers() {
    const decreaseButton = document.querySelector(".order-count-button.decrease");
    const increaseButton = document.querySelector(".order-count-button.increase");
    decreaseButton === null || decreaseButton === void 0 ? void 0 : decreaseButton.addEventListener("click", onDecreaseOrderCountClick);
    increaseButton === null || increaseButton === void 0 ? void 0 : increaseButton.addEventListener("click", onIncreaseOrderCountClick);
}
function onDecreaseOrderCountClick(e) {
    changeOrderCount(-1);
}
function onIncreaseOrderCountClick(e) {
    changeOrderCount(1);
}
function changeOrderCount(delta) {
    const countInput = document.querySelector(".order-count");
    if (!(countInput instanceof HTMLInputElement)) {
        return;
    }
    const min = Number(countInput.min || "1");
    const currentValue = Number(countInput.value || "1");
    const nextValue = Math.max(min, currentValue + delta);
    countInput.value = String(nextValue);
}
function onMenuItemClick(e) {
    return __awaiter(this, void 0, void 0, function* () {
        const menuItem = getMenuItemElement(e);
        if (menuItem == null || menuItem.dataset.menuItemId == null) {
            return;
        }
        const rtId = getRtId();
        if (rtId == null) {
            return;
        }
        const menuItemId = menuItem.dataset.menuItemId;
        const info = yield fetchMenuItemInfo(rtId, menuItemId);
        updateOrderDialog(info, rtId);
        showOrderDialog();
    });
}
function getMenuItemElement(e) {
    if (!(e.currentTarget instanceof HTMLElement)) {
        return null;
    }
    return e.currentTarget;
}
function getRtId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("rt_id");
}
function fetchMenuItemInfo(rtId, menuItemId) {
    return __awaiter(this, void 0, void 0, function* () {
        const params = new URLSearchParams();
        params.set("rt_id", rtId);
        params.set("menu_item_id", menuItemId);
        const response = yield fetch("getmenuiteminfo?" + params.toString());
        if (!response.ok) {
            throw new Error("メニューアイテム情報の取得に失敗しました。");
        }
        return yield response.json();
    });
}
function updateOrderDialog(info, rtId) {
    setText(".order-dialog-name", info.name);
    setText(".order-dialog-price", "¥" + info.price);
    setFullsizeImage(info, rtId);
    resetOrderCount();
    updateOptionField(info);
}
function setText(selector, text) {
    const element = document.querySelector(selector);
    if (element != null) {
        element.textContent = text;
    }
}
function setFullsizeImage(info, rtId) {
    const image = document.querySelector(".order-dialog-image");
    if (!(image instanceof HTMLImageElement)) {
        return;
    }
    const params = new URLSearchParams();
    params.set("rt_id", rtId);
    params.set("menu_item_id", String(info.menuItemId));
    image.src = "menuimagel?" + params.toString();
    image.alt = info.name;
}
function resetOrderCount() {
    const count = document.querySelector(".order-count");
    if (count instanceof HTMLInputElement) {
        count.value = "1";
    }
}
function updateOptionField(info) {
    var _a, _b;
    const optionField = document.querySelector(".order-option-field");
    const optionLabel = (_a = optionField === null || optionField === void 0 ? void 0 : optionField.querySelector(".order-field-label")) !== null && _a !== void 0 ? _a : null;
    const optionSelect = (_b = optionField === null || optionField === void 0 ? void 0 : optionField.querySelector(".order-option")) !== null && _b !== void 0 ? _b : null;
    if (!(optionField instanceof HTMLElement) || !(optionSelect instanceof HTMLSelectElement)) {
        return;
    }
    optionSelect.replaceChildren();
    if (info.optionName == null || info.optionName == "" || info.optionList == null || info.optionList.length == 0) {
        optionField.hidden = true;
        optionField.style.display = "none";
        return;
    }
    optionField.hidden = false;
    optionField.style.display = "";
    if (optionLabel != null) {
        optionLabel.textContent = info.optionName;
    }
    info.optionList.forEach((optionInfo) => {
        const option = document.createElement("option");
        option.value = String(optionInfo.id);
        option.textContent = optionInfo.name;
        optionSelect.appendChild(option);
    });
}
function showOrderDialog() {
    const dialog = document.getElementById("order-dialog");
    if (!(dialog instanceof HTMLDialogElement)) {
        return;
    }
    if (!dialog.open) {
        dialog.showModal();
    }
}
