window.onload = function(e: Event): void {
    setOrderListDeleteButtonHandlers();
    setPlaceOrderButtonHandler();
}

interface PlaceOrderItem {
    orderId: number;
    deleted: boolean;
}

interface PlaceOrderInfo {
    rtId: string;
    orderGroupId: string;
    orderItems: PlaceOrderItem[];
}

function setOrderListDeleteButtonHandlers(): void {
    const buttons: NodeListOf<HTMLButtonElement> = document.querySelectorAll(".order-delete-button");
    buttons.forEach((button: HTMLButtonElement): void => {
        button.addEventListener("click", onOrderListDeleteButtonClick);
    });
}

function setPlaceOrderButtonHandler(): void {
    const button: Element | null = document.querySelector(".order-submit-button");
    button?.addEventListener("click", onPlaceOrderButtonClick);
}

async function onPlaceOrderButtonClick(e: Event): Promise<void> {
    const button: Element | null = e.currentTarget instanceof Element ? e.currentTarget : null;
    if (button instanceof HTMLButtonElement) {
        button.disabled = true;
    }

    try {
        const info: PlaceOrderInfo | null = createPlaceOrderInfo();
        if (info == null) {
            alert("注文内容が不正です。");
            return;
        }

        await postPlaceOrder(info);
        redirectOrderListToMenu();
    } catch (error) {
        alert("注文の確定に失敗しました。");
    } finally {
        if (button instanceof HTMLButtonElement) {
            button.disabled = false;
        }
    }
}

function onOrderListDeleteButtonClick(e: Event): void {
    if (!(e.currentTarget instanceof HTMLButtonElement)) {
        return;
    }

    const button: HTMLButtonElement = e.currentTarget;
    const item: HTMLElement | null = button.closest(".order-list-item");
    if (!(item instanceof HTMLElement)) {
        return;
    }

    const deleted: boolean = !item.classList.contains("deleted");
    setOrderListItemDeleted(item, button, deleted);
}

function setOrderListItemDeleted(item: HTMLElement, button: HTMLButtonElement, deleted: boolean): void {
    item.classList.toggle("deleted", deleted);
    item.dataset.deleted = deleted ? "true" : "false";
    button.textContent = deleted ? "復活" : "削除";
}

function createPlaceOrderInfo(): PlaceOrderInfo | null {
    const rtId: string | null = getOrderListRtId();
    const orderGroupId: string | null = getOrderListOrderGroupId();
    if (rtId == null || orderGroupId == null) {
        return null;
    }

    const orderItems: PlaceOrderItem[] = [];
    const items: NodeListOf<HTMLElement> = document.querySelectorAll(".order-list-item");
    items.forEach((item: HTMLElement): void => {
        const orderId: number = Number(item.dataset.orderId);
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

function getOrderListRtId(): string | null {
    const params: URLSearchParams = new URLSearchParams(window.location.search);
    return params.get("rt_id");
}

function getOrderListOrderGroupId(): string | null {
    const orderGroupId: string | undefined = document.body.dataset.orderGroupId;
    return orderGroupId == null || orderGroupId == "" ? null : orderGroupId;
}

async function postPlaceOrder(info: PlaceOrderInfo): Promise<void> {
    const response: Response = await fetch("placeorder", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(info)
    });

    if (!response.ok) {
        throw new Error("注文の確定に失敗しました。");
    }
}

function redirectOrderListToMenu(): void {
    const rtId: string | null = getOrderListRtId();
    if (rtId == null) {
        return;
    }
    window.location.href = "menu?rt_id=" + encodeURIComponent(rtId) + "&category_id=1";
}
