package com.github.hbq969.gateway.master.utils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class SqlUtils {

    public static final String PROFILE_DEFAULT = "default";

    public static final String SEM = ";";

    public static InputStream read(String dir, String file, String profile) {
        if (dir == null) {
            dir = "/";
        }
        dir = (dir.endsWith("/") ? dir : String.join("", dir, "/"));
        String path;
        if (StringUtils.isEmpty(profile) || PROFILE_DEFAULT.equals(profile)) {
            path = String.join("", dir, file);
            log.info("读取文件: {}", path);
            return SqlUtils.class.getResourceAsStream(path);
        } else {
            String[] array = file.split("\\.");
            if (array.length != 2) {
                throw new IllegalArgumentException();
            }
            path = String.join("", dir, array[0], "-", profile, ".", array[1]);
            InputStream in = SqlUtils.class.getResourceAsStream(path);
            if (null == in) {
                path = String.join("", dir, file);
                log.info("读取文件: {}", path);
                return SqlUtils.class.getResourceAsStream(path);
            } else {
                log.info("读取文件: {}", path);
                return in;
            }
        }
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     */
    public static void initDataSql(JdbcTemplate jt, String dir, String file, String profile) {
        initDataSql(jt, dir, file, profile, Charset.forName("utf-8"));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     * @param charset 脚本文件编码
     */
    public static void initDataSql(JdbcTemplate jt, String dir, String file, String profile,
                                   Charset charset) {
        try (InputStream in = read(dir, file, profile)) {
            boolean execute = false;
            List box = new ArrayList();
            String sql;
            List list = IOUtils.readLines(in, charset.name());
            for (Object line : list) {
                String str = String.valueOf(line).trim();
                if (str.endsWith(SEM)) {
                    str = str.substring(0, str.length() - 1);
                    execute = true;
                }
                box.add(str);
                if (execute) {
                    sql = String.join("\n", box).trim();
                    try {
                        jt.update(sql);
                    } catch (DataAccessException e) {
                    }
                    box.clear();
                    execute = false;
                }
            }

        } catch (Exception e) {
            log.error(String.format("读取脚本文件[%]异常", file, e));
        }
    }
}
