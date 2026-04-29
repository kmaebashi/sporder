package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RedirectResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dto.JoinTokenDto;
import com.kmaebashi.sporder.dto.TableDto;
import com.kmaebashi.sporder.util.RandomIdGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class LoginService {
    private LoginService() {}

    public static DocumentResult start(ServiceInvoker invoker, String rtId, String tableCode) {
        return invoker.invoke((context) -> {
            Path htmlPath = context.getHtmlTemplateDirectory().resolve("login.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            TableDto tableDto = LoginDbAccess.getTableForUpdate(context.getDbAccessInvoker(), rtId, tableCode);
            if (tableDto == null) {
                throw new NotFoundException("テーブルが見つかりません。");
            }

            LocalDateTime now = LocalDateTime.now();
            boolean isFirst = tableDto.currentOrderGroup == null;
            String orderGroupId;
            if (isFirst) {
                orderGroupId = RandomIdGenerator.getRandomId();
                LoginDbAccess.insertOrderGroup(context.getDbAccessInvoker(), rtId, orderGroupId, tableDto.tableId,
                        now);
                LoginDbAccess.updateTableCurrentOrderGroup(context.getDbAccessInvoker(), rtId, tableDto.tableId,
                        orderGroupId, now);
            } else {
                orderGroupId = tableDto.currentOrderGroup;
            }

            String joinTokenId = RandomIdGenerator.getRandomId();
            LoginDbAccess.insertJoinToken(context.getDbAccessInvoker(), rtId, joinTokenId, orderGroupId, now);

            doc.getElementById("tokenForm").attr("action", "join");
            doc.selectFirst("input[name=rt_id]").attr("value", rtId);
            doc.selectFirst("input[name=order_group_id]").attr("value", orderGroupId);
            doc.selectFirst("input[name=join_token_id]").attr("value", joinTokenId);
            doc.selectFirst("input[name=is_first]").attr("value", String.valueOf(isFirst));

            return ret;
        }, InvokerOption.TRANSACTIONAL);
    }

    public static RedirectResult join(ServiceInvoker invoker, String rtId, String orderGroupId, String joinToken,
                                      boolean isFirst, String[] outSessionToken) {
        return invoker.invoke((context) -> {
            JoinTokenDto joinTokenDto = LoginDbAccess.getJoinTokenForUpdate(context.getDbAccessInvoker(), rtId,
                    orderGroupId, joinToken);
            if (joinTokenDto == null || joinTokenDto.used || isExpiredJoinToken(joinTokenDto.createdAt)) {
                throw new BadRequestException("不正なリクエストです。");
            }

            LoginDbAccess.updateJoinTokenUsed(context.getDbAccessInvoker(), rtId, joinToken);

            LocalDateTime now = LocalDateTime.now();
            String deviceSessionId = RandomIdGenerator.getRandomId();
            String sessionToken = RandomIdGenerator.getRandomId();
            LoginDbAccess.insertDeviceSession(context.getDbAccessInvoker(), rtId, deviceSessionId, orderGroupId,
                    sessionToken, now.plusHours(6), now);
            outSessionToken[0] = sessionToken;

            String url;
            if (isFirst) {
                url = "guestcount";
            } else {
                url = "menu?rt_id=" + URLEncoder.encode(rtId, StandardCharsets.UTF_8) + "&category_id=1";
            }
            return new RedirectResult(url);
        }, InvokerOption.TRANSACTIONAL);
    }

    private static boolean isExpiredJoinToken(LocalDateTime createdAt) {
        if (createdAt == null) {
            return true;
        }
        return createdAt.plusSeconds(10).isBefore(LocalDateTime.now());
    }
}
