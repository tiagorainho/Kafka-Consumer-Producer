# KafkaUseCases

## How to use

>``UseCaseID`` is a **natural number** representing the Use Cases in the ``src/UCs`` folder.

First, start both the zookeper and the server(s)
```bash
./run <UseCaseID>
```

Then run the java Use cases.

With little modifications, while using vscode, changing only the file paths the following files and running:
``` bash
./consumer.sh
./producer.sh
./source.sh
```
would do the trick.

Otherwise run the main function on the 3 process classes found in ``src/UCs/UC<UseCaseID>``.