#!/bin/bash

# path to the git.properties file
GIT_PROPERTIES_FILE=./src/main/resources/git.properties

# Create the git.properties file
cat <<EOL > $GIT_PROPERTIES_FILE
git.commit.id=${GIT_COMMIT_ID}
EOL

echo "git.properties file generated:"
cat $GIT_PROPERTIES_FILE