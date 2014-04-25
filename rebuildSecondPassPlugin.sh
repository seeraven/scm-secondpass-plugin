#!/bin/bash
#
# Rebuild the scm-secondpass-plugin
#
# (c) 2014 by Clemens Rabe <clemens.rabe@gmail.com>
#

# Ensure we are in the right directory
if [ ! -e pom.xml ]; then
    echo "Please execute this script from the scm-secondpass-plugin source directory!"
    exit 1
fi

mvn scmp:install -DscmHome=../scm-secondpass-plugin-dist
mvn scmp:package

cd ..
rm -rf dist/scm-secondpass-plugin

mkdir -p dist/scm-secondpass-plugin
mv scm-secondpass-plugin-dist/plugins/*                 dist/scm-secondpass-plugin

mkdir -p dist/scmp
cp scm-secondpass-plugin/target/*.scmp                  dist/scmp

echo
echo "Finished building second pass plugin."
echo
echo "The new created elements are located in the directory $PWD/dist:"
echo " $PWD/dist/scm-secondpass-plugin : The scm-secondpass-plugin plugin."
echo " $PWD/dist/scmp                  : The plugins as SCMP files."
echo
