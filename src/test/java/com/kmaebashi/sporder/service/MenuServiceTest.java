package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.*;
import com.kmaebashi.nctfwimpl.DbAccessContextImpl;
import com.kmaebashi.nctfwimpl.DbAccessInvokerImpl;
import com.kmaebashi.nctfwimpl.ServiceContextImpl;
import com.kmaebashi.nctfwimpl.ServiceInvokerImpl;
import com.kmaebashi.simplelogger.Logger;
import com.kmaebashi.simpleloggerimpl.FileLogger;
import com.kmaebashi.sporder.SpOrderTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.nio.file.Paths;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {
    private static Connection conn;
    private static Logger logger;

    @BeforeAll
    static void connectDb() throws Exception {
        conn = SpOrderTestUtil.getConnection();
        logger = new FileLogger("./log", "MenuServiceTest");
    }

    @Test
    void showMenuTest001() {
        DbAccessContext dc = new DbAccessContextImpl(this.conn, logger);
        DbAccessInvoker di = new DbAccessInvokerImpl(dc);
        ServiceContext sc = new ServiceContextImpl(di,
                Paths.get("./src/main/resources/htmltemplate"),
                logger);
        ServiceInvoker si = new ServiceInvokerImpl(sc);

        DocumentResult dr = MenuService.showMenu(si, "nonbe_republic", 1);
        String html = dr.getDocument().html();
    }

    @Test
    void showMenuTest002() {
        DbAccessContext dc = new DbAccessContextImpl(this.conn, logger);
        DbAccessInvoker di = new DbAccessInvokerImpl(dc);
        ServiceContext sc = new ServiceContextImpl(di,
                Paths.get("./src/main/resources/htmltemplate"),
                logger);
        ServiceInvoker si = new ServiceInvokerImpl(sc);

        DocumentResult dr = MenuService.showMenu(si, "nonbe_republic", 2);
        String html = dr.getDocument().html();
    }
}