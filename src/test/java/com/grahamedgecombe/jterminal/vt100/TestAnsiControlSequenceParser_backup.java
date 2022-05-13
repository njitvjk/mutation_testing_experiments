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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * A test for the {@link AnsiControlSequenceParser_back} class.
 * @author Graham Edgecombe
 */
public class TestAnsiControlSequenceParser_backup implements AnsiControlSequenceListener {

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
		parser.parse(new String(new char[] { 155 }));
		parser.parse(new String(new char[] { 'u' }));

		assertEquals(1, objects.size());

		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		assertEquals('u', seq.getCommand());
		assertEquals(0, params.length);
	}

	/**
	 *002TEST: Tests a broken sequence with the double byte CSI.
	 */
	@Test
	public void testBrokenDoubleSequence() {
		char[] ch1 = { 27 };
		char[] ch2 = { '[' };
		char[] ch3 = { '3', '0', ';' };
		char[] ch4 = { '4', '0', 'm' };

		parser.parse(new String(ch1));
		parser.parse(new String(ch2));
		parser.parse(new String(ch3));
		parser.parse(new String(ch4));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		assertEquals('m', seq.getCommand());
		assertEquals(2, params.length);
		assertEquals("30", params[0]);
		assertEquals("40", params[1]);
	}

	/**
	 *003TEST:Tests an empty string.
	 */
	@Test
	public void testEmpty() {
		parser.parse("");
		assertEquals(0, objects.size());
	}

	/**
	 *004TEST: Tests a sequence embedded within some text.
	 */
	@Test
	public void testTextAndSequence() {
		char[] ch = { 'h', 'i', 155, 'u', 'b', 'y', 'e' };
		parser.parse(new String(ch));

		assertEquals(3, objects.size());

		Object o1 = objects.get(0);
		Object o2 = objects.get(1);
		Object o3 = objects.get(2);

		assertEquals(String.class, o1.getClass());
		assertEquals(AnsiControlSequence.class, o2.getClass());
		assertEquals(String.class, o3.getClass());

		assertEquals("hi", o1);
		assertEquals("bye", o3);

		AnsiControlSequence seq = (AnsiControlSequence) o2;
		String[] params = seq.getParameters();

		assertEquals(0, params.length);
		assertEquals('u', seq.getCommand());
	}

	/**
	 *005TEST:Tests parameters within a sequence.
	 */
	@Test
	public void testParameters() {
		char[] ch = { 155, '3', '0', ';', '4', '0', 'm' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();

		assertEquals('m', seq.getCommand());
		assertEquals(2, params.length);
		assertEquals("30", params[0]);
		assertEquals("40", params[1]);
	}

	/**
	 * 006TEST:Tests with plain text.
	 */
	@Test
	public void testText() {
		parser.parse("Hello, World!");

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(String.class, obj.getClass());
		assertEquals(obj, "Hello, World!");
	}

	/**
	 * 007TEST:Tests with a single byte CSI.
	 */
	@Test
	public void testSingleCsi() {
		char[] ch = { 155, '6', 'n' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('n', seq.getCommand());
		assertEquals(1, params.length);
		assertEquals("6", params[0]);
	}

	/**
	 * 008TEST:Tests with a double byte CSI.
	 */
	@Test
	public void testDoubleCsi() {
		char[] ch = { 27, '[', 's' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('s', seq.getCommand());
		assertEquals(0, params.length);
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
		parser.parse("1234567890");

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(String.class, obj.getClass());
		assertEquals(obj, "1234567890");
	}
	/**
	 * Added0010:Tests with cursor movement up .
	 */
	@Test
	public void testCursorup() {
		char[] ch = { 27, '[', '#','A' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('A', seq.getCommand());
		assertEquals(1, params.length);
	}
	
	/**
	 * Added0011:Tests with cursor movement down .
	 */
	@Test
	public void testCursordown() {
		char[] ch = { 27, '[', '#','B' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('B', seq.getCommand());
		assertEquals(1, params.length);
	}
	
	/**
	 * Added0012:Tests with cursor movement right .
	 */
	@Test
	public void testCursorright() {
		char[] ch = { 27, '[', '#','C' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('C', seq.getCommand());
		assertEquals(1, params.length);
	}

	/**
	 * Added0013:Tests with cursor movement left .
	 */
	@Test
	public void testCursorleft() {
		char[] ch = { 27, '[', '#','D' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('D', seq.getCommand());
		assertEquals(1, params.length);
	}

	/**
	 * Added0014:Tests with cursor movement previous line .
	 */
	@Test
	public void testCursorpreviousline() {
		char[] ch = { 27, '[', '#','F' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('F', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0015:Tests with cursor movement next line .
	 */
	@Test
	public void testCursornextline() {
		char[] ch = { 27, '[', '#','E' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('E', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0016:Tests with ERASE from cursor until end of screen .
	 */
	@Test
	public void testEraseuntilend() {
		char[] ch = { 27, '[', '0','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('J', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0017:Tests with ERASE from cursor to beginning of screen .
	 */
	@Test
	public void testErasefrombeginning() {
		char[] ch = { 27, '[', '1','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('J', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0018:Tests with ERASE entire screen
	 */
	@Test
	public void testEraseentirescreen() {
		char[] ch = { 27, '[', '2','J' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('J', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0019:Tests with ERASE entire line
	 */
	@Test
	public void testEraseentireline() {
		char[] ch = { 27, '[', '2','K' };
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('K', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0020:Tests with foreground color white
	 */
	@Test
	public void testforegroundcolour_white() {
		char[] ch = { 27, '[', '3','8',';','5',';','9' ,'7','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(3, params.length);
	}
	/**
	 * Added0021:Tests with foreground color black
	 */
	@Test
	public void testforegroundcolour_black() {
		char[] ch = { 27, '[', '3','8',';','5',';','9' ,'0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(3, params.length);
	}
	/**
	 * Added0022:Tests with background color white
	 */
	@Test
	public void testbackgroundcolour_white() {
		char[] ch = { 27, '[', '4','8',';','5',';','9' ,'7','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(3, params.length);
	}
	/**
	 * Added0023:Tests with background color black
	 */
	@Test
	public void testbackgroundcolour_black() {
		char[] ch = { 27, '[', '4','8',';','5',';','9' ,'0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(3, params.length);
	}
	/**
	 * Added0024:Tests with graphic mode -italics
	 */
	@Test
	public void testgraphicmode_italics() {
		char[] ch = { 27, '[', '2','3','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0025:Tests with graphic mode -set underline
	 */
	@Test
	public void testgraphicmode_underline() {
		char[] ch = { 27, '[', '2','4','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0026:Tests with graphic mode -set bold
	 */
	@Test
	public void testgraphicmode_bold() {
		char[] ch = { 27, '[', '2','2','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(1, params.length);
	}
	
	/**
	 * Added0027:Tests with graphic mode -strike through
	 */
	@Test
	public void testgraphicmode_strike() {
		char[] ch = { 27, '[', '2','9','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0028:Tests with graphic mode -reset
	 */
	@Test
	public void testgraphicmode_reset() {
		char[] ch = { 27, '[', '0','m'};
		parser.parse(new String(ch));

		assertEquals(1, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		assertEquals('m', seq.getCommand());
		assertEquals(1, params.length);
	}
	/**
	 * Added0029:Tests with style text-dimmed white foreground with red background
	 */
	@Test
	public void teststyletext_red() {
		char[] ch = { 27, '[', '2',';','3','7','m','H','e','l','l','o'};
		parser.parse(new String(ch));

		assertEquals(2, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		
		assertEquals(2, params.length);
	
		assertEquals("37", params[1]);
	}
	/**
	 * Added0030:Tests with style text-bold with red foreground
	 */
	@Test
	public void teststyletext_boldred() {
		char[] ch = { 27, '[', '1',';','3','1','m','H','i'};
		parser.parse(new String(ch));

		assertEquals(2, objects.size());
		Object obj = objects.get(0);
		assertEquals(AnsiControlSequence.class, obj.getClass());

		AnsiControlSequence seq = (AnsiControlSequence) obj;
		String[] params = seq.getParameters();
		
		assertEquals(2, params.length);
	}

}

