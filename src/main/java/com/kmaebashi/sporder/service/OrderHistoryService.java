package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.dbaccess.CloseTableDbAccess;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dbaccess.OrderDbAccess;
import com.kmaebashi.sporder.dto.CloseTableDto;
import com.kmaebashi.sporder.dto.DeviceSessionDto;
import com.kmaebashi.sporder.dto.OrderHistoryItemDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryService {
    private OrderHistoryService() {}

    public static DocumentResult showPage(ServiceInvoker invoker, String rtId, String sessionToken, Locale locale) {
        return invoker.invoke((context) -> {
            DeviceSessionDto sessionDto = LoginDbAccess.getDeviceSessionBySessionTokenForUpdate(
                    context.getDbAccessInvoker(), sessionToken);
            if (sessionDto == null || sessionDto.expiresAt.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("セッションが無効です。");
            }
            if (!sessionDto.rtId.equals(rtId)) {
                throw new BadRequestException("リクエストが不正です。");
            }
            ensureOrderGroupOpen(rtId, sessionDto.orderGroupId, context.getDbAccessInvoker());

            Path htmlPath = context.getHtmlTemplateDirectory().resolve("orderhistory.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            CloseTableDto tableDto = CloseTableDbAccess.getTableByOrderGroup(context.getDbAccessInvoker(), rtId,
                    sessionDto.orderGroupId);
            if (tableDto == null) {
                throw new BadRequestException("テーブルが見つかりません。");
            }
            doc.body().attr("data-table-code", tableDto.tableCode);
            renderOrderHistory(doc, OrderDbAccess.getOrderHistoryList(context.getDbAccessInvoker(), rtId,
                    sessionDto.orderGroupId, locale.getCode()));
            renderFooter(doc, rtId);

            return new DocumentResult(doc);
        });
    }

    private static void renderOrderHistory(Document doc, List<OrderHistoryItemDto> orderHistoryList) {
        Element orderHistoryListElem = doc.getElementById("order-history-list");
        Element orderItemElem = orderHistoryListElem.getElementsByClass("order-list-item").first();
        orderHistoryListElem.empty();

        int totalPrice = 0;
        for (OrderHistoryItemDto dto : orderHistoryList) {
            Element cloneElem = orderItemElem.clone();
            cloneElem.getElementsByClass("order-list-name").first().text(dto.name);
            cloneElem.getElementsByClass("order-list-count").first().text(dto.count + "点");
            cloneElem.getElementsByClass("order-list-price").first().text("¥" + String.format("%,d",
                    dto.totalPrice));
            orderHistoryListElem.appendChild(cloneElem);
            totalPrice += dto.totalPrice;
        }

        doc.getElementsByClass("order-history-total-price").first()
                .text("¥" + String.format("%,d", totalPrice));
    }

    private static void ensureOrderGroupOpen(String rtId, String orderGroupId,
                                             com.kmaebashi.nctfw.DbAccessInvoker invoker) {
        LocalDateTime closedAt = LoginDbAccess.getOrderGroupClosedAt(invoker, rtId, orderGroupId);
        if (closedAt != null) {
            throw new BadRequestException("オーダーグループは終了しています。");
        }
    }

    private static void renderFooter(Document doc, String rtId) {
        String encodedRtId = URLEncoder.encode(rtId, StandardCharsets.UTF_8);
        doc.getElementsByClass("bottom-nav").first()
                .getElementsByClass("nav-item").get(1)
                .attr("href", "menu?rt_id=" + encodedRtId);
        doc.getElementsByClass("bottom-nav").first()
                .getElementsByClass("nav-item").get(2)
                .attr("href", "orderlist?rt_id=" + encodedRtId);
    }
}
