package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.simplelogger.Logger;
import com.kmaebashi.simpleloggerimpl.FileLogger;
import com.kmaebashi.sporder.SpOrderTestUtil;
import com.kmaebashi.sporder.common.Locale;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuServiceTest {
    private static final String RT_NO_SUB_FIRST = "rt_no_sub_first";
    private static final String RT_WITH_SUB_FIRST = "rt_with_sub_first";
    private static final String RT_CLOSED = "rt_closed";
    private static final String RT_NO_CATEGORIES = "rt_no_categories";
    private static final String SESSION_NO_SUB_FIRST = "ST00000000000000000001";
    private static final String SESSION_WITH_SUB_FIRST = "ST00000000000000000002";
    private static final String SESSION_EXPIRED = "ST00000000000000000003";
    private static final String SESSION_CLOSED = "ST00000000000000000004";
    private static final String SESSION_NO_CATEGORIES = "ST00000000000000000005";

    private static Connection conn;
    private static Logger logger;
    private static ServiceInvoker invoker;

    @BeforeAll
    static void connectDb() throws Exception {
        conn = SpOrderTestUtil.getConnection();
        conn.setAutoCommit(false);
        logger = new FileLogger("./log", "MenuServiceTest");
        invoker = SpOrderTestUtil.createServiceInvoker(conn, logger);
    }

    @BeforeEach
    void loadTestData() throws Exception {
        SpOrderTestUtil.loadServiceCsv(conn, MenuService.class,
                "m_restaurants.csv",
                "m_words.csv",
                "m_tables.csv",
                "t_order_groups.csv",
                "t_device_sessions.csv",
                "m_categories.csv",
                "m_subcategories.csv",
                "m_menu_items.csv",
                "m_categories_items.csv");
    }

    @AfterEach
    void rollback() throws Exception {
        conn.rollback();
    }

    @AfterAll
    static void closeDb() throws Exception {
        conn.close();
    }

    @Test
    void showMenu_categoryIdNull_selectsFirstCategory_withoutSubcategory() {
        Document doc = showMenu(RT_NO_SUB_FIRST, null, SESSION_NO_SUB_FIRST);

        assertActiveCategory(doc, "Direct Category");
        assertSubcategoryTexts(doc);
        assertMenuItemTexts(doc, "Direct Item A", "Direct Item B");
        assertCommonMenuAttributes(doc, RT_NO_SUB_FIRST, "OG00000000000000000001");
    }

    @Test
    void showMenu_categoryIdNull_selectsFirstCategory_withSubcategory() {
        Document doc = showMenu(RT_WITH_SUB_FIRST, null, SESSION_WITH_SUB_FIRST);

        assertActiveCategory(doc, "Parent Category");
        assertSubcategoryTexts(doc, "Subcategory A", "Subcategory B");
        assertMenuItemTexts(doc, "Sub Item A", "Sub Item B");
        assertCommonMenuAttributes(doc, RT_WITH_SUB_FIRST, "OG00000000000000000002");
    }

    @Test
    void showMenu_categoryIdSpecified_rendersCategory_withoutSubcategory() {
        Document doc = showMenu(RT_WITH_SUB_FIRST, 40, SESSION_WITH_SUB_FIRST);

        assertActiveCategory(doc, "Plain Category");
        assertSubcategoryTexts(doc);
        assertMenuItemTexts(doc, "Plain Item");
        assertCommonMenuAttributes(doc, RT_WITH_SUB_FIRST, "OG00000000000000000002");
    }

    @Test
    void showMenu_categoryIdSpecified_rendersCategory_withSubcategory() {
        Document doc = showMenu(RT_NO_SUB_FIRST, 20, SESSION_NO_SUB_FIRST);

        assertActiveCategory(doc, "Nested Category");
        assertSubcategoryTexts(doc, "Nested Subcategory");
        assertMenuItemTexts(doc, "Nested Item");
        assertCommonMenuAttributes(doc, RT_NO_SUB_FIRST, "OG00000000000000000001");
    }

    @Test
    void showMenu_sessionTokenNotFound_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> showMenu(RT_NO_SUB_FIRST, null, "ST99999999999999999999"));
    }

    @Test
    void showMenu_sessionTokenExpired_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> showMenu(RT_NO_SUB_FIRST, null, SESSION_EXPIRED));
    }

    @Test
    void showMenu_sessionRtIdMismatch_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> showMenu(RT_WITH_SUB_FIRST, null, SESSION_NO_SUB_FIRST));
    }

    @Test
    void showMenu_orderGroupClosed_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> showMenu(RT_CLOSED, null, SESSION_CLOSED));
    }

    @Test
    void showMenu_categoryIdNullAndNoCategory_throwsNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> showMenu(RT_NO_CATEGORIES, null, SESSION_NO_CATEGORIES));
    }

    private static Document showMenu(String rtId, Integer categoryId, String sessionToken) {
        DocumentResult result = MenuService.showMenu(invoker, rtId, categoryId, Locale.JP, sessionToken);
        return result.getDocument();
    }

    private static void assertActiveCategory(Document doc, String expectedText) {
        Element activeCategory = assertSingle(doc, "#category-bar .category.active");
        assertEquals(expectedText, activeCategory.text());
    }

    private static void assertSubcategoryTexts(Document doc, String... expectedTexts) {
        Elements subcategories = doc.select("#menu-list .subcategory");
        assertEquals(expectedTexts.length, subcategories.size());
        for (int i = 0; i < expectedTexts.length; i++) {
            assertEquals(expectedTexts[i], subcategories.get(i).text());
        }
    }

    private static void assertMenuItemTexts(Document doc, String... expectedTexts) {
        Elements menuItems = doc.select("#menu-list .menu-item");
        assertEquals(expectedTexts.length, menuItems.size());
        for (int i = 0; i < expectedTexts.length; i++) {
            assertEquals(expectedTexts[i], menuItems.get(i).selectFirst(".menu-name").text());
            assertTrue(menuItems.get(i).hasAttr("data-menu-item-id"));
            assertTrue(menuItems.get(i).selectFirst(".menu-thumbnail").attr("src").startsWith("menuimages?rt_id="));
        }
    }

    private static void assertCommonMenuAttributes(Document doc, String rtId, String orderGroupId) {
        assertEquals(orderGroupId, doc.body().attr("data-order-group-id"));
        assertEquals("orderlist?rt_id=" + rtId, doc.select(".bottom-nav .nav-item").get(1).attr("href"));
        assertEquals("orderhistory?rt_id=" + rtId, doc.select(".bottom-nav .nav-item").get(2).attr("href"));
    }

    private static Element assertSingle(Document doc, String cssQuery) {
        Elements elements = doc.select(cssQuery);
        assertEquals(1, elements.size(), cssQuery);
        return elements.first();
    }
}
