#!/bin/bash

set -e

cd ../target

buildVersion=`ls cheeta-*.zip|sed -e 's/cheeta-\(.*\).zip/\1/'`

rm -rf helm-chart/cheeta/*
rm -rf helm-chart/*.tgz
mkdir -p helm-chart/cheeta
cp -r ../helm/* helm-chart/cheeta
rm helm-chart/cheeta/prepare.sh

if [[ "$OSTYPE" == "darwin"* ]]; then
	find helm-chart -name "*.yaml" | xargs sed -i '' "s/\${buildVersion}/${buildVersion}/g"
else
	find helm-chart -name "*.yaml" | xargs sed -i -e "s/\${buildVersion}/${buildVersion}/g"
fi

cd helm-chart
tar zcvf cheeta-${buildVersion}.tgz cheeta
