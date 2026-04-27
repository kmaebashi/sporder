package com.kmaebashi.sporder.servlet;

import com.kmaebashi.nctfw.InternalException;
import com.kmaebashi.simpleloggerimpl.FileLogger;
import com.kmaebashi.sporder.router.SpOrderRouter;
import com.kmaebashi.sporder.util.Log;
import jakarta.servlet.http.HttpServlet;
import com.kmaebashi.simplelogger.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ResourceBundle;

public class SpOrderServlet extends HttpServlet {
    private SpOrderRouter router;
    private Logger logger;
    public void init() {
        ResourceBundle rb = ResourceBundle.getBundle("application");
        String logDirectory = rb.getString("sporder.log-directory");
        try {
            this.logger = new FileLogger(logDirectory, "SpOrderLog");
        } catch (IOException ex) {
            throw new InternalException("ログファイルの作成に失敗しました。", ex);
        }
        Log.setLogger(logger);
        this.router = new SpOrderRouter(this.getServletContext(), this.logger, rb);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Servlet.serice start." + request.getRequestURI());

        this.router.execute(request, response);

        logger.info("Servlet.serice end." + request.getRequestURI());
    }
}
