package dubbo.rest.external;

import dubbo.rest.component.GenericInvoke;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportInstance;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

@Component
public class Handler {

    static final AtomicInteger COUNT = new AtomicInteger();
    static final Random RND = new Random();
    private Logger logger = LoggerFactory.getLogger(Handler.class);

    @Autowired
    GenericInvoke genericInvoke;

    public Mono<ServerResponse> hello(ServerRequest request){
        Mono<String> bodyStr = request.bodyToMono(String.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(
                bodyStr.map(body->{
                    String application = request.pathVariable("application");
                    String interfaceName = request.pathVariable("service");
                    String group = request.queryParam("group").orElse("");
                    String version = request.queryParam("version").orElse("");
                    Object genericInvokeResult =
                            genericInvoke.genericCall(
                                    interfaceName, group, version,
                                    "sayHello",
                                    new String[] { String.class.getName() },
                                    new Object[] { "name" });

                    System.out.println(">>:"+genericInvokeResult);
                    return String.valueOf(genericInvokeResult);
                }),
                String.class));
//        return ServerResponse.ok().build(bodyStr.thenEmpty(Mono.empty()));
    }
    public Mono<ServerResponse> hello3(ServerRequest request) {
        Mono<String> bodyStr = request.bodyToMono(String.class);
        return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters
                                        .fromValue(
//                                String.format("Hi, %s!",request.pathVariable("name"))
                                                Optional.ofNullable("").map(x->{
                                                    String application = request.pathVariable("application");
                                                    String interfaceName = request.pathVariable("service");
                                                    String group = request.queryParam("group").orElse("dubbo");
                                                    String version = request.queryParam("version").orElse("1.0.0");

//                                                    ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
//                                                    reference.setInterface(interfaceName);
//                                                    reference.setVersion(version);
//                                                    reference.setGeneric("true");

                                                    MetadataReport metadataReport = MetadataReportInstance.getMetadataReport();
                                                    String serviceDefinition = metadataReport.getServiceDefinition(
                                                            new MetadataIdentifier(
                                                                    interfaceName,
                                                                    version,
                                                                    group,
                                                                    PROVIDER_SIDE,
                                                                    application)
                                                    );

//                                                    String body = request
//                                                            .body(BodyExtractors.toMono(String.class)).thenReturn("").block();
//                                                    logger.info("body {}", body);
//                                            .blockOptional();
//                                    GenericService genericService = ReferenceConfigCache
//                                            .getCache()
//                                            .get(reference);
                                                    Object genericInvokeResult =
//                                            genericService
//                                            .$invoke(
//                                                    "sayHello",
//                                                    new String[] { String.class.getName() },
//                                                    new Object[] { "dubbo generic invoke" });
                                                            genericInvoke.genericCall(
                                                                    interfaceName,group, version,"sayHello",
                                                                    new String[] { String.class.getName() },
                                                                    new Object[] { "dubbo generic invoke" });
                                                    System.out.println(">>:"+genericInvokeResult);
                                                    return genericInvokeResult;
                                                })
                                        )
                        );


    }
}
