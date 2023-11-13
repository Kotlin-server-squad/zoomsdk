#!/bin/bash

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
LIB_DIR="$SCRIPT_DIR/../lib"
SPEC_DIR="$SCRIPT_DIR/../spec"
BUILD_DIR="$SCRIPT_DIR/../build"

"$LIB_DIR"/swagger-codegen \
generate \
-i "$SPEC_DIR"/zoom-api-spec.json \
-l kotlin-client \
-o "$BUILD_DIR"/generated/src/main/kotlin

exit 0
