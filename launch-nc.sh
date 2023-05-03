#!/bin/bash

socat TCP4-LISTEN:10001,reuseaddr,fork EXEC:"stdbuf -oL ./run.sh" 

#connect with netcat `nc`, `ncat`, etc