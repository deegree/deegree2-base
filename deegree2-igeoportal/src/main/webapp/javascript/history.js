//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
----------------------------------------------------------------------------*/

function History( controller, maxSteps ) {

	// variable declaration
	this.controller = controller;
	this.maxSteps = 0;		 // TODO: implement maxSteps

	this.message1 = "It is not possible to move to the previous map, because you are at the initial map."
	this.message2 = "It is not possible to move to the next map, because you are at the final map."

	this.historyList = null; // contains the map envelopes
	this.previous = null;	 // points to the envelope that is one step back.
	this.current = null;	 // points to the current envelope as seen in map.
	this.next = null;		 // points to the envelope that is one step beyond.

	// method declaration
	this.init = init;
	this.addEnvelope = addEnvelope;
	this.moveBack = moveBack;
	this.moveForward = moveForward;

	// method implementation

	/*
	 * this method is called when the portal is loaded, in Controller.init(), in viewcontext.xml
	 */
	function init( initialEnvelope ) {
		this.previous = -1;
		this.current = 0;
		this.next = 1;
		this.historyList = new Array();
		this.historyList[this.current] = initialEnvelope;
	}

	/*
	 * this method is called when following events occur:
	 *
	 * PAN (use hand icon to move map)
	 * BOX (mode = zoomin, zoomout, recenter)
	 * BBOX (use mouse to open new bbox)
	 * MOVE (use arrows to navigate)
	 */
	function addEnvelope( envelope ) {
		var tempList = new Array();
		for ( var i = 0; i <= this.next; i++ ) {
			//copy historylist up to current position
			tempList[i] = this.historyList[i];
		}
		//overwrite history list with temp list, thus deleting forward branch
		//this.historyList = tempList;
		//add new entry to history list
		this.historyList[this.next] = envelope;
		// increase pointers
		this.next++;
		this.current++;
		this.previous++;
	}

	function moveBack() {
		var env = null;
		if ( this.previous == -1 ) {
			// do nothing
			alert( this.message1 );
		} else	{
			// move back
			env = this.historyList[this.previous];
			// decrease pointer
			this.next -= 2;
			this.current -= 2;
			this.previous -= 2;
			controller.actionPerformed( new Event( null, "BBOX", env ) );
		}
	}

	function moveForward() {
		var env = null;
		if ( this.next >= this.historyList.length ) {
			// do nothing
			alert( this.message2 );
		} else {
			// move forward
			env = this.historyList[this.next];			
			// increase pointer
			//this.next++;
			//this.current++;
			//this.previous++;
			controller.actionPerformed( new Event( null, "BBOX", env ) );
		}
	}
}
