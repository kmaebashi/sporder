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
    setCheckoutButtonHandler();
};
function setCheckoutButtonHandler() {
    const button = document.querySelector(".checkout-button");
    button === null || button === void 0 ? void 0 : button.addEventListener("click", onCheckoutButtonClick);
}
function onCheckoutButtonClick(e) {
    return __awaiter(this, void 0, void 0, function* () {
        const button = e.currentTarget instanceof Element ? e.currentTarget : null;
        if (button instanceof HTMLButtonElement) {
            button.disabled = true;
        }
        try {
            const info = createCloseTableInfo();
            if (info == null) {
                alert("テーブル情報が不正です。");
                return;
            }
            yield postCloseTable(info);
            alert("お会計を受け付けました。");
        }
        catch (error) {
            alert("お会計の受付に失敗しました。");
        }
        finally {
            if (button instanceof HTMLButtonElement) {
                button.disabled = false;
            }
        }
    });
}
function createCloseTableInfo() {
    const tableCode = document.body.dataset.tableCode;
    if (tableCode == null || tableCode == "") {
        return null;
    }
    return {
        tableCode: tableCode
    };
}
function postCloseTable(info) {
    return __awaiter(this, void 0, void 0, function* () {
        const response = yield fetch("closetable", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(info)
        });
        if (!response.ok) {
            throw new Error("お会計の受付に失敗しました。");
        }
    });
}
