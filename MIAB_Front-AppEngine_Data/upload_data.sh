#!/bin/sh
python.exe appcfg.py upload_data --config_file bulkloader.yaml --url=http://localhost:8888/remote_api --filename $1 --kind=$2 -e nobody@nowhere.com