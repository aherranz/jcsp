//////////////////////////////////////////////////////////////////////
//                                                                  //
//  JCSP ("CSP for Java") Libraries                                 //
//  Copyright (C) 1996-2008 Peter Welch and Paul Austin.            //
//                2001-2004 Quickstone Technologies Limited.        //
//                                                                  //
//  This library is free software; you can redistribute it and/or   //
//  modify it under the terms of the GNU Lesser General Public      //
//  License as published by the Free Software Foundation; either    //
//  version 2.1 of the License, or (at your option) any later       //
//  version.                                                        //
//                                                                  //
//  This library is distributed in the hope that it will be         //
//  useful, but WITHOUT ANY WARRANTY; without even the implied      //
//  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR         //
//  PURPOSE. See the GNU Lesser General Public License for more     //
//  details.                                                        //
//                                                                  //
//  You should have received a copy of the GNU Lesser General       //
//  Public License along with this library; if not, write to the    //
//  Free Software Foundation, Inc., 59 Temple Place, Suite 330,     //
//  Boston, MA 02111-1307, USA.                                     //
//                                                                  //
//  Author contact: P.H.Welch@kent.ac.uk                             //
//                                                                  //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package org.jcsp.net;

import org.jcsp.lang.*;

/**
 * <p>
 * An interface that should be implemented by
 * <CODE>ChannelOutput</CODE> objects which are used for transmitting
 * over the network.
 * </p>
 * <p>
 * As well as usefully combining the <CODE>Networked</CODE> and
 * <CODE>ChannelOutput</CODE> interfaces, this interface adds a
 * <CODE>recreate()</CODE> that requests the implementing class should
 * reinitialize itself.
 * </p>
 *
 * @author Quickstone Technologies Limited
 */
public interface NetChannelOutput extends ChannelOutput, Networked
{
   /**
    * <p>
    * Requests that the instance of the implementing class should
    * reinitialize itself.
    * </p>
    */
   public void recreate();
   
   /**
    * <p>
    * Requests that the instance of the implementing class should
    * reinitialize itself with a new location.
    * </p>
    *
    * @param newLoc the new location.
    */
   public void recreate(NetChannelLocation newLoc);
   
   /**
    * <p>
    * Destroys the channel writer end and frees all the
    * underlying JCSP.NET resources.
    * </p>
    */
   public void destroyWriter();
   
   /**
    * <p>
    * Returns the factory class used for constructing this channel
    * end object.
    * </p>
    *
    * @return the <code>Class</code> of the
    */
   public Class getFactoryClass();
   
}