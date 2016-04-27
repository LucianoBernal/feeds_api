#!/usr/bin/env bash
curl -XDELETE 'http://localhost:9200/crack/' && ./create_index.sh