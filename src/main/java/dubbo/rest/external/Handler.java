package dubbo.rest.external;

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
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

@Component
public class Handler {

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

//                    Req req = JSON.parseObject(body, Req.class);
//                    String methodName = req.getMethodName();

                    Req reqJson = null;
                    try {
                        reqJson = mapper.readValue(body, Req.class);
                    } catch (JsonProcessingException e) {
                        logger.info("body parser error: {}", body, e);
                    }
                    String methodName = reqJson.getMethodName();

                            Object genericInvokeResult = genericInvoke.genericCall(
                                    interfaceName, group, version, methodName,
                                    Handler.this.getParameterTypes(interfaceName, methodName, version,group,application).toArray(new String[]{}),
                                    reqJson.getParamValues());
                    return genericInvokeResult;

                }),
                Object.class));
    }

    private List<String> getParameterTypes(String interfaceName,String methodName, String version, String group, String application){
        MetadataIdentifier metadataIdentifier = new MetadataIdentifier(
                interfaceName,
                version,
                group,
                PROVIDER_SIDE,
                application);
        MetadataReport metadataReport = MetadataReportInstance.getMetadataReport(metadataIdentifier.getIdentifierKey());
        String serviceDefinition = metadataReport.getServiceDefinition(metadataIdentifier);
        List<String> parameterTypes = Optional.ofNullable(serviceDefinition)
                .map(sdStr-> {
                    try {

                        return mapper.readTree(new String(Base64.getDecoder().decode(sdStr)));
                    } catch (JsonProcessingException e) {
                        logger.warn("serviceDefinition 解析错误 {}", sdStr, e);
                        return null;
                    }
                })
                .map(sdJson->sdJson.get("methods"))
                .map(methods->{
                    for(JsonNode md:methods){
                        if(methodName.equals(Optional.ofNullable(md.get("name")).map(n->n.textValue()).orElse(""))){
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
        return parameterTypes;
    }
}
