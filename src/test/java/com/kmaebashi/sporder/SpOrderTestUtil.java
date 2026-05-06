package com.kmaebashi.sporder;


import com.kmaebashi.nctfw.DbAccessContext;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.nctfw.ServiceContext;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.nctfwimpl.DbAccessContextImpl;
import com.kmaebashi.nctfwimpl.DbAccessInvokerImpl;
import com.kmaebashi.nctfwimpl.ServiceContextImpl;
import com.kmaebashi.nctfwimpl.ServiceInvokerImpl;
import com.kmaebashi.simplelogger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpOrderTestUtil {
    private SpOrderTestUtil() {}

    public static final String HTML_TEMPLATE_PATH = "./src/main/webapp/WEB-INF/htmltemplate";
    private static final Path SERVICE_TEST_DATA_PATH = Paths.get("./src/test/resources/TestData/service");

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/spordertestdb?currentSchema=sporderschema",
                "spordertestuser", "spordertest#0506");
        return conn;
    }

    public static ServiceInvoker createServiceInvoker(Connection conn, Logger logger) {
        DbAccessContext dc = new DbAccessContextImpl(conn, logger);
        DbAccessInvoker di = new DbAccessInvokerImpl(dc);
        ServiceContext sc = new ServiceContextImpl(di, Paths.get(HTML_TEMPLATE_PATH), logger);
        return new ServiceInvokerImpl(sc);
    }

    public static void loadServiceCsv(Connection conn, Class<?> testTargetClass, String... fileNames) throws Exception {
        Path directory = SERVICE_TEST_DATA_PATH.resolve(testTargetClass.getSimpleName());
        for (String fileName : fileNames) {
            loadCsv(conn, directory.resolve(fileName));
        }
    }

    private static void loadCsv(Connection conn, Path csvPath) throws Exception {
        String tableName = csvPath.getFileName().toString().replaceFirst("\\.csv$", "");
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return;
            }
            List<String> columns = parseCsvLine(headerLine);
            Map<String, Integer> columnTypes = getColumnTypes(conn, tableName);
            String sql = createInsertSql(tableName, columns);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    List<String> values = parseCsvLine(line);
                    if (values.size() != columns.size()) {
                        throw new IllegalArgumentException("CSV column count mismatch: " + csvPath + " line=" + line);
                    }
                    for (int i = 0; i < columns.size(); i++) {
                        String columnName = columns.get(i);
                        int sqlType = columnTypes.get(columnName.toLowerCase());
                        setParameter(ps, i + 1, values.get(i), sqlType);
                    }
                    ps.executeUpdate();
                }
            }
        }
    }

    private static Map<String, Integer> getColumnTypes(Connection conn, String tableName) throws Exception {
        Map<String, Integer> ret = new HashMap<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getColumns(null, conn.getSchema(), tableName, null)) {
            while (rs.next()) {
                ret.put(rs.getString("COLUMN_NAME").toLowerCase(), rs.getInt("DATA_TYPE"));
            }
        }
        if (ret.isEmpty()) {
            try (ResultSet rs = metaData.getColumns(null, conn.getSchema(), tableName.toLowerCase(), null)) {
                while (rs.next()) {
                    ret.put(rs.getString("COLUMN_NAME").toLowerCase(), rs.getInt("DATA_TYPE"));
                }
            }
        }
        if (ret.isEmpty()) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        return ret;
    }

    private static String createInsertSql(String tableName, List<String> columns) {
        String columnPart = String.join(", ", columns);
        String placeholderPart = String.join(", ", columns.stream().map(column -> "?").toList());
        return "INSERT INTO " + tableName + " (" + columnPart + ") VALUES (" + placeholderPart + ")";
    }

    private static void setParameter(PreparedStatement ps, int parameterIndex, String value, int sqlType)
            throws Exception {
        if ("<null>".equals(value)) {
            ps.setNull(parameterIndex, sqlType);
            return;
        }
        switch (sqlType) {
            case Types.INTEGER, Types.SMALLINT -> ps.setInt(parameterIndex, Integer.parseInt(value));
            case Types.BOOLEAN, Types.BIT -> ps.setBoolean(parameterIndex, Boolean.parseBoolean(value));
            case Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE -> ps.setObject(parameterIndex,
                    LocalDateTime.parse(value));
            default -> ps.setString(parameterIndex, value);
        }
    }

    private static List<String> parseCsvLine(String line) throws IOException {
        List<String> ret = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    inQuote = !inQuote;
                }
            } else if (ch == ',' && !inQuote) {
                ret.add(value.toString());
                value.setLength(0);
            } else {
                value.append(ch);
            }
        }
        if (inQuote) {
            throw new IOException("Unclosed quote in CSV line: " + line);
        }
        ret.add(value.toString());
        return ret;
    }
}
