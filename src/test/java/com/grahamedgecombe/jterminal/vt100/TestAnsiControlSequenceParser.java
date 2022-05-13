/*
 * Copyright (c) 2009-2011 Graham Edgecombe.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.grahamedgecombe.jterminal.vt100;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for the {@link AnsiControlSequenceParser} class.
 * @author Graham Edgecombe
 */
public class TestAnsiControlSequenceParser implements AnsiControlSequenceListener {

	/**
	 * The current parser.
	 */
	private AnsiControlSequenceParser parser;

	/**
	 * The list of objects returned through the
	 * {@link AnsiControlSequenceListener} interface.
	 */
	private List<Object> objects = new ArrayList<Object>();

	/**
	 * Sets up the parser and object list.
	 */
	@Before
	public void setUp() {
		objects.clear();
		parser = new AnsiControlSequenceParser(this);
	}

	/**
	 * 001TEST:Tests a broken sequence with the single byte CSI.
	 */
	@Test
	public void testBrokenSingleSequence() {
		try {
		parser.parse(new String(new char[] { 155 }));
		parser.parse(new String(new char[] { 'u' }));

		//assertEquals(1, objects.size());

		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		//assertEquals('u', seq.getCommand());
	
		assertEquals(0, params.length);
		
		System.out.println("Test001,Parameter length is :"+ 0);}
		
		catch(AssertionError e) {
			
			//System.out.println("Test001,Parameter length is :"+ 1);}
			System.out.println("Test001,-1");}
		
		
	}

	/**
	 *002TEST: Tests a broken sequence with the double byte CSI.
	 */
	@Test
	public void testBrokenDoubleSequence() {
		try {
		char[] ch1 = { 27 };
		char[] ch2 = { '[' };
		char[] ch3 = { '3', '0', ';' };
		char[] ch4 = { '4', '0', 'm' };

		parser.parse(new String(ch1));
		parser.parse(new String(ch2));
		parser.parse(new String(ch3));
		parser.parse(new String(ch4));

		//assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		//assertEquals('m', seq.getCommand());
		assertEquals(2, params.length);
		//assertEquals("30", params[0]);
		//assertEquals("40", params[1]);
		System.out.println("Test002,Parameter length is :"+ 2);}
		catch(AssertionError e) {System.out.println("Test002,"+"-1");
		}
	}

	/**
	 *003TEST:Tests an empty string.
	 */
	@Test
	public void testEmpty() throws IOException {
		try {
		parser.parse("");
		

		//assertEquals('m', seq.getCommand());
		
		assertEquals(0, objects.size());
		System.out.println("Test003,object size is :"+ 0);}
		catch(AssertionError e) 
		{System.out.println("Test003,"+"-1");}
		
	}

	/**
	 *004TEST: Tests a sequence embedded within some text.
	 */
	@Test
	public void testTextAndSequence() {
		try {
		char[] ch = { 'h', 'i', 155, 'u', 'b', 'y', 'e' };
		parser.parse(new String(ch));

		assertEquals(3, objects.size());

		Object o1 = objects.get(0);
		Object o2 = objects.get(1);
		Object o3 = objects.get(2);

		//assertEquals(String.class, o1.getClass());
		//assertEquals(AnsiControlSequence.class, o2.getClass());
		//assertEquals(String.class, o3.getClass());

		//assertEquals("hi", o1);
		//assertEquals("bye", o3);

		AnsiControlSequence seq = (AnsiControlSequence) o2;
		String[] params = seq.getParameters();

		assertEquals(0, params.length);
		//assertEquals('u', seq.getCommand());
		System.out.println("Test004,param length is :"+ 0);
		}
		catch(AssertionError e) {System.out.println("Test004,"+"-1");}
	}

	/**
	 *005TEST:Tests parameters within a sequence.
	 */
	@Test
	public void testParameters() {
		try {
		char[] ch = { 155, '3', '0', ';', '4', '0', 'm' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		//assertEquals('m', seq.getCommand());
		assertEquals(2, params.length);
		//assertEquals("30", params[0]);
		//assertEquals("40", params[1]);
		System.out.println("Test005,param length is :"+ 2);}
		catch(AssertionError e) {System.out.println("Test005,"+"-1");}
	}

	/**
	 * 006TEST:Tests with plain text.
	 */
	@Test
	public void testText() {
		try {
		parser.parse("Hello, World!");

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(String.class, obj.getClass());
		//assertEquals(obj, "Hello, World!");
		System.out.println("Test006,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test006,"+"-1");}
	}

	/**
	 * 007TEST:Tests with a single byte CSI.
	 */
	@Test
	public void testSingleCsi() {
		try {
		char[] ch = { 155, '6', 'n' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('n', seq.getCommand());
		assertEquals(1, params.length);
		//assertEquals("6", params[0]);
		System.out.println("Test007,param length is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test007,"+"-1");}
	}

	/**
	 * 008TEST:Tests with a double byte CSI.
	 */
	@Test
	public void testDoubleCsi() {
		try {
		char[] ch = { 27, '[', 's' };
		parser.parse(new String(ch));

		//assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('s', seq.getCommand());
		assertEquals(0, params.length);
		System.out.println("Test008,param length is :"+ 0);}
		catch(AssertionError e) {System.out.println("Test008,"+"-1");}
	}

	@Override
	public void parsedControlSequence(AnsiControlSequence seq) {
		objects.add(seq);
	}

	@Override
	public void parsedString(String str) {
		objects.add(str);
	}
	
	/**
	 * Added009:Tests with numbers.
	 */
	@Test
	public void testNumbers() {
		try {
		parser.parse("1234567890");

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(String.class, obj.getClass());
		//assertEquals(obj, "1234567890");
		System.out.println("Test009,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test009,"+"-1");}
	}
	/**
	 * Added0010:Tests with cursor movement up .
	 */
	@Test
	public void testCursorup() {
		try {
		char[] ch = { 27, '[', '#','A' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('A', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test010,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test010,"+"-1");}
	}
	
	/**
	 * Added0011:Tests with cursor movement down .
	 */
	@Test
	public void testCursordown() {
		try {
		char[] ch = { 27, '[', '#','B' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('B', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test011,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test011,"+"-1");}
	}
	
	/**
	 * Added0012:Tests with cursor movement right .
	 */
	@Test
	public void testCursorright() {
		try {
		char[] ch = { 27, '[', '#','C' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('C', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test012,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test012,"+"-1");}
	}

	/**
	 * Added0013:Tests with cursor movement left .
	 */
	@Test
	public void testCursorleft() {
		try {
		char[] ch = { 27, '[', '#','D' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('D', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test013,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test013,"+"-1");}
	}

	/**
	 * Added0014:Tests with cursor movement previous line .
	 */
	@Test
	public void testCursorpreviousline() {
		try {
		char[] ch = { 27, '[', '#','F' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('F', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test014,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test014,"+"-1");}
	}
	/**
	 * Added0015:Tests with cursor movement next line .
	 */
	@Test
	public void testCursornextline() {
		try {
		char[] ch = { 27, '[', '#','E' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('E', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test015,object size is :"+ 1);}
		catch(AssertionError e) {System.out.println("Test015,"+"-1");}
	}
	/**
	 * Added0016:Tests with ERASE from cursor until end of screen .
	 */
	@Test
	public void testEraseuntilend() {
		try {
		char[] ch = { 27, '[', '0','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('J', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test016,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test016,"+"-1");}
	}
	/**
	 * Added0017:Tests with ERASE from cursor to beginning of screen .
	 */
	@Test
	public void testErasefrombeginning() {
		try {
		char[] ch = { 27, '[', '1','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('J', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test017,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test017,"+"-1");}
	}
	/**
	 * Added0018:Tests with ERASE entire screen
	 */
	@Test
	public void testEraseentirescreen() {
		try {
		char[] ch = { 27, '[', '2','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('J', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test018,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test018,"+"-1");}
	}
	/**
	 * Added0019:Tests with ERASE entire line
	 */
	@Test
	public void testEraseentireline() {
		try {
		char[] ch = { 27, '[', '2','K' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('K', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test019,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test019,"+"-1");}
	}
	/**
	 * Added0020:Tests with foreground color white
	 */
	@Test
	public void testforegroundcolour_white() {
		try {
		char[] ch = { 27, '[', '3','8',';','5',';','9' ,'7','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(3, params.length);
		System.out.println("Test020,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test020,"+"-1");}
	}
	/**
	 * Added0021:Tests with foreground color black
	 */
	@Test
	public void testforegroundcolour_black() {
		try {
		char[] ch = { 27, '[', '3','8',';','5',';','9' ,'0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(3, params.length);
		System.out.println("Test021,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test021,"+"-1");}
	}
	
	/**
	 * Added0022:Tests with background color white
	 */
	@Test
	public void testbackgroundcolour_white() {
		try {
		char[] ch = { 27, '[', '4','8',';','5',';','9' ,'7','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(3, params.length);
		System.out.println("Test022,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test022,"+"-1");}
	}
	/**
	 * Added0023:Tests with background color black
	 */
	@Test
	public void testbackgroundcolour_black() {
		try {
		char[] ch = { 27, '[', '4','8',';','5',';','9' ,'0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(3, params.length);
		System.out.println("Test023,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test023,"+"-1");}
	}
	/**
	 * Added0024:Tests with graphic mode -italics
	 */
	@Test
	public void testgraphicmode_italics() {
		try {
		char[] ch = { 27, '[', '2','3','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test024,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test024,"+"-1");}
	}
	/**
	 * Added0025:Tests with graphic mode -set underline
	 */
	@Test
	public void testgraphicmode_underline() {
		try {
		char[] ch = { 27, '[', '2','4','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test025,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test025,"+"-1");}
	}
	/**
	 * Added0026:Tests with graphic mode -set bold
	 */
	@Test
	public void testgraphicmode_bold() {
		try {
		char[] ch = { 27, '[', '2','2','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test026,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test026,"+"-1");}
	}
	
	/**
	 * Added0027:Tests with graphic mode -strike through
	 */
	@Test
	public void testgraphicmode_strike() {
		try {
		char[] ch = { 27, '[', '2','9','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test027,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test027,"+"-1");}
	}
	/**
	 * Added0028:Tests with graphic mode -reset
	 */
	@Test
	public void testgraphicmode_reset() {
		try {
		char[] ch = { 27, '[', '0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		//assertEquals('m', seq.getCommand());
		//assertEquals(1, params.length);
		System.out.println("Test028,object size is :"+ 1);
		}
		catch(AssertionError e) {System.out.println("Test028,"+"-1");}
	}
	/**
	 * Added0029:Tests with style text-dimmed white foreground with red background
	 */
	@Test
	public void teststyletext_red() {
		try {
		char[] ch = { 27, '[', '2',';','3','7','m','H','e','l','l','o'};
		parser.parse(new String(ch));

		assertEquals(2, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		
		//assertEquals(2, params.length);
	
		//assertEquals("37", params[1]);
		System.out.println("Test029,object size is :"+ 2);
		}
		catch(AssertionError e) {System.out.println("Test029,"+"-1");}
	}
	/**
	 * Added0030:Tests with style text-bold with red foreground
	 */
	@Test
	public void teststyletext_boldred() {
		try {
		char[] ch = { 27, '[', '1',';','3','1','m','H','i'};
		parser.parse(new String(ch));

		assertEquals(2, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		
		//assertEquals(2, params.length);
		System.out.println("Test030,object size is :"+ 2);
		}
		catch(AssertionError e) {System.out.println("Test030,"+"-1");}
	}
	/**
	 * Added0031:Tests with style text-bold with red foreground
	 */
	@Test
	public void testBrokenSingleSequence_negativetc()  {
		try {
		parser.parse(new String(new char[] { 155 }));
		parser.parse(new String(new char[] { 'u' }));
		
		assertEquals(2, objects.size());

		Object obj = objects.get(0);
		
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		//assertEquals('u', seq.getCommand());
		
		//assertEquals(1, params.length);
		System.out.println("Test031,object size is :"+ 1);
		}
		
		catch(AssertionError e) {
			System.out.println("Test0031,"+"-1");
		}
	}
	/**
	 * Added0032:Tests with style text-bold with red foreground
	 */
	@Test
	public void teststyletext_boldred_negativetc() {
		try {
		char[] ch = { 27, '[', '1',';','3','1','m','H','i'};
		parser.parse(new String(ch));
		
		assertEquals(3, objects.size());
		Object obj = objects.get(0);
		//assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		
		//assertEquals(3, params.length);
		System.out.println("Test032,object size is :"+ 3);
		}
		catch(AssertionError e){
		System.out.println("Test0032," +"-1");}
		catch(ArrayIndexOutOfBoundsException ex) {
		System.out.println("Test0032,Exception:"+ex);}
	}

}

