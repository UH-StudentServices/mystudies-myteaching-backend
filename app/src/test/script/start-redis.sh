#!/bin/bash

docker run --rm -d -p 6379:6379 --name opintoni_redis redis:3.2-alpine
