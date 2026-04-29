package com.kmaebashi.sporder.router;

import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.Router;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.simplelogger.Logger;
import com.kmaebashi.sporder.controller.GuestCountController;
import com.kmaebashi.sporder.controller.ImageController;
import com.kmaebashi.sporder.util.Log;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.ResourceBundle;
import com.kmaebashi.sporder.controller.LoginController;
import com.kmaebashi.sporder.controller.MenuController;

public class SpOrderRouter extends Router {
    private ServletContext servletContext;
    private Logger logger;
    private ResourceBundle resourceBundle;
    private Path imageRoot;

    public SpOrderRouter(ServletContext servletContext, Logger logger, ResourceBundle rb) {
        this.servletContext = servletContext;
        this.logger = logger;
        this.resourceBundle = rb;
        this.imageRoot = Paths.get(rb.getString("sporder.image-directory"));
        Log.info("imageRoot src.." + rb.getString("sporder.image-directory"));
        Log.info("imageRoot.." + imageRoot);
    }
    public RoutingResult doRouting(String path, ControllerInvoker invoker, HttpServletRequest request) {
        HashMap<String, Object> params = new HashMap<>();
        Route route = SelectRoute.select(path, params);
        if (route == Route.NO_ROUTE) {
            throw new BadRequestException("URLが不正です。");
        }

        if (request.getMethod().equals("GET")) {
            this.logger.info("GET path.." + path);

            if (route == Route.QR) {
                return LoginController.start(invoker);
            } else if (route == Route.JOIN) {
                return LoginController.join(invoker);
            } else if (route == Route.GUEST_COUNT) {
                return GuestCountController.showPage(invoker);
            } else if (route == Route.MENU) {
                return MenuController.showMenu(invoker);
            } else if (route == Route.MENU_IMAGE_S) {
                return ImageController.getMenuThumbnail(invoker, imageRoot);
            } else if (route == Route.MENU_IMAGE_L) {
                return ImageController.getMenuFullsize(invoker, imageRoot);
            } else if (route == Route.GET_MENU_ITEM_INFO) {
                return MenuController.getMenuItemInfo(invoker);
            }
        } else if (request.getMethod().equals("POST")) {
            if (route == Route.JOIN) {
                return LoginController.join(invoker);
            } else if (route == Route.SET_GUEST_COUNT) {
                return GuestCountController.setGuestCount(invoker);
            }
        }
        return null;
    }

    @Override
    public Connection getConnection() throws Exception {
        Context context = new InitialContext();
        DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/sporder");
        Connection conn = ds.getConnection();

        return  conn;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Path getHtmlTemplateDirectory() {
        return Paths.get(this.servletContext.getRealPath("WEB-INF/htmltemplate"));
    }
}
