package dubbo.rest.component;

import org.apache.dubbo.rpc.RpcException;

public class RpcError {
    private final int value;
    private final String exceptionName;
    private final String phrase;

    public RpcError(int code, Exception exception){
        this.value = code;
        this.exceptionName = exception.getClass().getName();
        this.phrase = exception.getMessage();
    }

    public RpcError(RpcStatus rpcStatus){
        this.value = rpcStatus.code();
        this.exceptionName = RpcException.class.getName();
        this.phrase = rpcStatus.phrase();
    }

    public int getCode() {
        return value;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public String getPhrase() {
        return phrase;
    }
}
