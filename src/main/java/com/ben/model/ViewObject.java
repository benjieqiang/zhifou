package com.ben.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ViewObject
 * @author: benjamin
 * @version: 1.0
 * @description: ViewObject是个map的包装类，方便把任何类型的数据放到一起。通过model将数据输送到页面
 * @createTime: 2019/08/17/09:10
 */

public class ViewObject implements Serializable {
    private Map<String, Object> objs = new HashMap<String, Object>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }

    @Override
    public String toString() {
        return "ViewObject{" +
                "objs=" + objs +
                '}';
    }
}