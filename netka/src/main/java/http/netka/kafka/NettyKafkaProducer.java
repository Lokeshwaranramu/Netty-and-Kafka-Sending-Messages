package http.netka.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.apache.zookeeper.ZooKeeper;

import kafka.cluster.Broker;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class NettyKafkaProducer
{
    public static void main(String[] args) throws Exception
    {
    	 ZooKeeper zk = new ZooKeeper("localhost:2181", 10000, null);
    	    List<String> brokerList = new ArrayList<String>();

    	    List<String> ids = zk.getChildren("/brokers/ids", false);
    	    for (String id : ids) {
    	        String brokerInfoString = new String(zk.getData("/brokers/ids/" + id, false, null));
    	        Broker broker = Broker.createBroker(Integer.valueOf(id), brokerInfoString);
    	        if (broker != null) {
    	            brokerList.add(broker.getConnectionString());
    	        }
    	    }
    	Properties props=new Properties();
    	props.put("metadata.broker.list", String.join(",", brokerList)); 
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        System.out.println("Broker Initialized");
        ProducerConfig config=new ProducerConfig(props);
        Producer<String, String> kafkaProducer=new Producer<String, String>(config);
        @SuppressWarnings("resource")
		Scanner z = new Scanner(System.in);
        System.out.println("Enter Message to send(Type 'exit' to stop sending message): ");
        String D= null;
        D=z.nextLine();
        while(!D.equals("exit")) {
        	KeyedMessage<String, String> data=new KeyedMessage<String, String>("test-topic", D);
        	kafkaProducer.send(data);
        	D=z.nextLine();
        }
        System.out.println("Data sending completed!");
       kafkaProducer.close();
    }
}