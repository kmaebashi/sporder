package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.RedirectResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.file.Path;

public class LoginService {
    private LoginService() {}

    public static DocumentResult start(ServiceInvoker invoker, String rtId, String tableCode) {
        return invoker.invoke((context) -> {
            Path htmlPath = context.getHtmlTemplateDirectory().resolve("login.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            // ここを埋めて

            return ret;
        }, InvokerOption.TRANSACTIONAL);
    }

    public static RedirectResult join(ServiceInvoker invoker, String rtId, String tableCode, String joinToken,
                                      boolean isFirst, String[] outSessionToken) {
        return invoker.invoke((context) -> {
            // ここを埋めて

            String url;
            if (isFirst) {
                url = "guestcount";
            } else {
                url = "menu?category_id=1";
            }
            return new RedirectResult(url);
        }, InvokerOption.TRANSACTIONAL);
    }
}
