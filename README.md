# ART Gradle
ART Gradle is a Gradle plugin for configuring and using ART projects.

## Badges
[![ART Gradle Main](https://github.com/art-community/art-gradle/actions/workflows/push-main.yml/badge.svg)](https://github.com/art-community/art-gradle/actions/workflows/push-main.yml)
![GitHub repo size](https://img.shields.io/github/repo-size/art-community/art-gradle)

## Build and Binaries
Releases are available via [ART Packages](https://repsy.io/mvn/antonsh/art-packages/).

## Requirements
- Gradle 7+

## Bugs and Feedback
For bugs, questions and discussions please use the [Github Issues](https://github.com/art-community/art-gradle/issues).

Join us on Telegram: https://t.me/art_github

## Using

### JVM-based project

gradle.properties:
```
artPluginVersion=main
```

settings.gradle.kts:
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repsy.io/mvn/antonsh/art-packages/") }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.contains("art")) {
                useModule("io.art.gradle:art-gradle:main")
            }
        }
    }
    plugins {
        val artPluginVersion: String by settings
        id("art-jvm") version artPluginVersion
    }
}
```

build.gradle.kts:
```
plugins {
    `java-library`
    id("art-jvm")
}

art {
    // Configure here
}
```

## LICENSE
ART

Copyright 2020-2021 ART

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
