import groovy.json.JsonSlurper

String baseUrl = (GlobalVariable.baseUrl ?: 'http://127.0.0.1:5000').toString()
def connection = new URL("${baseUrl}/messages/msg-1001").openConnection()
connection.setRequestMethod('GET')
connection.setRequestProperty('Accept', 'application/json')

int statusCode = connection.responseCode
String responseText = (statusCode >= 400 ? connection.errorStream : connection.inputStream).getText('UTF-8')
assert statusCode == 200 : "Expected HTTP 200, got ${statusCode}. Body: ${responseText}"

def response = new JsonSlurper().parseText(responseText)
assert response.id == 'msg-1001'
assert response.text == 'Hello from Katalon'
assert response.status == 'queued'
println "PASS: REST consumer retrieved and validated ${response.id}"
