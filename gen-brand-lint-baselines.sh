#!/usr/bin/env bash
rm app/lint-baseline.xml

./gradlew lintBru_brandBruDevServerDebug --no-daemon --refresh-dependencies
mv app/lint-baseline.xml app/lint-baseline-bru-dev.xml

./gradlew lintEbb_brandEbbDevServerDebug --no-daemon --refresh-dependencies
mv app/lint-baseline.xml app/lint-baseline-ebb-dev.xml

./gradlew lintNnyb_brandNnybDevServerDebug --no-daemon --refresh-dependencies
mv app/lint-baseline.xml app/lint-baseline-nnyb-dev.xml
