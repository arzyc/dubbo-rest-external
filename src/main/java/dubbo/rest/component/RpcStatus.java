package dubbo.rest.component;

public enum RpcStatus {
    UNKNOWN_EXCEPTION(0,"UNKNOWN_EXCEPTION"),
    NETWORK_EXCEPTION(1,"NETWORK_EXCEPTION"),
    TIMEOUT_EXCEPTION(2,"TIMEOUT_EXCEPTION"),
    BIZ_EXCEPTION(3,"BIZ_EXCEPTION"),
    FORBIDDEN_EXCEPTION(4,"FORBIDDEN_EXCEPTION"),
    SERIALIZATION_EXCEPTION(5,"SERIALIZATION_EXCEPTION"),
    NO_INVOKER_AVAILABLE_AFTER_FILTER(6,"NO_INVOKER_AVAILABLE_AFTER_FILTER"),
    LIMIT_EXCEEDED_EXCEPTION(7,"LIMIT_EXCEEDED_EXCEPTION"),
    TIMEOUT_TERMINATE(8,"TIMEOUT_TERMINATE");

    private final int value;
    private final String phrase;

    RpcStatus(int code, String phrase){
        this.value = code;
        this.phrase = phrase;
    }

    public int code(){
        return this.value;
    };
    public String phrase(){
        return this.phrase;
    }

    public static RpcStatus resolve(int code) {
        RpcStatus[] allStatus = values();
        int length = allStatus.length;

        for(int i = 0; i < length; ++i) {
            RpcStatus status = allStatus[i];
            if (status.value == code) {
                return status;
            }
        }
        return RpcStatus.UNKNOWN_EXCEPTION;
    }
}
