window.onload = function(e: Event): void {
    setCheckoutButtonHandler();
}

interface CloseTableInfo {
    tableCode: string;
}

function setCheckoutButtonHandler(): void {
    const button: Element | null = document.querySelector(".checkout-button");
    button?.addEventListener("click", onCheckoutButtonClick);
}

async function onCheckoutButtonClick(e: Event): Promise<void> {
    const button: Element | null = e.currentTarget instanceof Element ? e.currentTarget : null;
    if (button instanceof HTMLButtonElement) {
        button.disabled = true;
    }

    try {
        const info: CloseTableInfo | null = createCloseTableInfo();
        if (info == null) {
            alert("テーブル情報が不正です。");
            return;
        }

        await postCloseTable(info);
        alert("お会計を受け付けました。");
    } catch (error) {
        alert("お会計の受付に失敗しました。");
    } finally {
        if (button instanceof HTMLButtonElement) {
            button.disabled = false;
        }
    }
}

function createCloseTableInfo(): CloseTableInfo | null {
    const tableCode: string | undefined = document.body.dataset.tableCode;
    if (tableCode == null || tableCode == "") {
        return null;
    }

    return {
        tableCode: tableCode
    };
}

async function postCloseTable(info: CloseTableInfo): Promise<void> {
    const response: Response = await fetch("closetable", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(info)
    });

    if (!response.ok) {
        throw new Error("お会計の受付に失敗しました。");
    }
}
