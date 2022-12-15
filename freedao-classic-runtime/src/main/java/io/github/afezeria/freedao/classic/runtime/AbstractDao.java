package io.github.afezeria.freedao.classic.runtime;

import io.github.afezeria.freedao.classic.runtime.context.DaoContext;

/**
 * @author afezeria
 */
public abstract class AbstractDao {
    protected DaoContext __context__;

    public void setContext(DaoContext context) {
        this.__context__ = context;
    }

}