BODY=$(cat <<JSON
    {
      "name": "José Aldo Teste",
      "email": "teste@teste.com.br"
    }
JSON
)

curl --data "$BODY" \
    -H 'Content-type: application/json' \
    -v \
    http://localhost:8080/users

