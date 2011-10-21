#!/bin/bash
#$HeadURL: svn+ssh://rbezema@svn.wald.intevation.org/deegree/base/trunk/scripts/shell/create_eclipse_userlibrary.sh $
#*----------------    FILE HEADER  ------------------------------------------
#
# This file is part of deegree.
# Copyright (C) 2001-2006 by:
# EXSE, Department of Geography, University of Bonn
# http://www.giub.uni-bonn.de/deegree/
# lat/lon GmbH
# http://www.lat-lon.de
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
# Contact:
#
# Andreas Poth
# lat/lon GmbH
# Aennchenstraße 19
# 53177 Bonn
# Germany
# E-Mail: poth@lat-lon.de
#
# Prof. Dr. Klaus Greve
# Department of Geography
# University of Bonn
# Meckenheimer Allee 166
# 53115 Bonn
# Germany
# E-Mail: greve@giub.uni-bonn.de
# 
# ---------------------------------------------------------------------------

function outputFile (){
 for check in $*
 do
   KEY=`head -4 "$check"  | grep -i targetNamespace | cut -d\" -f2`
   echo "    <uri name='$KEY' uri='file:$check'/>" >> $OUTPUT_FILE;
done
}

function outputHelp (){
 echo create_schema_cache.sh can help to create an eclipse cache for your local schema definitions.
 echo usage\:
 echo create_schema_cache.sh schema_dir [output_file];
 echo - if no outputfile is given schema_dir/.xmlcatalog is used
}

if( test -z $1 -o $1 = 'h' ) then
  echo "No schema directory was provided!"
  outputHelp;
  exit 1;
fi

if( test ${1:0:1} != / ) then
  dir=`pwd`/$1
else
  dir=$1
fi

if( test -z $2 ) then
  OUTPUT_FILE=$1/.xmlcatalog
else
  OUTPUT_FILE=$2
fi



if( ! test -d $1 ) then

    echo $1 is not a directory.
    
else
 
    echo "writing file to: " `pwd`"/"$OUTPUT_FILE;
    echo '<?xml version="1.0" encoding="UTF-8"?>' > $OUTPUT_FILE
    echo '<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog">' >> $OUTPUT_FILE
    outputFile `find $dir -iname \*xsd| /usr/bin/sort` 
    echo '</catalog>' >> $OUTPUT_FILE

fi
