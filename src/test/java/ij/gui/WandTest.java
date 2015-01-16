/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package ij.gui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import org.junit.Test;

/**
 * Unit tests for {@link Wand}.
 *
 * @author Barry DeZonia
 */
public class WandTest {

	// data
	
	private static byte[] Pixels7x3 = new byte[]
	                                   	    {0,0,0,0,0,0,0,
	                                   		 0,0,1,1,1,0,0,
	                                   		 0,0,0,0,0,0,0};
	
	private static byte[] Pixels5x5 = new byte[]
	    {0,0,0,0,0,
		 0,0,1,0,0,
		 0,1,0,1,0,
		 0,0,1,0,0,
		 0,0,0,0,0};
	
	// instance vars
	
	Wand w;
	
	// helpers
	
	/** basic constructor from bytes */
	private Wand newWand(int width, int height, byte[] bytes)
	{
		ImageProcessor proc = new ByteProcessor(width,height,bytes,null);
		return new Wand(proc);
	}

	/** validates a Wand's output data against passed in reference data */
	private void outlineTest(Wand w, int[] xs, int[] ys)
	{
		// validate reference data
		assertEquals(xs.length,ys.length);
		
		// validate Wand data
		assertEquals(xs.length,w.npoints);
		
		// make a copy of data so we can use more informative method arrayEquals()
		int[] tmp = new int[w.npoints];
		for (int i = 0; i < w.npoints; i++)
		  tmp[i] = w.xpoints[i];
		
		// test x vals
		assertArrayEquals(xs,tmp);
		
		// make a copy of data so we can use more informative method arrayEquals()
		for (int i = 0; i < w.npoints; i++)
			  tmp[i] = w.ypoints[i];
		
		// test y vals
		assertArrayEquals(ys,tmp);
	}
	
	// tests +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	@Test
	public void testPublicConstants()
	{
		// constants test
		assertEquals(4,Wand.FOUR_CONNECTED);
		assertEquals(8,Wand.EIGHT_CONNECTED);
		assertEquals(1,Wand.LEGACY_MODE);
	}
	
	@Test
	public void testWand() {
		// constructor test
		w = newWand(5,5,Pixels5x5);
		assertEquals(0,w.npoints);
		assertArrayEquals(new int[1000], w.xpoints);
		assertArrayEquals(new int[1000], w.ypoints);
	}

	@Test
	public void testAutoOutlineIntIntDoubleDoubleInt() {
		
		// mode default of 0
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, 0);
		outlineTest(w,new int[]{4,3,3,2,2,1,1,2,2,3,3,4},new int[]{3,3,4,4,3,3,2,2,1,1,2,2});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, 0);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, Wand.LEGACY_MODE);
		outlineTest(w,new int[]{3,2,2,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, Wand.LEGACY_MODE);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// EIGHT_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{4,3,3,2,2,1,1,2,2,3,3,4},new int[]{3,3,4,4,3,3,2,2,1,1,2,2});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// FOUR_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{3,4,4,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE AND EIGHT_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, Wand.LEGACY_MODE|Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{3,2,2,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, Wand.LEGACY_MODE|Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE AND FOUR_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, Wand.LEGACY_MODE|Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{3,2,2,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, Wand.LEGACY_MODE|Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});
	}

	@Test
	public void testAutoOutlineIntIntDoubleDouble() {
		// thresholds of 1.0 and 1.0
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0);
		outlineTest(w,new int[]{3,2,2,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1.0, 1.0, 0);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});
	}

	@Test
	public void testAutoOutlineIntIntIntInt() {
		// thresholds of 1.0 and 1.0
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1, 1);
		outlineTest(w,new int[]{3,2,2,3},new int[]{2,2,3,3});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 1, 1);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});
	}

	@Test
	public void testAutoOutlineIntInt() {
		// default
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2);
		outlineTest(w,new int[]{3,2,2,3},new int[]{3,3,2,2});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});
	}

	@Test
	public void testAutoOutlineIntIntDoubleInt() {
		// default mode 0
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, 0);
		outlineTest(w,new int[]{5,0,0,5},new int[]{5,5,0,0});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, 0);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, Wand.LEGACY_MODE);
		outlineTest(w,new int[]{5,0,0,5},new int[]{5,5,0,0});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, Wand.LEGACY_MODE);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// EIGHT_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,0,0,5},new int[]{5,5,0,0});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// FOUR_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{3,2,2,3},new int[]{3,3,2,2});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE and EIGHT_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, Wand.LEGACY_MODE|Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,0,0,5},new int[]{5,5,0,0});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, Wand.LEGACY_MODE|Wand.EIGHT_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});

		// LEGACY_MODE and FOUR_CONNECTED
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 0.5, Wand.LEGACY_MODE|Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{3,2,2,3},new int[]{3,3,2,2});

		w = newWand(7,3,Pixels7x3);
		w.autoOutline(2, 1, 0.5, Wand.LEGACY_MODE|Wand.FOUR_CONNECTED);
		outlineTest(w,new int[]{5,2,2,5},new int[]{2,2,1,1});
	}

	@Test
	public void testSetAndGetAllPoints() {
		// test default value
		assertFalse(Wand.allPoints());
		
		Wand.setAllPoints(true);
		assertTrue(Wand.allPoints());

		// test side effects
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, 0);
		outlineTest(w,new int[]{4,3,3,2,2,1,1,2,2,3,3,4,4},new int[]{3,3,4,4,3,3,2,2,1,1,2,2,3});

		Wand.setAllPoints(false);  // make sure we reset
		assertFalse(Wand.allPoints());

		// test side effects
		w = newWand(5,5,Pixels5x5);
		w.autoOutline(2, 2, 1.0, 1.0, 0);
		outlineTest(w,new int[]{4,3,3,2,2,1,1,2,2,3,3,4},new int[]{3,3,4,4,3,3,2,2,1,1,2,2});
	}
}
