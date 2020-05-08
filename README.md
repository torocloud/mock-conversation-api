# Mock Conversation API
A simple conversation api

### Prerequisites

  - Apache Maven 3+
  - [Martini Desktop](https://www.torocloud.com/martini/download)

### Building the Martini Package

```
$ mvn clean package
```
This will create a ZIP file named `mock-conversation-api-bin.zip` containing all the files (services, configurations, etc.) needed under the `target` folder. This ZIP file is what we call a [Martini Package](https://docs.torocloud.com/martini/latest/developing/package/) which then you can import in Martini Desktop to get started. You can learn more how to import a Martini Package by visiting our [documentation](https://docs.torocloud.com/martini/latest/developing/package/importing/)

### Usage
This package exposes operations for a simple conversation REST API. You can find the [Gloop REST API](https://docs.torocloud.com/martini/latest/developing/gloop/api/rest/) file at `com/torocloud/mock/conversation/dm/DirectMessage` under the `code` folder after importing the package to your Martini Desktop application.

### Setup
This Martini Package automatically sets up the required resources for you. During the package startup, Martini will execute the configured _Startup Service_ that initializes the database to be used for storing the record that will be creating for using this mock api.

This package uses HSQL as the default datastore but you can change this to MySQL by updating the [package properties](https://docs.torocloud.com/martini/latest/developing/package/properties/) located at [conf](https://docs.torocloud.com/martini/latest/developing/package/directory/) folder. Below is what the contents of the properties file look like

```
# Flag used for testing
debug.enabled=true

# The database connection name to be used
database.name=mock_conversation_api

# The database type to be used
database.type=hsql

# HSQL database configuration to be used for testing environment
hsql.driver=org.hsqldb.jdbc.JDBCDriver
hsql.connection.url=jdbc:hsqldb:file:${toroesb.home}data/hsql/mock_conversation_api.db;hsqldb.tx=MVCC;sql.syntax_mys=true
hsql.username=sa
hsql.password=

# MySQL database configuration to be used in production environment
mysql.driver=com.mysql.cj.jdbc.Driver
mysql.connection.url=jdbc:mysql://<host>/<database-to-use>
mysql.username=root
mysql.password=
```
* `debug.enabled` when set to `true` initializes the database with dummy data when the package starts. The initialized data are also removed when the package stops or is unloaded. Set the value of this property to `false` if you want to keep your data when doing a restart. You can also set this property to `true` when the package starts and then set to `false` afterwards so that you'll have the data initialized on startup, and keep the data when the package or the Martini instance do a restart
* `database.type` sets the database provider the Martini package will use. If you will use the default hsql config, you don't need to change anything in the hsql configuration. **Note**: If you will use a different hsql database, make sure that you add `sql.syntax_mys=true` in the connection properties. This ensures that the SQL query from the SQL Services in this package will be compatible with hsql.

### Operations

The base url is `<host>/api/conversations/dm` where `host` is the location where Martini is deployed. By default, it's `localhost:8080`

`POST /register`

Registers a participant

**Sample Request**

**curl**

```bash
curl -X POST \
  http://localhost:8080/api/conversations/dm/register \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
	    "name": "Sample User 1"
	}'
```

**Sample Response**

If the request is successful, it will return an HTTP status code of `200` with the response payload below.

```json
{
    "id": "3a936ae6-a15a-44bb-94f3-cdb6ea6515d1",
    "name": "Sample User 1"
}
```

`POST /to/{to}`

Sends a message to the given participant `to`

**Headers**

`X_USERNAME` - The participant's username

**Sample Request**

**curl**

```bash
curl -X POST \
  http://localhost:8080/api/conversations/dm/to/Sample%20User%202 \
  -H 'Content-Type: text/plain' \
  -H 'X_USERNAME: Sample User 1' \
  -d 'Sample text'
```

**Sample Response**

If the request is successful, it will return an HTTP status code of `204` with no response payload.

`GET /list`

Returns a list of threads/conversations of the given user that are not archived.

**Headers**

`X_USERNAME` - The participant's username

**Sample Request**

**curl**

```bash
curl -X GET \
  http://localhost:8080/api/conversations/dm/list \
  -H 'accept: application/json' \
  -H 'X_USERNAME: Sample User 1'
```

**Sample Response**

If the request is successful, it will return an HTTP status code of `200` with the response payload below.

```json
[
    {
        "id": "7fb1d890-73c8-4f87-b13c-df1b531d3b62",
        "participants": [
            {
                "id": "3922c746-1b71-4637-a388-deb50ce90ff8",
                "name": "Sample User 2"
            }
        ]
    }
]
```

`GET /to/{to}`

Returns a list of messages between the two given participants.

**Headers**

`X_USERNAME` - The participant's username

**Query Parameters**

`offset` - The number of messages to skip

**Sample Request**

**curl**

```bash
curl -X GET \
  http://localhost:8080/api/conversations/dm/to/Sample%20User%202?offset=0 \
  -H 'accept: application/json' \
  -H 'X_USERNAME: Sample User 1'
```

**Sample Response**

If the request is successful, it will return an HTTP status code of `200` with the response payload below.

```json
[
    {
        "id": "5c0459c5-d038-4b14-95a9-c80c0b4c5c47",
        "createdAt": "2020-05-06T16:28:03+0800",
        "body": "Sample text",
        "participantId": "3a936ae6-a15a-44bb-94f3-cdb6ea6515d1",
        "threadId": "7fb1d890-73c8-4f87-b13c-df1b531d3b62"
    }
]
```

`PUT /to/{to}/archive`

Archives a conversation between two participants

**Headers**

`X_USERNAME` - The participant's username

**Sample Request**

**curl**

```bash
curl -X PUT \
	http://localhost:8080/api/conversations/dm/to/Sample%20User%202/archive \
	-H 'Accept: application/json' \
	-H 'X_USERNAME: Sample User 1'
```

**Sample Response**

If the request is successful, it will return an HTTP status code of `202` with the response payload below.

```json
{
    "result": "OK",
    "message": "The conversation '7fb1d890-73c8-4f87-b13c-df1b531d3b62' was archived for 'Sample User 1'",
    "warnings": []
}
```