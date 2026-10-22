import groovy.json.JsonOutput
import groovy.json.JsonSlurper

String baseUrl = (GlobalVariable.baseUrl ?: 'http://127.0.0.1:5000').toString()
String body = JsonOutput.toJson([
    id: 'msg-1001',
    text: 'Hello from Katalon',
    status: 'queued'
])

def connection = new URL("${baseUrl}/messages").openConnection()
connection.setRequestMethod('POST')
connection.setDoOutput(true)
connection.setRequestProperty('Content-Type', 'application/json')
connection.setRequestProperty('Accept', 'application/json')
connection.outputStream.withWriter('UTF-8') { it << body }

int statusCode = connection.responseCode
String responseText = (statusCode >= 400 ? connection.errorStream : connection.inputStream).getText('UTF-8')
assert statusCode == 201 : "Expected HTTP 201, got ${statusCode}. Body: ${responseText}"

def response = new JsonSlurper().parseText(responseText)
assert response.id == 'msg-1001'
assert response.text == 'Hello from Katalon'
assert response.status == 'queued'
println "PASS: REST producer created message ${response.id}"
