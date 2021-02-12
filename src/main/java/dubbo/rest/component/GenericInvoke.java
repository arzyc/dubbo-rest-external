package dubbo.rest.component;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GenericInvoke {

    @Autowired
    private DubboBootstrap dubboBootstrap;

    //TODO change Cache impl
    private ConcurrentHashMap<String, ReferenceConfig<GenericService>> cachedConfig = new ConcurrentHashMap<>();
    private Logger logger = LoggerFactory.getLogger(GenericInvoke.class);

    public Object genericCall(String interfaceName, String group,
                                     String version, String methodName, String[] paramTypes,
                                     Object[] paramObjs) {
        ReferenceConfig<GenericService> reference;
        reference = addNewReference(interfaceName, group, version);

        try {
            GenericService svc =
                    ReferenceConfigCache
                    .getCache()
                    .get(reference);
//            reference.get();
            logger.info("dubbo generic invoke, service is {}, method is {} , paramTypes is {} , paramObjs is {} , svc" +
                            " is {}.", interfaceName
                    , methodName,paramTypes,paramObjs,svc);
            return svc.$invoke(methodName, paramTypes, paramObjs);
        } catch (Exception e) {
            logger.error("Generic invoke failed",e);
            if (e instanceof RpcException) {
                RpcException e1 = (RpcException)e;
                return e1.getCode();
            }
            throw e;
        }
    }

    private ReferenceConfig<GenericService> addNewReference(String interfaceName,
                                                                   String group, String version) {
        ReferenceConfig<GenericService> reference;
        String cachedKey = interfaceName + group + version;
        reference = cachedConfig.get(cachedKey);
        if (reference == null) {
            ReferenceConfig<GenericService> newReference = initReference(interfaceName, group,
                    version);
            ReferenceConfig<GenericService> oldReference = cachedConfig.putIfAbsent(cachedKey, newReference);
            if (oldReference != null) {
                reference = oldReference;
            } else {
                reference = newReference;
            }
        }
        return reference;
    }

    private ReferenceConfig<GenericService> initReference(String interfaceName, String group,
                                                                 String version) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setGeneric("true");//.setGeneric(true);
        reference.setBootstrap(dubboBootstrap);
        reference.setGroup(group);
        reference.setVersion(version);
        reference.setInterface(interfaceName);
        return reference;
    }
}
