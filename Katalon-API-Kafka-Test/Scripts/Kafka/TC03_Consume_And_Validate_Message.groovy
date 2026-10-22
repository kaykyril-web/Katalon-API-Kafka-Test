import groovy.json.JsonSlurper
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties
import java.time.Instant

String bootstrap = (GlobalVariable.kafkaBootstrapServers ?: 'localhost:9092').toString()
String topic = (GlobalVariable.kafkaTopic ?: 'messages').toString()
String groupId = "katalon-technical-test-${System.currentTimeMillis()}"

Properties props = new Properties()
props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap)
props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.name)
props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.name)
props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, 'false')

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
try {
    consumer.subscribe([topic])
    def deadline = Instant.now().plusSeconds(30)
    def matched = null

    while (Instant.now().isBefore(deadline) && matched == null) {
        def records = consumer.poll(Duration.ofMillis(500))
        for (def record : records) {
            try {
                def payload = new JsonSlurper().parseText(record.value())
                if (payload.messageId == 'kafka-msg-001') {
                    matched = payload
                    break
                }
            } catch (Exception ignored) {
                println "Skipping non-JSON Kafka record at offset ${record.offset()}"
            }
        }
    }

    assert matched != null : "Timed out waiting for kafka-msg-001 on topic ${topic}"
    assert matched.eventType == 'MESSAGE_CREATED'
    assert matched.payload.text == 'Hello from Kafka'
    assert matched.payload.status == 'queued'
    println "PASS: Kafka consumer received and validated ${matched.messageId}"
} finally {
    consumer.close()
}
