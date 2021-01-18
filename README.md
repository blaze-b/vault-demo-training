# Spring Vault Demo Training

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.7.RELEASE/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.7.RELEASE/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.4.1/reference/htmlsingle/#using-boot-devtools)

### Install jq.exe file for JSON printing

## Vault Dev Environment Setup in Local

- Download the file from the following website, [Click Here](https://www.vaultproject.io/downloads)
- Set the path in the environment variables
- Command to check whether the installation was correct `vault`, `vault version`
- CMD to start the server `vault server -dev`, Note : do not run the web server in production
- After starting the server,
    - Open a new terminal
    - Run the export command `set VAULT_ADDR=http://127.0.0.1:8200`
    - Save the unseal key `echo "Nd6DVwrEPyA3Jpy6yUxoRC8II6XCQs1Le+EDLAZ++T0=" > unseal.key` 
    - Save the root token `set VAULT_DEV_ROOT_TOKEN_DEV=s.yeo5TWjhY6QjpIvg9oGpjhay`
    - Verify the status of the server by running the command `vault status`

### CRUD operations

- Writing a secret in vault `vault kv put secret/hello foo=world`, 
  `<vault kv put <path> <key>=<value>>` 
  Eg: `vault kv put secret/hello foo=world excited=yes`
- Get the key details command `vault kv get <path>`, `vault kv get secret/hello`
- Get based on a particular field `vault kv get -field=excited secret/hello`
- Get output in the JSON format `vault kv get -format=json secret/hello`
- Deleting the secret `vault kv delete secret/hello`


### Secret Engines

- vault kv put foo/bar a=b -> error will occur
- List command -> `vault secrets list`
- Create and enable a secret `kv`, `kv-v2`> `vault secrets enable -path=kv kv`, `vault secrets enable kv`, `vault secrets list`
- CMD for crud `vault kv put kv/hello target=world`, `vault kv get kv/hello`, `vault kv put kv/my-secret value="s3c(eT"`, `vault kv get -format=json kv/my-secret`, `vault kv delete kv/my-secret`
- List the paths `vault kv list kv/`, `vault kv list kv/`
- Disable a  secret engine `vault secrets disable kv/`

### Dynamic Secrets(AWS)

- Enable the AWS secrets engine `vault secrets enable -path=aws aws`
- `set AWS_ACCESS_KEY_ID=<aws_access_key_id>`
- `set AWS_SECRET_ACCESS_KEY=<aws_secret_key>`
- Write cmd: `vault write aws/config/root \
	    access_key=$AWS_ACCESS_KEY_ID \
	    secret_key=$AWS_SECRET_ACCESS_KEY \
	    region=us-east-1`
- Create a role: `vault write aws/roles/my-role \
	        credential_type=iam_user \
	        policy_document=-<<EOF
	{
	  "Version": "2012-10-17",
	  "Statement": [
	    {
	      "Sid": "Stmt1426528957000",
	      "Effect": "Allow",
	      "Action": [
	        "ec2:*"
	      ],
	      "Resource": [
	        "*"
	      ]
	    }
	  ]
	}
	EOF`
- `vault read aws/creds/my-role`
- `vault lease revoke aws/creds/my-role/0bce0782-32aa-25ec-f61d-c026ff22106`

### Built-In Help

- `vault secrets enable -path=aws aws`
- `vault path-help aws`
- `vault path-help aws/creds/my-non-existent-role`

### Authentication

- Token/ authentication is automatically enabled, During startup of the server the token will be generated as ROOT_TOKEN
- Command to create a new token `vault token create`
- Command to login to the token: `vault login s.KwMVMnrg7u3mBFAIGdBUbkcO`
- Command to revoke the token: `vault token revoke s.iyNUhq8Ov4hIAx6snw5mB2nL`

### Git hub

- Can do the github enabling `vault auth enable github`
- Writing data - `vault write auth/github/config organization=hashicorp`
- Reading `vault read auth/github/config`,`vault auth list`
- CMD `vault write auth/github/map/teams/engineering value=default,applications`
- Reading `vault read auth/github/map/teams/engineering `
- `vault auth help github`
- `vault login -method=github`
- `vault token revoke -mode path auth/github`
- `vault auth disable github`

### Policy

- `vault policy read default`
- `vault policy list`
- Policy writing help command `vault policy write -h`
- Creating a named policy: `vault policy write my-policy - << EOF # Dev servers have version 2 of KV secrets engine mounted by default, so will # need these paths to grant permissions:
	path "secret/data/*" {
	  capabilities = ["create", "update"]
	}
	path "secret/data/foo" {
	  capabilities = ["read"]
	}
	EOF`
- To read the created policy `vault policy read my-policy`
- Testing the token
- Create a token `vault token create -policy=my-policy`
- Use the created token `VAULT_TOKEN=s.QyUoJzR8BXWDkX0n8RzAyElu vault kv put secret/creds password="my-long-password"`
- Write to the secrets `VAULT_TOKEN=s.QyUoJzR8BXWDkX0n8RzAyElu vault kv put secret/foo robot=beepboop`

### Associate policies to the auth

- `vault auth list | grep 'approle/'`
- `vault auth enable approle`
- `vault write auth/approle/role/my-role \
	    secret_id_ttl=10m \
	    token_num_uses=10 \
	    token_ttl=20m \
	    token_max_ttl=30m \
	    secret_id_num_uses=40 \
	    token_policies=my-policy`
- Write the role-id and the secret id to the env variables
  - `export ROLE_ID="$(vault read -field=role_id auth/approle/role/my-role/role-id)"`
  - `export SECRET_ID="$(vault write -f -field=secret_id auth/approle/role/my-role/secret-id)"`
  - `vault write auth/approle/login role_id="$ROLE_ID" secret_id="$SECRET_ID"`
- `vault write auth/approle/login role_id="$ROLE_ID" secret_id="$SECRET_ID"`

### Deploying the vault permanently

- Create a config.hcl file in the vault.exe path with the following content 
- Create a directory in the following path `mkdir -p vault/data`
- Start the server using the following command `vault server -config=config.hcl`
- Initialize the vault `set VAULT_ADDR=http://127.0.0.1:8200`, `vault operator init`
- Init operation will generate a Unseal Keys which should be permanently stored for login
  `{
  "keys": [
  "0c731099534cbced4b16aefb4a2961c15afdf286bb6111a9c1d4f2e53f02ab5af2",
  "60c9134ea838efe3c144f824042f002272925e01629c217068c7b16fa06e106ee0",
  "79d6c6f37558494fe7f285e678ec0dba61b02388fee0f504e9b09106cc7d5a249c",
  "789683056fe1bbbc182859d80346a69a028b0cd348e1b2d8b9b7f7267043d21460",
  "26f6ab40c109631c51b23dcce9ddd69ee1838be7117540ae6446b5db47eeb73529"
  ],
  "keys_base64": [
  "DHMQmVNMvO1LFq77SilhwVr98oa7YRGpwdTy5T8Cq1ry",
  "YMkTTqg47+PBRPgkBC8AInKSXgFinCFwaMexb6BuEG7g",
  "edbG83VYSU/n8oXmeOwNumGwI4j+4PUE6bCRBsx9WiSc",
  "eJaDBW/hu7wYKFnYA0ammgKLDNNI4bLYubf3JnBD0hRg",
  "JvarQMEJYxxRsj3M6d3WnuGDi+cRdUCuZEa120futzUp"
  ],
  "root_token": "s.6qdidDRHD9A1Su1VkWtE9LHt"
  }`
- Unseal three threshold keys `vault operator unseal` enter the valid key after that
- Login command `vault login <Initial_Root_Token>`, `vault login s.yo5p3hCZuUGRmhNNPVtaXOFG`
- Deletion and clean of data
  - `ps aux | grep "vault server" | grep -v grep | awk '{print $2}' | xargs kill`
  - `rm -r /vault/data`


### Using the HTTP API's with Authentication

- vault server -config=config.hcl
- Init - `curl http://127.0.0.1:8200/v1/sys/init -X POST -d "{\"secret_shares\": 1, \"secret_threshold\": 1}" | jq` 
- set VAULT_TOKEN=s.mWSvGgnHbImvZVmWVsNnYwuH
- Unseal the key `curl -X POST -d "{\"key\": \"MMQCBBOUwo4z8dLHxb12L/n64vykmQL0usFSGgzfS0o=\"}" http://127.0.0.1:8200/v1/sys/unseal | jq`
- Check whether it is initialized `curl http://127.0.0.1:8200/v1/sys/init`
- Now for the auth part enabling `vault auth enable -output-curl-string approle`,
	 `curl -H "X-Vault-Request: true" -H "X-Vault-Token: s.mWSvGgnHbImvZVmWVsNnYwuH" http://127.0.0.1:8200/v1/sys/auth |jq`
	 Output: `curl -X POST -H "X-Vault-Request: true" -H "X-Vault-Token:s.1gTwVAr6nz4fNJBBjMcfOdyM" -d '{"type":"approle","description":"","config":{"options":null,"default_lease_ttl":"0s","max_lease_ttl":"0s","force_no_cache":false},"local":false,"seal_wrap":false,"external_entropy_access":false,"options":null}' https://127.0.0.1:8200/v1/sys/auth`
- Policy creation in Windows `curl  -H "X-Vault-Token: $VAULT_TOKEN" -X PUT -d '{"policy":"# Dev servers have version 2 of KV secrets engine mounted by default, so will\n# need these paths to grant permissions:\npath \"secret/data/*\" {\n  capabilities = [\"create\", \"update\"]\n}\n\npath \"secret/data/foo\" {\n  capabilities = [\"read\"]\n}\n"}' http://127.0.0.1:8200/v1/sys/policies/acl/my-policy`
- Association command `curl \
	    --header "X-Vault-Token: $VAULT_TOKEN" \
	    --request POST \
	    --data '{"policies": ["my-policy"]}' \
	    http://127.0.0.1:8200/v1/auth/approle/role/my-role`
- Role id API`curl \
	    --header "X-Vault-Token: $VAULT_TOKEN" \
	     http://127.0.0.1:8200/v1/auth/approle/role/my-role/role-id | jq -r ".data"`
- Secret ID API `curl \
	    --header "X-Vault-Token: $VAULT_TOKEN" \
	    --request POST \
	    http://127.0.0.1:8200/v1/auth/approle/role/my-role/secret-id | jq -r ".data"`
- Login API`curl --request POST \
	       --data '{"role_id": "c3ec4eab-5477-c669-fca8-6a71fdf38c23", "secret_id": "fc2710e5-9536-3f4f-666d-fd5d8379b2b9"}' \
	       http://127.0.0.1:8200/v1/auth/approle/login | jq -r ".auth"`
- Output client-token and `set export VAULT_TOKEN="s.p5NB4dTlsPiUU94RA5IfbzXv"`
- Secret credentials `curl \
	    --header "X-Vault-Token: $VAULT_TOKEN" \
	    --request POST \
	    --data '{ "data": {"password": "my-long-password"} }' \
	    http://127.0.0.1:8200/v1/secret/data/creds | jq -r ".data"`

### Content for the config.hcl file

storage "raft" {
#path    = "./vault/data"
#path = "vault-data"
#path    = "./vault/data1"
path = "./vault/data2"
node_id = "node1"
}
listener "tcp" {
address     = "127.0.0.1:8200"
tls_disable = 1
}
disable_mlock = true
api_addr = "http://127.0.0.1:8200"
cluster_addr = "https://127.0.0.1:8201"
ui = true

