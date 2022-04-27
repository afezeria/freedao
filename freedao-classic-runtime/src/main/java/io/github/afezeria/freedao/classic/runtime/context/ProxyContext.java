package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.classic.runtime.SqlSignature;

import java.util.List;

/**
 * @author afezeria
 */
public class ProxyContext extends DaoContext {
    @Override
    @SuppressWarnings("unchecked")
    public <T, E> T proxy(SqlSignature<T, E> signature, Object... methodArgs) {
        Object[] sqlAndArgs = getDelegate().buildSql(signature, methodArgs, signature.getSqlBuilderClosure());
        return getDelegate().execute(
                signature,
                methodArgs,
                (String) sqlAndArgs[0],
                (List<Object>) sqlAndArgs[1],
                signature.getSqlExecutor(),
                signature.getResultHandler()
        );
    }
}
