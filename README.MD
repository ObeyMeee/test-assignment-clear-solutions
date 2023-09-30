# Test Assignment

This small project is made as test assignment for Clear Solutions
Application provide REST API for Users

## Endpoints

| HTTP Method | Endpoint                          | Query Parameters                                                                                                                                                                                                                                                                                                  | Passed body                                                                                                                                                                            | Description                              | Returned Body     |
|-------------|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|-------------------|
| GET         | `/users/:id`                      | None                                                                                                                                                                                                                                                                                                              | None                                                                                                                                                                                   | Get a single user by ID.                 | User body         |
| GET         | `/users/search/birthDate/between` | <ul><li>`from` lower bound of date in format "YYYY-mm-dd" (required)</li><li>`to` upper bound of date in format "YYY--mm-dd" (required)</li><li>`page` number of page of returning list (optional, default value is `0`)</li> <li>`size` amount of elements to return (optional, default value is `10`)</li></ul> | None                                                                                                                                                                                   | Get list of users in range of birthdate. | List of users     |
| POST        | `/users`                          | None                                                                                                                                                                                                                                                                                                              | `email`: string<br/>`firstName`: string<br/>`lastName`: string<br/>`birthDate`: YYY-mm-dd<br/>`address`: string(optional)<br/> `phoneNumber`: string(optional)                         | Create a new user.                       | Created User body |
| PUT         | `/users`                          | None                                                                                                                                                                                                                                                                                                              | `id`: string(UUID)<br/> `email`: string<br/>`firstName`: string<br/>`lastName`: string<br/>`birthDate`: YYY-mm-dd<br/>`address`: string(optional)<br/> `phoneNumber`: string(optional) | Full update a single user .              | Updated User body |
| PATCH       | `/users/:id`                      | None                                                                                                                                                                                                                                                                                                              | Key-value pair(s). For example <br/> `{"email": "newemail@mail.com"}` or <br/> `{"firstName": "Zahar", "lastName": "Berkut"}`                                                          | Partial update a user by ID.             | Updated User body |
| DELETE      | `/users/:id`                      | None                                                                                                                                                                                                                                                                                                              | None                                                                                                                                                                                   | Delete a single user by ID.              | None              |

Returning User body example

```json
{
  "data": {
        "id": "23abccda-a56a-49c3-970e-4d73c3dff6c9",
        "email": "stepan@unesco.org",
        "firstName": "Stepan",
        "lastName": "Petrenko",
        "birthDate": "1985-10-23",
        "address": "9296 Dakota Avenue",
        "phoneNumber": "+380675963345"
    }
}
```

## 🚀 Run Application

1. Firstly, you need to create database
2. Open run.sh script
3. Change environment variables(DB_URL, DB_USER, DB_PASSWORD)
4. Run script in bash console ```$ sh run.sh```
5. Your server will start on `http://localhost:8080`. Move to any endpoint to test it out!