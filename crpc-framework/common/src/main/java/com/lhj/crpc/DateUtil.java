package com.lhj.crpc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description：
 * @createTime：2023-09-0210:04
 * @author：banyanmei
 */
public class DateUtil {
    
    public static Date get(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return  sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
}
