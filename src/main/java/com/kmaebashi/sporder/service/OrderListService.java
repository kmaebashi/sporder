package com.kmaebashi.sporder.service;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.controller.data.ApiResult;
import com.kmaebashi.sporder.controller.data.PlaceOrderInfo;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dbaccess.OrderDbAccess;
import com.kmaebashi.sporder.dto.DeviceSessionDto;
import com.kmaebashi.sporder.dto.OrderListItemDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class OrderListService {
    private OrderListService() {}

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

            Path htmlPath = context.getHtmlTemplateDirectory().resolve("orderlist.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            doc.body().attr("data-order-group-id", sessionDto.orderGroupId);
            renderOrderList(doc, OrderDbAccess.getUnplacedOrderList(context.getDbAccessInvoker(), rtId,
                    sessionDto.orderGroupId, locale.getCode()));
            doc.getElementsByClass("bottom-nav").first()
                    .getElementsByClass("nav-item").get(1)
                    .attr("href", createMenuUrl(rtId));
            doc.getElementsByClass("bottom-nav").first()
                    .getElementsByClass("nav-item").get(2)
                    .attr("href", createOrderHistoryUrl(rtId));

            return ret;
        });
    }

    public static JsonResult placeOrder(ServiceInvoker invoker, String sessionToken, PlaceOrderInfo info) {
        return invoker.invoke((context) -> {
            if (info == null || info.orderItems == null) {
                throw new BadRequestException("注文内容が不正です。");
            }

            DeviceSessionDto sessionDto = LoginDbAccess.getDeviceSessionBySessionTokenForUpdate(
                    context.getDbAccessInvoker(), sessionToken);
            if (sessionDto == null || sessionDto.expiresAt.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("セッションが無効です。");
            }
            if (!sessionDto.rtId.equals(info.rtId) || !sessionDto.orderGroupId.equals(info.orderGroupId)) {
                throw new BadRequestException("リクエストが不正です。");
            }
            ensureOrderGroupOpen(info.rtId, info.orderGroupId, context.getDbAccessInvoker());

            LocalDateTime now = LocalDateTime.now();
            for (PlaceOrderInfo.OrderItem item : info.orderItems) {
                if (item.deleted) {
                    OrderDbAccess.updateOrderDeletedAt(context.getDbAccessInvoker(), info.rtId, info.orderGroupId,
                            item.orderId, now);
                } else {
                    OrderDbAccess.updateOrderOrderedAt(context.getDbAccessInvoker(), info.rtId, info.orderGroupId,
                            item.orderId, now);
                }
            }
            LoginDbAccess.updateDeviceSessionExpiresAt(context.getDbAccessInvoker(), sessionDto.rtId,
                    sessionDto.deviceSessionId, now.plusHours(6));

            ApiResult result = new ApiResult("成功しました。");
            return new JsonResult(ClassMapper.toJson(result));
        }, InvokerOption.TRANSACTIONAL);
    }

    private static void renderOrderList(Document doc, List<OrderListItemDto> orderList) {
        Element orderListElem = doc.getElementById("order-list");
        Element orderItemElem = orderListElem.getElementsByClass("order-list-item").first();
        orderListElem.empty();

        for (OrderListItemDto dto : orderList) {
            Element cloneElem = orderItemElem.clone();
            cloneElem.attr("data-order-id", String.valueOf(dto.orderId));
            cloneElem.getElementsByClass("order-list-name").first().text(dto.name);
            cloneElem.getElementsByClass("order-list-count").first().text(dto.count + "点");
            cloneElem.getElementsByClass("order-list-price").first().text("¥" + String.format("%,d",
                    dto.price * dto.count));
            cloneElem.getElementsByClass("order-delete-button").first()
                    .attr("data-order-id", String.valueOf(dto.orderId));
            orderListElem.appendChild(cloneElem);
        }
    }

    private static void ensureOrderGroupOpen(String rtId, String orderGroupId,
                                             com.kmaebashi.nctfw.DbAccessInvoker invoker) {
        LocalDateTime closedAt = LoginDbAccess.getOrderGroupClosedAt(invoker, rtId, orderGroupId);
        if (closedAt != null) {
            throw new BadRequestException("オーダーグループは終了しています。");
        }
    }

    private static String createMenuUrl(String rtId) {
        String encodedRtId = URLEncoder.encode(rtId, StandardCharsets.UTF_8);
        return "menu?rt_id=" + encodedRtId;
    }

    private static String createOrderHistoryUrl(String rtId) {
        String encodedRtId = URLEncoder.encode(rtId, StandardCharsets.UTF_8);
        return "orderhistory?rt_id=" + encodedRtId;
    }
}
