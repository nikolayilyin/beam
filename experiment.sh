#!/bin/bash

MAXRAM="30g"

run_base(){
  ./gradlew :run --stacktrace -PappArgs="['--config', 'test/input/sf-light/test-base.conf']" -PmaxRAM=$MAXRAM
}

run_evcav(){
  ./gradlew :run --stacktrace -PappArgs="['--config', 'test/input/sf-light/test-EV-CAV.conf']" -PmaxRAM=$MAXRAM
}

run_base
run_evcav

run_base
run_evcav

#run_base
#run_evcav

#run_base
#run_evcav
