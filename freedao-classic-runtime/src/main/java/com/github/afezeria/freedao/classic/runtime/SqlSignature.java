package com.github.afezeria.freedao.classic.runtime;

import com.github.afezeria.freedao.StatementType;

/**
 * sql签名
 *
 */
public class SqlSignature {
    /**
     * sql类型
     */
    private final StatementType type;
    private final Class<?> daoClass;
    private final String method;
    private final Class<?> returnType;
    private final Class[] parameterTypeList;

    public SqlSignature(StatementType type, Class<?> daoClass, String method, Class<?> returnType, Class... parameterTypeList) {
        this.type = type;
        this.daoClass = daoClass;
        this.method = method;
        this.returnType = returnType;
        this.parameterTypeList = parameterTypeList;
    }

    public StatementType getType() {
        return type;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }

    public String getMethod() {
        return method;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class[] getParameterTypeList() {
        return parameterTypeList;
    }
}
