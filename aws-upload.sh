#!/usr/bin/env bash
aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket aqe-serverless-asset-overview
aws cloudformation deploy --template-file output-sam.yaml --stack-name ServerlessAssetOverviewApi --capabilities CAPABILITY_IAM
