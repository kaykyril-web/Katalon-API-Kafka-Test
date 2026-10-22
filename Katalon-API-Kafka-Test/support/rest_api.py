"""Tiny local REST API for the Katalon producer/consumer exercise."""
from http.server import BaseHTTPRequestHandler, HTTPServer
import json
from urllib.parse import urlparse

MESSAGES = {}

class Handler(BaseHTTPRequestHandler):
    def _send(self, status, body):
        raw = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)

    def do_GET(self):
        path = urlparse(self.path).path
        if path == "/health":
            return self._send(200, {"status": "ok"})
        if path.startswith("/messages/"):
            message_id = path.rsplit("/", 1)[-1]
            message = MESSAGES.get(message_id)
            if message is None:
                return self._send(404, {"error": "message_not_found", "id": message_id})
            return self._send(200, message)
        return self._send(404, {"error": "not_found"})

    def do_POST(self):
        if urlparse(self.path).path != "/messages":
            return self._send(404, {"error": "not_found"})
        try:
            length = int(self.headers.get("Content-Length", "0"))
            payload = json.loads(self.rfile.read(length).decode("utf-8"))
        except (ValueError, json.JSONDecodeError):
            return self._send(400, {"error": "invalid_json"})
        required = ("id", "text", "status")
        if not isinstance(payload, dict) or any(not payload.get(k) for k in required):
            return self._send(400, {"error": "missing_required_fields", "required": list(required)})
        if payload["id"] in MESSAGES:
            return self._send(409, {"error": "duplicate_id", "id": payload["id"]})
        MESSAGES[payload["id"]] = payload
        return self._send(201, payload)

    def log_message(self, fmt, *args):
        print("[REST API]", fmt % args)

if __name__ == "__main__":
    print("Local REST API listening on http://127.0.0.1:5000")
    HTTPServer(("127.0.0.1", 5000), Handler).serve_forever()
