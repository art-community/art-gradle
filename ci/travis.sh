#!/usr/bin/env bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    echo -e "Building PR #$TRAVIS_PULL_REQUEST [$TRAVIS_PULL_REQUEST_SLUG/$TRAVIS_PULL_REQUEST_BRANCH => $TRAVIS_REPO_SLUG/$TRAVIS_BRANCH]"
    ./gradlew build
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_TAG" == "" ] && [ "$gradlePublishKey" != "" ] ; then
    echo -e "Building Snapshot $TRAVIS_REPO_SLUG/$TRAVIS_BRANCH"
    ./gradlew \
        -Pgradle.publish.key="${gradlePublishKey}" -Pgradle.publish.secret="${gradlePublishSecret}" \
        publishPlugins --stacktrace
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_TAG" != "" ] && [ "$gradlePublishKey" != "" ] ; then
    echo -e "Building Tag $TRAVIS_REPO_SLUG/$TRAVIS_TAG"
    ./gradlew \
        -Pversion="$TRAVIS_TAG" \
        -Pgradle.publish.key="${gradlePublishKey}" -Pgradle.publish.secret="${gradlePublishSecret}" \
        publishPlugins --stacktrace
else
    echo -e "Building $TRAVIS_REPO_SLUG/$TRAVIS_BRANCH"
    ./gradlew build
fi