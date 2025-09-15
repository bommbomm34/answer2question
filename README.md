# answer2question

## Description

A Kotlin project that converts answers into corresponding questions using NLP techniques. Currently, only English is supported. German support will follow.

> [!WARNING]
> This project is **not recommended for production use.** This project is highly experimental and can produce wrong content.

## Requirements

- Gradle
- Java (version 21 or higher)

## Build & Usage

1. Sync the project
2. Run the project with

```shell
./gradlew run
```

This command will run the evaluation test. If you want a custom text to be converted,
you need to pass some arguments to the application in the following way:

```shell
./gradlew run --args="en text.txt"
```

where ```text.txt``` is the file where you have the text. Currently, the first argument (the langauge) is ignored because only English is supported.

> [!NOTE]
> Ensure your environment meets the Java and Gradle version requirements.
> The given text must consist of grammatically and correctly spelled sentences.

## Licensing

This project is licensed under [GNU GPLv3](LICENSE.txt).  
It uses Stanford CoreNLP for NLP tasks, which is also licensed under GPLv3.  
Stanford CoreNLP's default models are also used in the project and are synced via Gradle. The licenses of the models may vary depending on the model. For production use, it is **highly recommended** to use your own models to avoid licensing problems.
