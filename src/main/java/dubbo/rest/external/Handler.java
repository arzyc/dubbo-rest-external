package dubbo.rest.external;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dubbo.rest.component.GenericInvoke;
import dubbo.rest.dto.Req;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportInstance;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
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

    private final ObjectMapper mapper = new ObjectMapper();

    public Mono<ServerResponse> exec(ServerRequest request){
        Mono<String> bodyStr = request.bodyToMono(String.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(
                bodyStr.map(body->{
                    String application = request.pathVariable("application");
                    String interfaceName = request.pathVariable("service");
                    String group = request.queryParam("group").orElse("");
                    String version = request.queryParam("version").orElse("");
//                    String methodName = "sayHello";

                    Req req = JSON.parseObject(body, Req.class);

                    MetadataReport metadataReport = MetadataReportInstance.getMetadataReport();
                    String serviceDefinition = metadataReport.getServiceDefinition(
                            new MetadataIdentifier(
                                    interfaceName,
                                    version,
                                    group,
                                    PROVIDER_SIDE,
                                    application)
                    );
                    List<String> parameterTypes = Optional.ofNullable(serviceDefinition)
                    .map(sdStr-> {
                        try {
                            return mapper.readTree(sdStr);
                        } catch (JsonProcessingException e) {
                            return null;
                        }
                    })
                    .map(sdJson->sdJson.get("methods"))
                    .map(methods->{
                        for(JsonNode md:methods){
                            if(req.getMethodName().equals(Optional.ofNullable(md.get("name")).map(n->n.textValue()).orElse(""))){
                                return md;
                            }
                        }
                        return null;
                    })
                    .map(md->md.get("parameterTypes"))
                    .map(pts->{
                        List<String> xparameterTypes = new ArrayList<>();
                        for(JsonNode pt:pts){
                            pt.textValue();
                            xparameterTypes.add(pt.textValue());
                        }
                        return xparameterTypes;
                    }).orElse(new ArrayList<>());
                        Object genericInvokeResult =
                                genericInvoke.genericCall(
                                        interfaceName, group, version,
                                        req.getMethodName(),
                                        parameterTypes.toArray(new String[]{}),
                                        req.getParamValues());
                        System.out.println(">>:"+genericInvokeResult);
                        return String.valueOf(genericInvokeResult);

                }),
                String.class));
//        return ServerResponse.ok().build(bodyStr.thenEmpty(Mono.empty()));
    }
}
