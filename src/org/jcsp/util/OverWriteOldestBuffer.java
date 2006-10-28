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

package org.jcsp.util;

import java.io.Serializable;

/**
 * This is used to create a buffered object channel that always accepts input,
 * overwriting its oldest data if full.
 * <H2>Description</H2>
 * <TT>OverWriteOldestBuffer</TT> is an implementation of <TT>ChannelDataStore</TT> that yields
 * a <I>FIFO</I> buffered semantics for a channel.  When empty, the channel blocks readers.
 * When full, a writer will overwrite the <I>oldest</I> item left unread in the channel.
 * See the static
 * {@link org.jcsp.lang.Channel#createOne2One(org.jcsp.util.ChannelDataStore) <TT>create</TT>}
 * methods of {@link org.jcsp.lang.Channel} etc.
 * <P>
 * The <TT>getState</TT> method returns <TT>EMPTY</TT> or <TT>NONEMPTYFULL</TT>, but
 * never <TT>FULL</TT>.
 *
 * @see org.jcsp.util.ZeroBuffer
 * @see org.jcsp.util.Buffer
 * @see org.jcsp.util.OverWritingBuffer
 * @see org.jcsp.util.OverFlowingBuffer
 * @see org.jcsp.util.InfiniteBuffer
 * @see org.jcsp.lang.Channel
 *
 * @author P.D.Austin
 */

public class OverWriteOldestBuffer implements ChannelDataStore, Serializable
{
    /** The storage for the buffered Objects */
    private final Object[] buffer;

    /** The number of Objects stored in the Buffer */
    private int counter = 0;

    /** The index of the oldest element (when counter > 0) */
    private int firstIndex = 0;

    /** The index of the next free element (when counter < buffer.length) */
    private int lastIndex = 0;

    /**
     * Construct a new <TT>OverWriteOldestBuffer</TT> with the specified size.
     *
     * @param size the number of <TT>Object</TT>s the <TT>OverWriteOldestBuffer</TT> can store.
     * @throws BufferSizeError if <TT>size</TT> is zero or negative.  Note: no action
     * should be taken to <TT>try</TT>/<TT>catch</TT> this exception
     * - application code generating it is in error and needs correcting.
     */
    public OverWriteOldestBuffer(int size)
    {
        if (size <= 0)
            throw new BufferSizeError
                    ("\n*** Attempt to create an overwriting buffered channel with negative or zero capacity");
        buffer = new Object[size];
    }

    /**
     * Returns the oldest <TT>Object</TT> from the <TT>OverWriteOldestBuffer</TT> and removes it.
     * <P>
     * <I>Pre-condition</I>: <TT>getState</TT> must not currently return <TT>EMPTY</TT>.
     *
     * @return the oldest <TT>Object</TT> from the <TT>OverWriteOldestBuffer</TT>
     */
    public Object get()
    {
        Object value = buffer[firstIndex];
        buffer[firstIndex] = null;
        firstIndex = (firstIndex + 1) % buffer.length;
        counter--;
        return value;
    }

    /**
     * Puts a new <TT>Object</TT> into the <TT>OverWriteOldestBuffer</TT>.
     * <P>
     * If <TT>OverWriteOldestBuffer</TT> is full, the <I>oldest</I> item
     * left unread in the buffer will be overwritten.
     *
     * @param value the <TT>Object</TT> to put into the <TT>OverWriteOldestBuffer</TT>
     */
    public void put(Object value)
    {
        if (counter == buffer.length)
            firstIndex = (firstIndex + 1) % buffer.length;
        else
            counter++;
        buffer[lastIndex] = value;
        lastIndex = (lastIndex + 1) % buffer.length;
    }

    /**
     * Returns the current state of the <TT>OverWriteOldestBuffer</TT>.
     *
     * @return the current state of the <TT>OverWriteOldestBuffer</TT> (<TT>EMPTY</TT> or
     * <TT>NONEMPTYFULL</TT>)
     */
    public int getState()
    {
        if (counter == 0)
            return EMPTY;
        else
            return NONEMPTYFULL;
    }

    /**
     * Returns a new (and <TT>EMPTY</TT>) <TT>OverWriteOldestBuffer</TT> with the same
     * creation parameters as this one.
     * <P>
     * <I>Note: Only the size and structure of the </I><TT>OverWriteOldestBuffer</TT><I> is
     * cloned, not any stored data.</I>
     *
     * @return the cloned instance of this <TT>OverWriteOldestBuffer</TT>.
     */
    public Object clone()
    {
        return new OverWriteOldestBuffer(buffer.length);
    }
}
