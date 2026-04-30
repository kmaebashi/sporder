window.onload = function(e: Event): void {
    setMenuItemClickHandlers();
    setOrderCountClickHandlers();
    setOrderDialogClickHandlers();
}

interface OptionInfo {
    id: number;
    name: string;
}

interface MenuItemInfo {
    menuItemId: number;
    name: string;
    price: number;
    optionName: string | null;
    optionList: OptionInfo[] | null;
}

interface OrderInfo {
    rtId: string;
    orderGroupId: string;
    menuItem: number;
    count: number;
}

function setMenuItemClickHandlers(): void {
    const menuItems: NodeListOf<HTMLElement> = document.querySelectorAll(".menu-item");
    menuItems.forEach((menuItem: HTMLElement): void => {
        menuItem.addEventListener("click", onMenuItemClick);
    });
}

function setOrderCountClickHandlers(): void {
    const decreaseButton: Element | null = document.querySelector(".order-count-button.decrease");
    const increaseButton: Element | null = document.querySelector(".order-count-button.increase");

    decreaseButton?.addEventListener("click", onDecreaseOrderCountClick);
    increaseButton?.addEventListener("click", onIncreaseOrderCountClick);
}

function setOrderDialogClickHandlers(): void {
    const addToOrderButton: Element | null = document.querySelector(".add-to-order-button");
    const continueSelectionButton: Element | null = document.querySelector(".continue-selection-button");
    const confirmOrderButton: Element | null = document.querySelector(".confirm-order-button");

    addToOrderButton?.addEventListener("click", onAddToOrderClick);
    continueSelectionButton?.addEventListener("click", onContinueSelectionClick);
    confirmOrderButton?.addEventListener("click", onConfirmOrderClick);
}

async function onAddToOrderClick(e: Event): Promise<void> {
    const button: Element | null = e.currentTarget instanceof Element ? e.currentTarget : null;
    if (button instanceof HTMLButtonElement) {
        button.disabled = true;
    }

    try {
        const orderInfo: OrderInfo | null = createOrderInfo();
        if (orderInfo == null) {
            alert("注文内容が不正です。");
            return;
        }

        await postOrder(orderInfo);
        closeOrderDialog();
        showOrderConfirmDialog();
    } catch (error) {
        alert("注文に失敗しました。");
    } finally {
        if (button instanceof HTMLButtonElement) {
            button.disabled = false;
        }
    }
}

function onContinueSelectionClick(e: Event): void {
    closeOrderConfirmDialog();
}

function onConfirmOrderClick(e: Event): void {
    const rtId: string | null = getRtId();
    if (rtId == null) {
        return;
    }
    window.location.href = "orderlist?rt_id=" + encodeURIComponent(rtId);
}

function onDecreaseOrderCountClick(e: Event): void {
    changeOrderCount(-1);
}

function onIncreaseOrderCountClick(e: Event): void {
    changeOrderCount(1);
}

function changeOrderCount(delta: number): void {
    const countInput: Element | null = document.querySelector(".order-count");
    if (!(countInput instanceof HTMLInputElement)) {
        return;
    }

    const min: number = Number(countInput.min || "1");
    const currentValue: number = Number(countInput.value || "1");
    const nextValue: number = Math.max(min, currentValue + delta);
    countInput.value = String(nextValue);
}

async function onMenuItemClick(e: Event): Promise<void> {
    const menuItem: HTMLElement | null = getMenuItemElement(e);
    if (menuItem == null || menuItem.dataset.menuItemId == null) {
        return;
    }

    const rtId: string | null = getRtId();
    if (rtId == null) {
        return;
    }

    const menuItemId: string = menuItem.dataset.menuItemId;
    const info: MenuItemInfo = await fetchMenuItemInfo(rtId, menuItemId);
    updateOrderDialog(info, rtId);
    showOrderDialog();
}

function getMenuItemElement(e: Event): HTMLElement | null {
    if (!(e.currentTarget instanceof HTMLElement)) {
        return null;
    }
    return e.currentTarget;
}

function getRtId(): string | null {
    const params: URLSearchParams = new URLSearchParams(window.location.search);
    return params.get("rt_id");
}

function getOrderGroupId(): string | null {
    const orderGroupId: string | undefined = document.body.dataset.orderGroupId;
    return orderGroupId == null || orderGroupId == "" ? null : orderGroupId;
}

async function fetchMenuItemInfo(rtId: string, menuItemId: string): Promise<MenuItemInfo> {
    const params: URLSearchParams = new URLSearchParams();
    params.set("rt_id", rtId);
    params.set("menu_item_id", menuItemId);

    const response: Response = await fetch("getmenuiteminfo?" + params.toString());
    if (!response.ok) {
        throw new Error("メニューアイテム情報の取得に失敗しました。");
    }
    return await response.json() as MenuItemInfo;
}

function updateOrderDialog(info: MenuItemInfo, rtId: string): void {
    setOrderDialogMenuItemId(info.menuItemId);
    setText(".order-dialog-name", info.name);
    setText(".order-dialog-price", "¥" + info.price);
    setFullsizeImage(info, rtId);
    resetOrderCount();
    updateOptionField(info);
}

function setOrderDialogMenuItemId(menuItemId: number): void {
    const dialog: HTMLElement | null = document.getElementById("order-dialog");
    if (dialog instanceof HTMLDialogElement) {
        dialog.dataset.menuItemId = String(menuItemId);
    }
}

function setText(selector: string, text: string): void {
    const element: Element | null = document.querySelector(selector);
    if (element != null) {
        element.textContent = text;
    }
}

function setFullsizeImage(info: MenuItemInfo, rtId: string): void {
    const image: Element | null = document.querySelector(".order-dialog-image");
    if (!(image instanceof HTMLImageElement)) {
        return;
    }

    const params: URLSearchParams = new URLSearchParams();
    params.set("rt_id", rtId);
    params.set("menu_item_id", String(info.menuItemId));
    image.src = "menuimagel?" + params.toString();
    image.alt = info.name;
}

function resetOrderCount(): void {
    const count: Element | null = document.querySelector(".order-count");
    if (count instanceof HTMLInputElement) {
        count.value = "1";
    }
}

function createOrderInfo(): OrderInfo | null {
    const rtId: string | null = getRtId();
    const orderGroupId: string | null = getOrderGroupId();
    const menuItemId: number | null = getOrderDialogMenuItemId();
    const count: number | null = getOrderCount();

    if (rtId == null || orderGroupId == null || menuItemId == null || count == null) {
        return null;
    }

    return {
        rtId: rtId,
        orderGroupId: orderGroupId,
        menuItem: menuItemId,
        count: count
    };
}

function getOrderDialogMenuItemId(): number | null {
    const dialog: HTMLElement | null = document.getElementById("order-dialog");
    if (!(dialog instanceof HTMLDialogElement) || dialog.dataset.menuItemId == null) {
        return null;
    }

    const menuItemId: number = Number(dialog.dataset.menuItemId);
    return Number.isNaN(menuItemId) ? null : menuItemId;
}

function getOrderCount(): number | null {
    const countInput: Element | null = document.querySelector(".order-count");
    if (!(countInput instanceof HTMLInputElement)) {
        return null;
    }

    const count: number = Number(countInput.value);
    if (Number.isNaN(count) || count <= 0) {
        return null;
    }
    return count;
}

async function postOrder(orderInfo: OrderInfo): Promise<void> {
    const response: Response = await fetch("order", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(orderInfo)
    });

    if (!response.ok) {
        throw new Error("注文に失敗しました。");
    }
}

function updateOptionField(info: MenuItemInfo): void {
    const optionField: Element | null = document.querySelector(".order-option-field");
    const optionLabel: Element | null = optionField?.querySelector(".order-field-label") ?? null;
    const optionSelect: Element | null = optionField?.querySelector(".order-option") ?? null;
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

    info.optionList.forEach((optionInfo: OptionInfo): void => {
        const option: HTMLOptionElement = document.createElement("option");
        option.value = String(optionInfo.id);
        option.textContent = optionInfo.name;
        optionSelect.appendChild(option);
    });
}

function showOrderDialog(): void {
    const dialog: HTMLElement | null = document.getElementById("order-dialog");
    if (!(dialog instanceof HTMLDialogElement)) {
        return;
    }
    if (!dialog.open) {
        dialog.showModal();
    }
}

function closeOrderDialog(): void {
    const dialog: HTMLElement | null = document.getElementById("order-dialog");
    if (dialog instanceof HTMLDialogElement && dialog.open) {
        dialog.close();
    }
}

function showOrderConfirmDialog(): void {
    const dialog: HTMLElement | null = document.getElementById("order-confirm-dialog");
    if (!(dialog instanceof HTMLDialogElement)) {
        return;
    }
    if (!dialog.open) {
        dialog.showModal();
    }
}

function closeOrderConfirmDialog(): void {
    const dialog: HTMLElement | null = document.getElementById("order-confirm-dialog");
    if (dialog instanceof HTMLDialogElement && dialog.open) {
        dialog.close();
    }
}
