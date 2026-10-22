# API test objects

The portable scripts under `Scripts/` call the local test API with `URL.openConnection()`, so they do not depend on imported Object Repository objects. If you prefer Katalon's Web Service object workflow, create request objects for:
- `POST http://127.0.0.1:5000/messages`
- `GET http://127.0.0.1:5000/messages/msg-1001`
