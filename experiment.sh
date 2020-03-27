#!/bin/bash

run_base(){
  ./gradlew :run -PappArgs="['--config', 'test/input/sf-light/test-base.conf']" -PmaxRAM=16g
}

run_evcav(){
  ./gradlew :run -PappArgs="['--config', 'test/input/sf-light/test-EV-CAV.conf']" -PmaxRAM=16g
}

run_base
run_evcav

run_base
run_evcav

run_base
run_evcav

run_base
run_evcav
