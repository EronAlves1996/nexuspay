BODY=$(cat <<JSON
    {
      "name": "Carteira teste",
      "userId": "bbc25793-c3d2-4783-8147-6ff4d5a0b5b7"
    }
JSON
)

curl --data "$BODY" \
    -H 'Content-type: application/json' \
    -v \
    http://localhost:8080/wallets

