BODY=$(cat <<JSON
    {
      "name": "José Aldo",
      "email": "teste@teste.com"
    }
JSON
)

curl --data "$BODY" \
    -H 'Content-type: application/json' \
    -v \
    http://localhost:8080/users

