

配置：
```
dubbo:
  provider:
    enabled: true
    accounts:
      - name: test1
        url: http:/aba.com/{{statck}}
        stacks: 
          - test1
       
aws:
    enabled: true
    accounts:
        - name: xhqb-aws
          accountId: 1234567899999
          defaultKeyPair: 'my-keypair'
          discovery: "dubbo"
          discoveryEnabled: true
          regions:
            - name: cn-north-1

```

hacking thins...:

 目前为了不更多的修改clouddriver-aws的代码， 采用了比较投机的方案：
 
 因为必须配置了discovery， discoveryEnabled 才会执行服务发现相关的代码。
 discovery 在实现时就当成eureka的地址了。 
 现约定如下：  
 discovery: 如果设置值为dubbo, 认为只启动dubbo
 如果需要同时支持eureka， 采用http://myeureka.com/abc/?dubbo=true的形式。
 


##当启用dubbo时， 不支持Rolling相关策略， 如RollingRedBack
