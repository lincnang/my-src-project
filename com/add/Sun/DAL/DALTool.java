package com.add.Sun.DAL;

import com.lineage.DatabaseFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DALTool {
    private static final Log _log = LogFactory.getLog(DALTool.class);

    private static void loadResultSetIntoObject(ResultSet rst, Object object) throws IllegalArgumentException, IllegalAccessException, SQLException {
        Class<?> zclass = object.getClass();
        for (Field field : zclass.getDeclaredFields()) {
            field.setAccessible(true);
            DBTable column = field.getAnnotation(DBTable.class);
            Object value = rst.getObject(column.columnName());
            Class<?> type = field.getType();
            if (isPrimitive(type)) {//check primitive type(Point 5)
                Class<?> boxed = boxPrimitiveClass(type);//box if primitive(Point 6)
                try {
                    if (type == long.class) {
                        value = ((Number) value).longValue();
                    } else {
                        value = boxed.cast(value);
                    }
                } catch (Exception e) {
                    _log.error("loadResultSetIntoObject Exception:" + e.getMessage());
                    _log.error("colName:" + column.columnName() + ",type:" + type + ", value:" + value);
                    _log.error("SQL Tool Error: " + e.getLocalizedMessage(), e);
                }
            }
            field.set(object, value);
        }
    }

    private static boolean isPrimitive(Class<?> type) {
        return (type == int.class || type == long.class || type == double.class || type == float.class || type == boolean.class || type == byte.class || type == char.class || type == short.class);
    }

    private static Class<?> boxPrimitiveClass(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == Timestamp.class) {
            return Timestamp.class;
        } else {
            String string = "class '" + type.getName() + "' is not a primitive";
            throw new IllegalArgumentException(string);
        }
    }

    public <T> List<T> selectQuery(Class<T> type, String query, List params) throws SQLException {
        List<T> list = new ArrayList<>();
        //_log.debug("query:" + query);
        try (Connection conn = DatabaseFactory.get().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                if (params.size() > 0) {
                    for (int i = 0; i < params.size(); i++) {
                        stmt.setObject(i + 1, params.get(i));
                        //_log.debug("setObject:" + params.get(i));
                    }
                }
                try (ResultSet rst = stmt.executeQuery()) {
                    while (rst.next()) {
                        T t = type.newInstance();
                        loadResultSetIntoObject(rst, t);// Point 4
                        list.add(t);
                    }
                }
            } catch (Exception e) {
                _log.error("Exception:" + e.getMessage());
                _log.error("SQL Tool Error: " + e.getLocalizedMessage(), e);
            }
        }
        return list;
    }

    public void Execute(String sql) {
        try (Connection conn = DatabaseFactory.get().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            _log.error(e.getMessage());
            _log.debug("sql:" + sql);
            throw new RuntimeException("SQL Tool Error: " + e.getMessage(), e);
        }
    }

    public void Execute(String sql, List params) {
        try (Connection conn = DatabaseFactory.get().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                stmt.execute();
            }
        } catch (Exception e) {
            _log.error(e.getMessage());
            _log.debug("sql:" + sql);
            throw new RuntimeException("SQL Tool Error: " + e.getMessage(), e);
        }
    }

    public long Count(String tableName, String where) {
        String sql = "SELECT COUNT(*) AS count FROM " + tableName + " " + where;
        //_log.debug("sql:" + sql);
        long result = 0;
        try (Connection conn = DatabaseFactory.get().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    result = rs.getLong("count");
                    //_log.debug("count:" + result);
                }
            }
        } catch (Exception e) {
            _log.error("Exception:" + e.getMessage());
            _log.error("SQL Tool Error: " + e.getLocalizedMessage(), e);
            throw new RuntimeException("SQL Tool Error: " + e.getMessage(), e);
        }
        return result;
    }

    public <T> void Insert(T pojo, String tableName, boolean isAutoId) throws IllegalAccessException {
        Field[] fields = pojo.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        StringBuilder colNames = new StringBuilder();
        StringBuilder colValues = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            try {
                if (!fields[i].isAccessible()) {
                    fields[i].setAccessible(true);
                }
                String name = fields[i].getName();
                Object value = fields[i].get(pojo);
                String colValue = "";
                if (value != null) {
                    colValue = value.toString();
                }
                //_log.debug("Value of Field "+name+" is "+colValue);
                if (i != 0 && (colNames.length() > 0)) {
                    colNames.append(",");
                    colValues.append(",");
                }
                if (name == "id" && isAutoId) {
                    continue;
                }
                if (fields[i].get(pojo) instanceof String || fields[i].get(pojo) instanceof java.sql.Timestamp) {
                    colValues.append("'").append(colValue).append("'");
                } else {
                    colValues.append(colValue);
                }
                colNames.append(name);
            } catch (Exception e) {
                _log.error("SQL Tool Error:" + e.getMessage());
                _log.debug("colNames:" + colNames);
                _log.debug("colValues:" + colValues);
                throw e;
            }
        }
        String sql = "insert into " + tableName + " (" + colNames + ") values(" + colValues + ");";
        //_log.debug("sql:" + sql);
        Execute(sql);
    }
}
