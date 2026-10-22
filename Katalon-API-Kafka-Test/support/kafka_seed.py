"""Publishes a deterministic test message; Katalon is the consumer under test."""
import json
from kafka import KafkaProducer
from kafka.admin import KafkaAdminClient, NewTopic
from kafka.errors import TopicAlreadyExistsError

BOOTSTRAP = "localhost:9092"
TOPIC = "messages"

admin = KafkaAdminClient(bootstrap_servers=BOOTSTRAP, client_id="katalon-test-setup")
try:
    existing = admin.list_topics()
    if TOPIC not in existing:
        try:
            admin.create_topics([NewTopic(name=TOPIC, num_partitions=1, replication_factor=1)])
        except TopicAlreadyExistsError:
            pass
finally:
    admin.close()

producer = KafkaProducer(
    bootstrap_servers=BOOTSTRAP,
    value_serializer=lambda value: json.dumps(value).encode("utf-8"),
    key_serializer=lambda key: key.encode("utf-8"),
)
record = {
    "messageId": "kafka-msg-001",
    "eventType": "MESSAGE_CREATED",
    "payload": {"text": "Hello from Kafka", "status": "queued"},
}
producer.send(TOPIC, key=record["messageId"], value=record).get(timeout=15)
producer.flush()
producer.close()
print(f"Published seed message to topic={TOPIC}: {json.dumps(record)}")
