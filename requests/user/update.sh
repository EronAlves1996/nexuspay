BODY=$(cat <<JSON
    {
      "name": "José Aldo Teste Mudado",
      "email": "teste@teste.com"
    }
JSON
)
ID=3c380b1a-083e-4311-b0eb-fa4504647399 

curl --data "$BODY" \
    -H 'Content-type: application/json' \
    -v \
    -X PUT \
    "http://localhost:8080/users/$ID"

