# Portable Groovy scripts

These `.groovy` files contain the executable source for the test cases. In Katalon Studio, create/open the corresponding Test Case and paste each script into Script view if your Katalon release does not automatically bind the provided `.tc` metadata to a script.

The scripts use `GlobalVariable.baseUrl`, `GlobalVariable.kafkaBootstrapServers`, and `GlobalVariable.kafkaTopic` when available, with localhost defaults. Add those variables in Profiles > default if your Katalon version requires them.
