#!/bin/bash
PACKAGE=at.tugraz.ist.catroid
ACTIVITY=app_1

#Uninstall old app
echo "Uninstalling App"
adb uninstall $PACKAGE.$ACTIVITY

#Delete catroid dir on sdcard
echo "Removing cartroid dir on sdcard"
adb shell rm -r /sdcard/catroid > /dev/null

if [ $# -eq 1 ]; then
  echo "Installing specified app"
  adb install $1
fi

