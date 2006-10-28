//////////////////////////////////////////////////////////////////////
//                                                                  //
//  JCSP ("CSP for Java") Libraries                                 //
//  Copyright (C) 1996-2001 Peter Welch and Paul Austin.            //
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
//  Author contact: P.H.Welch@ukc.ac.uk                             //
//                  mailbox@quickstone.com                          //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package org.jcsp.awt;

import java.awt.*;
import java.util.Vector;
import org.jcsp.lang.*;

/**
 * {@link java.awt.Label <TT>java.awt.Label</TT>}
 * with a channel interface.
 * <H2>Process Diagram</H2>
 * <p><img src="doc-files/ActiveLabel1.gif"></p>
 * <H2>Description</H2>
 * <TT>ActiveLabel</TT> is a process extension of <TT>java.awt.Label</TT>
 * with channels for run-time configuration and event notification.  The event channels
 * should be connected to one or more application-specific server processes (instead
 * of registering a passive object as a <I>Listener</I> to this component).
 * <P>
 * All channels are optional.  The <TT>configure</TT> channel is settable from a constructor.
 * Event channels can be added to notify the occurrence of any type of <TT>Event</TT>
 * the component generates (by calling the appropriate
 * <TT>add</TT><I>XXX</I><TT>EventChannel</TT> method <I>before</I> the process is run).
 * Messages can be sent down the <TT>configure</TT> channel at any time to configure
 * the component.  See the <A HREF="#Protocols">table below</A> for details.
 * <P>
 * All channels are managed by independent internal handler processes.  It is, therefore,
 * safe for a serial application process both to service an event channel and configure
 * the component -- no deadlock can occur.
 * <P>
 * <I>IMPORTANT: it is essential that event channels from this process are
 * always serviced -- otherwise the Java Event Thread will be blocked and the GUI
 * will stop responding.  A simple way to guarantee this is to use channels
 * configured with overwriting buffers.
 * For example:</I>
 * <PRE>
 *   final One2OneChannel myMouseEvent = Channel.createOne2One (new OverWriteOldestBuffer (n));
 * <I></I>
 *   final ActiveLabel myLabel = new ActiveLabel ("Look at Me");
 *   myContainer.addMouseEventChannel (myMouseEvent.out ());
 * </PRE>
 * <I>This will ensure that the Java Event Thread will never be blocked.
 * Slow or inattentive readers may miss rapidly generated events, but
 * the </I><TT>n</TT><I> most recent events will always be available.</I>
 * </P>
 * <H2><A NAME="Protocols">Channel Protocols</A></H2>
 * <CENTER>
 * <TABLE BORDER="2">
 *   <TR>
 *     <TH COLSPAN="3">Input Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH ROWSPAN="3">configure</TH>
 *     <TD>String</TD>
 *     <TD>Change the label on the <TT>ActiveLabel</TT> to the value of the <TT>String</TT></TD>
 *   </TR>
 *   <TR>
 *     <TD>Boolean</TD>
 *     <TD>
 *       <OL>
 *         <LI>If this is the <TT>Boolean.TRUE</TT> object,
 *           the label is made active</LI>
 *         <LI>If this is the <TT>Boolean.FALSE</TT> object,
 *            the label is made inactive</LI>
 *         <LI>Other <TT>Boolean</TT> objects are ignored</LI>
 *       </OL>
 *     </TD>
 *   </TR>
 *   <TR>
 *     <TD>ActiveLabel.Configure</TD>
 *     <TD>Invoke the user-defined <TT>Configure.configure</TT> method on the label.</TD>
 *   </TR>
 *   <TR>
 *     <TH COLSPAN="3">Output Channels</TH>
 *   </TR>
 *   <TR>
 *     <TH>componentEvent</TH>
 *     <TD>ComponentEvent</TD>
 *     <TD>See the {@link #addComponentEventChannel
 *         <TT>addComponentEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>focusEvent</TH>
 *     <TD>FocusEvent</TD>
 *     <TD>See the {@link #addFocusEventChannel
 *         <TT>addFocusEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>keyEvent</TH>
 *     <TD>KeyEvent</TD>
 *     <TD>See the {@link #addKeyEventChannel
 *         <TT>addKeyEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link #addMouseEventChannel
 *         <TT>addMouseEventChannel</TT>} method.</TD>
 *   </TR>
 *   <TR>
 *     <TH>mouseMotionEvent</TH>
 *     <TD>MouseEvent</TD>
 *     <TD>See the {@link #addMouseMotionEventChannel
 *         <TT>addMouseMotionEventChannel</TT>} method.</TD>
 *   </TR>
 * </TABLE>
 * </CENTER>
 * <H2>Example</H2>
 * <PRE>
 * import java.awt.*;
 * import org.jcsp.lang.*;
 * import org.jcsp.util.*;
 * import org.jcsp.awt.*;
 * <I></I>
 * public class ActiveLabelExample {
 * <I></I>
 *   public static void main (String argv[]) {
 * <I></I>
 *     final Frame root = new Frame ("ActiveLabel Example");
 * <I></I>
 *     final int nLabels = 8;
 *     final int countdown = 10;
 * <I></I>
 *     final One2OneChannel[] configureLabel = Channel.createOne2One (nLabels);
 * <I></I>
 *     final ActiveLabel[] label = new ActiveLabel[nLabels];
 *     for (int i = 0; i < label.length; i++) {
 *       label[i] = new ActiveLabel (configureLabel[i].in (), "==>  " + countdown + "  <==");
 *       label[i].setAlignment (Label.CENTER);
 *     }
 * <I></I>
 *     final One2OneChannel configureButton = Channel.createOne2One ();
 *     final One2OneChannel event = Channel.createOne2One (new OverWriteOldestBuffer (10));
 * <I></I>
 *     final ActiveButton button = new ActiveButton (configureButton.in (), event.out (), "Start");
 * <I></I>
 *     root.setSize (300, 200);
 *     root.setLayout (new GridLayout (3, 3));
 *     for (int i = 0; i < nLabels + 1; i++) {
 *       if (i < 4) {
 *         root.add (label[i]);
 *       } else if (i == 4) {
 *         root.add (button);
 *       } else if (i > 4) {
 *         root.add (label[i - 1]);
 *       }
 *     }
 *     root.setVisible (true);
 * <I></I>
 *     new Parallel (
 *       new CSProcess[] {
 *         new Parallel (label),
 *         button,
 *         new CSProcess () {
 *           public void run () {
 *             final long second = 1000;
 *             CSTimer tim = new CSTimer ();
 *             Alternative alt = new Alternative (new Guard[] {event.in (), tim});
 *             event.in ().read ();              // wait for the start signal
 *             configureButton.out ().write ("Restart");
 *             int count = countdown;
 *             long timeout = tim.read () + second;
 *             while (count > 0) {
 *               tim.setAlarm (timeout);
 *               switch (alt.priSelect ()) {
 *                 case 0:                 // reset signal
 *                   event.in ().read ();        // clear the reset
 *                   timeout = tim.read () + second;
 *                   count = countdown;
 *                 break;
 *                 case 1:                 // timeout signal
 *                   timeout += second;
 *                   count--;
 *                 break;
 *               }
 *               final String newLabel = "==>  " + count + "  <==";
 *               for (int i = 0; i < nLabels; i++) {
 *                 configureLabel[i].out ().write (newLabel);
 *               }
 *             }
 *             root.setVisible (false);
 *             System.exit (0);
 *           }
 *         }
 *       }
 *     ).run ();
 * <I></I>
 *   }
 * <I></I>
 * }
 * </PRE>
 *
 * @see java.awt.Label
 * @see java.awt.event.ComponentEvent
 * @see java.awt.event.FocusEvent
 * @see java.awt.event.KeyEvent
 * @see java.awt.event.MouseEvent
 * @see org.jcsp.util.OverWriteOldestBuffer
 *
 * @author P.D.Austin and P.H.Welch
 */

public class ActiveLabel extends Label implements CSProcess
{
   /**
    * The Vector construct containing the handlers.
    */
   private Vector vec = new Vector();
   
   /**
    * The channel from which configuration messages arrive.
    */
   private ChannelInput configure;
   
   /**
    * Constructs a new <TT>ActiveLabel</TT> with no label or configuration channel.
    */
   public ActiveLabel()
   {
      this("");
   }
   
   /**
    * Constructs a new <TT>ActiveLabel</TT> with no configuration channel.
    *
    * @param s the initial string displayed on the label.
    */
   public ActiveLabel(String s)
   {
      this(null, s);
   }
   
   /**
    * Constructs a new <TT>ActiveLabel</TT> with no initial label.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public ActiveLabel(ChannelInput configure)
   {
      this(configure, "");
   }
   
   /**
    * Constructs a new <TT>ActiveLabel</TT>.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    * @param s the initial string displayed on the label.
    */
   public ActiveLabel(ChannelInput configure, String s)
   {
      super(s);
      this.configure = configure;
   }
   
   /**
    * Sets the configuration channel for this <TT>ActiveLabel</TT>.
    * This method overwrites any configuration channel set in the constructor.
    *
    * @param configure the channel for configuration events
    * -- can be null if no configuration is required.
    */
   public void setConfigureChannel(ChannelInput configure)
   {
      this.configure = configure;
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>ComponentEvent</TT> has occurred. <I>This should be used
    * instead of registering a ComponentListener with the component.</I>  It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param <TT>componentEvent</TT> the channel down which to send <TT>ComponentEvent</TT>s.
    */
   public void addComponentEventChannel(ChannelOutput componentEvent)
   {
      if (componentEvent != null)
      {
         ComponentEventHandler handler = new ComponentEventHandler(componentEvent);
         addComponentListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>FocusEvent</TT> has occurred. <I>This should be used
    * instead of registering a FocusListener with the component.</I> It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param <TT>focusEvent</TT> the channel down which to send <TT>FocusEvent</TT>s.
    */
   public void addFocusEventChannel(ChannelOutput focusEvent)
   {
      if (focusEvent != null)
      {
         FocusEventHandler handler = new FocusEventHandler(focusEvent);
         addFocusListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>KeyEvent</TT> has occurred. <I>This should be used
    * instead of registering a KeyListener with the component.</I> It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param <TT>keyEvent</TT> the channel down which to send <TT>KeyEvent</TT>s.
    */
   public void addKeyEventChannel(ChannelOutput keyEvent)
   {
      if (keyEvent != null)
      {
         KeyEventHandler handler = new KeyEventHandler(keyEvent);
         addKeyListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>MouseEvent</TT> has occurred. <I>This should be used
    * instead of registering a MouseListener with the component.</I> It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param <TT>mouseEvent</TT> the channel down which to send <TT>MouseEvent</TT>s.
    */
   public void addMouseEventChannel(ChannelOutput mouseEvent)
   {
      if (mouseEvent != null)
      {
         MouseEventHandler handler = new MouseEventHandler(mouseEvent);
         addMouseListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * Add a new channel to this component that will be used to notify that
    * a <TT>MouseMotionEvent</TT> has occurred. <I>This should be used
    * instead of registering a MouseMotionListener with the component.</I> It is
    * possible to add more than one channel by calling this method multiple times
    * If the channel passed is <TT>null</TT>, no action will be taken.
    * <P>
    * <I>NOTE: This method must be called before this process is run.</I>
    *
    * @param <TT>mouseMotionEvent</TT> the channel down which to send <TT>MouseMotionEvent</TT>s.
    */
   public void addMouseMotionEventChannel(ChannelOutput mouseMotionEvent)
   {
      if (mouseMotionEvent != null)
      {
         MouseMotionEventHandler handler = new MouseMotionEventHandler(mouseMotionEvent);
         addMouseMotionListener(handler);
         vec.addElement(handler);
      }
   }
   
   /**
    * This enables general configuration of this component.  Any object implementing
    * this interface and sent down the <TT>configure</TT> channel to this component will have its
    * <TT>configure</TT> method invoked on this component.
    * <P>
    * For an example, see {@link ActiveApplet.Configure}.
    */
   static public interface Configure
   {
      /**
       * @param <TT>label</TT> the Label being configured.
       */
      public void configure(final Label label);
   }
   
   /**
    * The main body of this process.
    */
   public void run()
   {
      if (configure != null)
      {
         while (true)
         {
            Object message = configure.read();
            if (message instanceof String)
               setText((String) message);
            else if (message instanceof Boolean)
            {
               if (message == Boolean.TRUE)
                  setEnabled(true);
               else if (message == Boolean.FALSE)
                  setEnabled(false);
            }
            else if (message instanceof Configure)
               ((Configure) message).configure(this);
         }
      }
   }
}