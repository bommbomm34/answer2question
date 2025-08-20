# answer2question

## Description

A Kotlin project that converts answers into corresponding questions using NLP techniques. Currently, only English is supported. German support will follow.

> Warning: This project is **not recommended for production use.** This project is highly experimental and can produce wrong content.

## Requirements

- Gradle  
- Java (version X or higher)  

## Build & Usage

1. Open `Config.kt` and fill out the `text` variable with your input.  
2. Sync the project and run it as a standard Gradle project.  

> Note: Ensure your environment meets the Java and Gradle version requirements.

## Licensing

This project is licensed under [GNU GPLv3](LICENSE.txt).  
It uses Stanford CoreNLP for NLP tasks, which is also licensed under GPLv3.  
Stanford CoreNLP's default models are also used in the project and are synced via Gradle. The licenses of the models may vary depending on the model. For production use, it is **highly recommended** to use your own models to avoid licensing problems.


