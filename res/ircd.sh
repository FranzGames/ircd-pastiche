#!/bin/bash

if [ -z PASTICHE_IRCD_HOME ]; then
   export PASTICHE_IRCD_HOME=`dirname $0`
fi

java -cp $PASTICHE_IRCD_HOME/lib/ircd.jar:$PASTICHE_IRCD_HOME/conf org.pastiche.ircd.Ircd
