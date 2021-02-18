dubbo-rest-external

dubbo服务转化成rest服务

http->dubbo-rest-external->dubbo-rpc

请求uri格式
host:port/{application}/{service}?group={group}&version={version}
示例： http://127.0.0.1:8182/dubbo-demo-annotation-provider/org.apache.dubbo.demo.DemoService

请求内容格式
{
    "methodName" : 方法名,
    "paramValues": [参数...]
}
示例
{
    "methodName" : "sayHello1",
    "paramValues": [
        "abc"
    ]
}
