#!/bin/bash

if [ -z $PASTICHE_IRCD_HOME ]; then
   export PASTICHE_IRCD_HOME=`dirname $0`/..
fi

if [ -z $JAVA_HOME ]; then
   java -cp $PASTICHE_IRCD_HOME/lib/ircd.jar org.pastiche.ircd.Ircd $PASTICHE_IRCD_HOME/conf
else
   $JAVA_HOME/bin/java -cp $PASTICHE_IRCD_HOME/lib/ircd.jar org.pastiche.ircd.Ircd $PASTICHE_IRCD_HOME/conf
fi

