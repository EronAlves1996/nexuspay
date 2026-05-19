BODY=$(cat <<JSON
    {
      "name": "Carteira teste 3",
      "userId": "bbc25793-c3d2-4783-8147-6ff4d5a0b5b7",
      "minLimit": -5
    }
JSON
)

curl --data "$BODY" \
    -H 'Content-type: application/json' \
    -v \
    -X PUT \
    http://localhost:8080/wallets/9cac3700-9bb4-4508-86ee-fc91148a5aaf

