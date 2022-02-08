package com.github.afezeria.freedao.runtime.classic;

/**
 * 字段值生成器父类，该类表示字段的值由数据库生成
 *
 */
public class ValueGenerator {

    /**
     * 传入参数为待插入对象，返回
     * todo 具体逻辑没想好
     *
     * @param obj
     * @return
     */
    public Object gen(Object obj) {
        throw new RuntimeException();
    }

}
