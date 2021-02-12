package dubbo.rest.config;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportInstance;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfig {
//    @Bean
//    Registry getRegistry(@Value("${dubbo.registry.address}") String url) {
////        URL url = URL.valueOf(registryAddress);
////        if (StringUtils.isNotEmpty(group)) {
////            url = url.addParameter(org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY, group);
////        }
//        RegistryFactory registryFactory = ExtensionLoader
//                .getExtensionLoader(RegistryFactory.class)
//                .getAdaptiveExtension();
//        Registry registry = registryFactory.getRegistry(URL.valueOf(url));
//        return registry;
//    }

    @Bean(
            initMethod = "start",
            destroyMethod = "stop"
    )
    DubboBootstrap dubboBootstrap(
            @Value("${spring.application.name}") String name,
            @Value("${dubbo.registry.address}") String registryAddress,
            @Value("${dubbo.metadata-report.address}") String metadataReportAddress){
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        ApplicationConfig applicationConfig = new ApplicationConfig(name);
        RegistryConfig registryConfig = new RegistryConfig(registryAddress);
        MetadataReportConfig metadataReportConfig = new MetadataReportConfig(metadataReportAddress);
        bootstrap
                .application(applicationConfig)
                .registry(registryConfig)
                .metadataReport(metadataReportConfig);

        return bootstrap;
    }

//    @Bean
//    MetadataReport getMetadataReport(@Value("${dubbo.metadata-report.address}") String metadataReportURL) {
//        MetadataReportInstance.init(URL.valueOf(metadataReportURL));
//        MetadataReport metadataReport = MetadataReportInstance.getMetadataReport(true);
//        return metadataReport;
//    }
}
