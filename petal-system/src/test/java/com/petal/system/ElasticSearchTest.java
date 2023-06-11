package com.petal.system;

import com.petal.common.base.entity.SysOperationLog;
import com.petal.system.mapper.SysOperationLogMapper;
import com.petal.system.service.SysOperationLogService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@SpringBootTest(classes = PetalSystemApplication.class)
public class ElasticSearchTest {

    private RestHighLevelClient restHighLevelClient;

    private SysOperationLogService sysOperationLogService;

    private SysOperationLogMapper sysOperationLogMapper;

    @Autowired
    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Autowired
    public void setSysOperationLogService(SysOperationLogService sysOperationLogService) {
        this.sysOperationLogService = sysOperationLogService;
    }

    @Autowired
    public void setSysOperationLogMapper(SysOperationLogMapper sysOperationLogMapper) {
        this.sysOperationLogMapper = sysOperationLogMapper;
    }

    /**
     * 操作日志的es索引
     */
    private static final String OPER_LOG_INDEX="sys-operation-log-index";

    /*
     1:创建操作日志的es索引


     PUT operation-log-index {
        "mappings":{

            "properties":{

                "username":{
                    "type":"text",
                    "analyzer":"standard"
                },
                "type":{
                    "type":"keyword"
                },
                "uri":{
                    "type":"keyword"
                },
                "time":{
                    "type":"keyword"
                },
                "ip":{
                    "type":"keyword"
                },
                "address":{
                    "type":"keyword"
                },
                "browser":{
                    "type":"keyword"
                },
                "os":{
                    "type":"keyword"
                },
                "operTime":{
                    "type":"date"
                 },
                "delFlag":{
                    "type":"integer"
                }


            }

        }

     }

     */
    @Test
    //注意：使用XContentFactory.jsonBuilder()创建索引，不需要把"mappings":{}这个算上去。不然会报错。也就是说不可以写startObject("mappings").endObject()
    public void createOperLogEsIndex() throws IOException {

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(OPER_LOG_INDEX);

        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()// { 开始

                .startObject("properties")//"properties"{

                .startObject("username") //"username"{
                .field("type", "text") // "type":"text",
                .field("analyzer", "standard")//"analyzer":"standard"
                .endObject()// },username的结束

                .startObject("type") //"type":{
                .field("type","keyword")
                .endObject() // },type的结束

                .startObject("uri") //"uri":{
                .field("type","keyword")
                .endObject() // },uri的结束

                .startObject("time") //"time":{
                .field("type","keyword")
                .endObject() // },time的结束

                .startObject("ip") //"ip":{
                .field("type","keyword")
                .endObject() // },ip的结束

                .startObject("address") //"address":{
                .field("type","keyword")
                .endObject() // },address的结束

                .startObject("browser") //"browser":{
                .field("type","keyword")
                .endObject() // },browser的结束

                .startObject("os") //"os":{
                .field("type","keyword")
                .endObject() // },os的结束

                .startObject("operTime") //"operTime":{
                .field("type","date")
                .endObject() // },operTime的结束

                .startObject("delFlag") //"delFlag":{
                .field("type","integer")
                .endObject() // },delFlag的结束

                .endObject()// },properties的结束

                .endObject();// 结束}


        createIndexRequest.mapping(xContentBuilder);

        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);

    }


    /**
     * 删除操作日志的es索引
     * @throws IOException ioexception
     */
    @Test
    public void deleteOperLogEsIndex() throws IOException {

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(OPER_LOG_INDEX);
        restHighLevelClient.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);

    }

    /**
     * 先删除操作日志es索引，然后再创建操作日志的es索引。相当于把上面的deleteOperLogEsIndex和createOperLogEsIndex组合起来了。
     */
    @Test
    public void deleteAndCreateOperLogEsIndex() throws IOException {

        //先删除索引
        this.deleteOperLogEsIndex();

        //再创建索引
        this.createOperLogEsIndex();

    }

    /**
     * 数据量：13w
     * 从mysql中导入操作日志到es。版本1:直接从数据库拉取全部数据，使用es的bulk一直一次性导入进去
     * 第一次耗时：17940ms
     * 第二次耗时：14261ms
     * 第三次耗时：13221ms
     * 平均耗时：15140ms
     */
    @Test
    public void importOperationLog2Es01() throws IOException {

        long start = System.currentTimeMillis();

        BulkRequest bulkRequest = new BulkRequest();

        //查询所有操作日志记录
        List<SysOperationLog> sysOperationLogList = sysOperationLogService.lambdaQuery().list();

        for (SysOperationLog sysOperationLog : sysOperationLogList) {

            IndexRequest indexRequest = new IndexRequest(OPER_LOG_INDEX);
            indexRequest.id(sysOperationLog.getId().toString());

            Map<String, Object> sources = new ConcurrentHashMap<>();
            sources.put("username", sysOperationLog.getUsername());
            sources.put("type", sysOperationLog.getType());
            sources.put("uri", sysOperationLog.getUri());
            sources.put("time", sysOperationLog.getTime());
            sources.put("ip", sysOperationLog.getIp());
            sources.put("address",sysOperationLog.getAddress());
            sources.put("browser", sysOperationLog.getBrowser());
            sources.put("os", sysOperationLog.getOs());
            sources.put("operTime", sysOperationLog.getOperTime());
            sources.put("delFlag", sysOperationLog.getDelFlag());
            indexRequest.source(sources);

            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);

        long end = System.currentTimeMillis();
        System.out.println((end-start)+"ms");
    }

    /**
     * 版本2：采用线程池（core=10，max=20的配置）+分页（大小1000）
     * （线程池参数core=10、max=20）
     * 第一次：10334ms
     * 第一次：10034ms
     * 第三次：10653ms
     * 平均耗时：10340ms
     *
     * @throws IOException          ioexception
     * @throws InterruptedException 中断异常
     */
    @Test
    public void importOperationLog2Es02() throws IOException, InterruptedException {

        long start = System.currentTimeMillis();

        ThreadPoolExecutor threadPoolExecutor
                = new ThreadPoolExecutor(10,
                20,
                2L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        //数据总数
        long count = sysOperationLogService.count();
        //每一页大小。
        int size= 1000;
        //循环次数。如果count=132760（也就是说不是整数，则循环次数+1）
        long circle=(count%size==0)?count/size : (count/size)+1;

        //JUC包下的倒计时器
        CountDownLatch countDownLatch = new CountDownLatch((int)circle);

        for (long i = 1; i < circle+1; i++) {

            int page= (int) ((i-1)*size);

            threadPoolExecutor.submit(()->{

                try {

                    List<SysOperationLog> sysOperationLogList = sysOperationLogMapper.selectAllOperationLogByLimit(page, size);

                    BulkRequest bulkRequest = new BulkRequest();
                    for (SysOperationLog sysOperationLog : sysOperationLogList) {

                        IndexRequest indexRequest = new IndexRequest(OPER_LOG_INDEX);
                        indexRequest.id(sysOperationLog.getId().toString());

                        Map<String, Object> sources = new ConcurrentHashMap<>();
                        sources.put("username", sysOperationLog.getUsername());
                        sources.put("type", sysOperationLog.getType());
                        sources.put("uri", sysOperationLog.getUri());
                        sources.put("time", sysOperationLog.getTime());
                        sources.put("ip", sysOperationLog.getIp());
                        sources.put("address",sysOperationLog.getAddress());
                        sources.put("browser", sysOperationLog.getBrowser());
                        sources.put("os", sysOperationLog.getOs());
                        sources.put("operTime", sysOperationLog.getOperTime());
                        sources.put("delFlag", sysOperationLog.getDelFlag());

                        indexRequest.source(sources);

                        bulkRequest.add(indexRequest);
                    }

                    restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);

                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    //计数器 -1
                    countDownLatch.countDown();
                }
            });
        }
        //阻塞线程，只有当计数器=0时才会解除阻塞。防止主线程执行完了，子线程还没有执行完就终止了该程序的运行。
        countDownLatch.await();
        //终止时间
        long end = System.currentTimeMillis();

        System.out.println((end-start)+"ms");
    }

}
