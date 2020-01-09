#!/bin/bash

set -ex

export PATH=$PATH:/var/www/liquibase

source ~/.profile
source ~/.bashrc

liquibase --defaultsFile=src/main/resources/liquibase.properties --driverPropertiesFile=src/main/resources/connection.properties $@