package com.kmaebashi.sporder.service;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.controller.data.ApiResult;
import com.kmaebashi.sporder.controller.data.GuestCountInfo;
import com.kmaebashi.sporder.dbaccess.GuestCountDbAccess;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dto.DeviceSessionDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class GuestCountService {
    private GuestCountService() {}

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

            Path htmlPath = context.getHtmlTemplateDirectory().resolve("guestcount.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            doc.getElementsByClass("guest-count-page").first()
                    .attr("data-order-group-id", sessionDto.orderGroupId);

            String restaurantName = GuestCountDbAccess.getRestaurantName(
                    context.getDbAccessInvoker(), rtId, locale.getCode());
            if (restaurantName != null) {
                doc.getElementsByClass("restaurant-name").first().text(restaurantName);
            }

            return ret;
        });
    }

    public static JsonResult setGuestCount(ServiceInvoker invoker, GuestCountInfo info) {
        return invoker.invoke((context) -> {
            GuestCountDbAccess.updateGuestCount(context.getDbAccessInvoker(), info.orderGroupId, info.guestCount);

            ApiResult result = new ApiResult("成功しました。");
            String retJson = ClassMapper.toJson(result);
            return new JsonResult(retJson);
        }, InvokerOption.TRANSACTIONAL);
    }
}
