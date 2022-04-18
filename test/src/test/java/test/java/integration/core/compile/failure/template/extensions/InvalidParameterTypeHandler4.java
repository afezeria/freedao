package test.java.integration.core.compile.failure.template.extensions;

/**
 * handle方法不是静态方法
 *
 * @author afezeria
 */
public class InvalidParameterTypeHandler4 {
    public Object handle(Object abc) {
        throw new IllegalStateException();
    }

}
