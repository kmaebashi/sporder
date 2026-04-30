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
    setOrderListDeleteButtonHandlers();
    setPlaceOrderButtonHandler();
};
function setOrderListDeleteButtonHandlers() {
    const buttons = document.querySelectorAll(".order-delete-button");
    buttons.forEach((button) => {
        button.addEventListener("click", onOrderListDeleteButtonClick);
    });
}
function setPlaceOrderButtonHandler() {
    const button = document.querySelector(".order-submit-button");
    button === null || button === void 0 ? void 0 : button.addEventListener("click", onPlaceOrderButtonClick);
}
function onPlaceOrderButtonClick(e) {
    return __awaiter(this, void 0, void 0, function* () {
        const button = e.currentTarget instanceof Element ? e.currentTarget : null;
        if (button instanceof HTMLButtonElement) {
            button.disabled = true;
        }
        try {
            const info = createPlaceOrderInfo();
            if (info == null) {
                alert("注文内容が不正です。");
                return;
            }
            yield postPlaceOrder(info);
            redirectOrderListToMenu();
        }
        catch (error) {
            alert("注文の確定に失敗しました。");
        }
        finally {
            if (button instanceof HTMLButtonElement) {
                button.disabled = false;
            }
        }
    });
}
function onOrderListDeleteButtonClick(e) {
    if (!(e.currentTarget instanceof HTMLButtonElement)) {
        return;
    }
    const button = e.currentTarget;
    const item = button.closest(".order-list-item");
    if (!(item instanceof HTMLElement)) {
        return;
    }
    const deleted = !item.classList.contains("deleted");
    setOrderListItemDeleted(item, button, deleted);
}
function setOrderListItemDeleted(item, button, deleted) {
    item.classList.toggle("deleted", deleted);
    item.dataset.deleted = deleted ? "true" : "false";
    button.textContent = deleted ? "復活" : "削除";
}
function createPlaceOrderInfo() {
    const rtId = getOrderListRtId();
    const orderGroupId = getOrderListOrderGroupId();
    if (rtId == null || orderGroupId == null) {
        return null;
    }
    const orderItems = [];
    const items = document.querySelectorAll(".order-list-item");
    items.forEach((item) => {
        const orderId = Number(item.dataset.orderId);
        if (!Number.isNaN(orderId)) {
            orderItems.push({
                orderId: orderId,
                deleted: item.dataset.deleted == "true"
            });
        }
    });
    return {
        rtId: rtId,
        orderGroupId: orderGroupId,
        orderItems: orderItems
    };
}
function getOrderListRtId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("rt_id");
}
function getOrderListOrderGroupId() {
    const orderGroupId = document.body.dataset.orderGroupId;
    return orderGroupId == null || orderGroupId == "" ? null : orderGroupId;
}
function postPlaceOrder(info) {
    return __awaiter(this, void 0, void 0, function* () {
        const response = yield fetch("placeorder", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(info)
        });
        if (!response.ok) {
            throw new Error("注文の確定に失敗しました。");
        }
    });
}
function redirectOrderListToMenu() {
    const rtId = getOrderListRtId();
    if (rtId == null) {
        return;
    }
    window.location.href = "menu?rt_id=" + encodeURIComponent(rtId) + "&category_id=1";
}
