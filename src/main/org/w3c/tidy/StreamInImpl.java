/*
 *  Java HTML Tidy - JTidy
 *  HTML parser and pretty printer
 *
 *  Copyright (c) 1998-2000 World Wide Web Consortium (Massachusetts
 *  Institute of Technology, Institut National de Recherche en
 *  Informatique et en Automatique, Keio University). All Rights
 *  Reserved.
 *
 *  Contributing Author(s):
 *
 *     Dave Raggett <dsr@w3.org>
 *     Andy Quick <ac.quick@sympatico.ca> (translation to Java)
 *     Gary L Peskin <garyp@firstech.com> (Java development)
 *     Sami Lempinen <sami@lempinen.net> (release management)
 *     Fabrizio Giustina <fgiust at users.sourceforge.net>
 *
 *  The contributing author(s) would like to thank all those who
 *  helped with testing, bug fixes, and patience.  This wouldn't
 *  have been possible without all of you.
 *
 *  COPYRIGHT NOTICE:
 * 
 *  This software and documentation is provided "as is," and
 *  the copyright holders and contributing author(s) make no
 *  representations or warranties, express or implied, including
 *  but not limited to, warranties of merchantability or fitness
 *  for any particular purpose or that the use of the software or
 *  documentation will not infringe any third party patents,
 *  copyrights, trademarks or other rights. 
 *
 *  The copyright holders and contributing author(s) will not be
 *  liable for any direct, indirect, special or consequential damages
 *  arising out of any use of the software or documentation, even if
 *  advised of the possibility of such damage.
 *
 *  Permission is hereby granted to use, copy, modify, and distribute
 *  this source code, or portions hereof, documentation and executables,
 *  for any purpose, without fee, subject to the following restrictions:
 *
 *  1. The origin of this source code must not be misrepresented.
 *  2. Altered versions must be plainly marked as such and must
 *     not be misrepresented as being the original source.
 *  3. This Copyright notice may not be removed or altered from any
 *     source or altered source distribution.
 * 
 *  The copyright holders and contributing author(s) specifically
 *  permit, without fee, and encourage the use of this source code
 *  as a component for supporting the Hypertext Markup Language in
 *  commercial products. If you use this source code in a product,
 *  acknowledgment is not required but would be appreciated.
 *
 */
package org.w3c.tidy;

import java.io.IOException;
import java.io.InputStream;


/**
 * Input Stream Implementation.
 * @author Dave Raggett <a href="mailto:dsr@w3.org">dsr@w3.org </a>
 * @author Andy Quick <a href="mailto:ac.quick@sympatico.ca">ac.quick@sympatico.ca </a> (translation to Java)
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class StreamInImpl implements StreamIn
{

    /**
     * number of characters kept in buffer.
     */
    private static final int CHARBUF_SIZE = 5;

    /**
     * Mapping for Windows Western character set (128-159) to Unicode.
     */
    private static final int[] WIN2UNICODE = {
        0x20AC,
        0x0000,
        0x201A,
        0x0192,
        0x201E,
        0x2026,
        0x2020,
        0x2021,
        0x02C6,
        0x2030,
        0x0160,
        0x2039,
        0x0152,
        0x0000,
        0x017D,
        0x0000,
        0x0000,
        0x2018,
        0x2019,
        0x201C,
        0x201D,
        0x2022,
        0x2013,
        0x2014,
        0x02DC,
        0x2122,
        0x0161,
        0x203A,
        0x0153,
        0x0000,
        0x017E,
        0x0178};

    /**
     * John Love-Jensen contributed this table for mapping MacRoman character set to Unicode.
     */
    private static final int[] MAC2UNICODE = {
        0x0000,
        0x0001,
        0x0002,
        0x0003,
        0x0004,
        0x0005,
        0x0006,
        0x0007,
        0x0008,
        0x0009,
        0x000A,
        0x000B,
        0x000C,
        0x000D,
        0x000E,
        0x000F,
        0x0010,
        0x0011,
        0x0012,
        0x0013,
        0x0014,
        0x0015,
        0x0016,
        0x0017,
        0x0018,
        0x0019,
        0x001A,
        0x001B,
        0x001C,
        0x001D,
        0x001E,
        0x001F,
        0x0020,
        0x0021,
        0x0022,
        0x0023,
        0x0024,
        0x0025,
        0x0026,
        0x0027,
        0x0028,
        0x0029,
        0x002A,
        0x002B,
        0x002C,
        0x002D,
        0x002E,
        0x002F,
        0x0030,
        0x0031,
        0x0032,
        0x0033,
        0x0034,
        0x0035,
        0x0036,
        0x0037,
        0x0038,
        0x0039,
        0x003A,
        0x003B,
        0x003C,
        0x003D,
        0x003E,
        0x003F,
        0x0040,
        0x0041,
        0x0042,
        0x0043,
        0x0044,
        0x0045,
        0x0046,
        0x0047,
        0x0048,
        0x0049,
        0x004A,
        0x004B,
        0x004C,
        0x004D,
        0x004E,
        0x004F,
        0x0050,
        0x0051,
        0x0052,
        0x0053,
        0x0054,
        0x0055,
        0x0056,
        0x0057,
        0x0058,
        0x0059,
        0x005A,
        0x005B,
        0x005C,
        0x005D,
        0x005E,
        0x005F,
        0x0060,
        0x0061,
        0x0062,
        0x0063,
        0x0064,
        0x0065,
        0x0066,
        0x0067,
        0x0068,
        0x0069,
        0x006A,
        0x006B,
        0x006C,
        0x006D,
        0x006E,
        0x006F,
        0x0070,
        0x0071,
        0x0072,
        0x0073,
        0x0074,
        0x0075,
        0x0076,
        0x0077,
        0x0078,
        0x0079,
        0x007A,
        0x007B,
        0x007C,
        0x007D,
        0x007E,
        0x007F,
        /* x7F = DEL */
        0x00C4,
        0x00C5,
        0x00C7,
        0x00C9,
        0x00D1,
        0x00D6,
        0x00DC,
        0x00E1,
        0x00E0,
        0x00E2,
        0x00E4,
        0x00E3,
        0x00E5,
        0x00E7,
        0x00E9,
        0x00E8,
        0x00EA,
        0x00EB,
        0x00ED,
        0x00EC,
        0x00EE,
        0x00EF,
        0x00F1,
        0x00F3,
        0x00F2,
        0x00F4,
        0x00F6,
        0x00F5,
        0x00FA,
        0x00F9,
        0x00FB,
        0x00FC,
        0x2020,
        0x00B0,
        0x00A2,
        0x00A3,
        0x00A7,
        0x2022,
        0x00B6,
        0x00DF,
        0x00AE,
        0x00A9,
        0x2122,
        0x00B4,
        0x00A8,
        0x2260,
        0x00C6,
        0x00D8,
        0x221E,
        0x00B1,
        0x2264,
        0x2265,
        0x00A5,
        0x00B5,
        0x2202,
        0x2211,
        0x220F,
        0x03C0,
        0x222B,
        0x00AA,
        0x00BA,
        0x03A9,
        0x00E6,
        0x00F8,
        0x00BF,
        0x00A1,
        0x00AC,
        0x221A,
        0x0192,
        0x2248,
        0x2206,
        0x00AB,
        0x00BB,
        0x2026,
        0x00A0,
        0x00C0,
        0x00C3,
        0x00D5,
        0x0152,
        0x0153,
        0x2013,
        0x2014,
        0x201C,
        0x201D,
        0x2018,
        0x2019,
        0x00F7,
        0x25CA,
        0x00FF,
        0x0178,
        0x2044,
        0x20AC,
        0x2039,
        0x203A,
        0xFB01,
        0xFB02,
        0x2021,
        0x00B7,
        0x201A,
        0x201E,
        0x2030,
        0x00C2,
        0x00CA,
        0x00C1,
        0x00CB,
        0x00C8,
        0x00CD,
        0x00CE,
        0x00CF,
        0x00CC,
        0x00D3,
        0x00D4,
        /* xF0 = Apple Logo */
        0xF8FF,
        0x00D2,
        0x00DA,
        0x00DB,
        0x00D9,
        0x0131,
        0x02C6,
        0x02DC,
        0x00AF,
        0x02D8,
        0x02D9,
        0x02DA,
        0x00B8,
        0x02DD,
        0x02DB,
        0x02C7};

    /**
     * table to map symbol font characters to Unicode; undefined characters are mapped to 0x0000 and characters without
     * any unicode equivalent are mapped to '?'. Is this appropriate?
     */
    private static final int[] SYMBOL2UNICODE = {
        0x0000,
        0x0001,
        0x0002,
        0x0003,
        0x0004,
        0x0005,
        0x0006,
        0x0007,
        0x0008,
        0x0009,
        0x000A,
        0x000B,
        0x000C,
        0x000D,
        0x000E,
        0x000F,

        0x0010,
        0x0011,
        0x0012,
        0x0013,
        0x0014,
        0x0015,
        0x0016,
        0x0017,
        0x0018,
        0x0019,
        0x001A,
        0x001B,
        0x001C,
        0x001D,
        0x001E,
        0x001F,

        0x0020,
        0x0021,
        0x2200,
        0x0023,
        0x2203,
        0x0025,
        0x0026,
        0x220D,
        0x0028,
        0x0029,
        0x2217,
        0x002B,
        0x002C,
        0x2212,
        0x002E,
        0x002F,

        0x0030,
        0x0031,
        0x0032,
        0x0033,
        0x0034,
        0x0035,
        0x0036,
        0x0037,
        0x0038,
        0x0039,
        0x003A,
        0x003B,
        0x003C,
        0x003D,
        0x003E,
        0x003F,

        0x2245,
        0x0391,
        0x0392,
        0x03A7,
        0x0394,
        0x0395,
        0x03A6,
        0x0393,
        0x0397,
        0x0399,
        0x03D1,
        0x039A,
        0x039B,
        0x039C,
        0x039D,
        0x039F,

        0x03A0,
        0x0398,
        0x03A1,
        0x03A3,
        0x03A4,
        0x03A5,
        0x03C2,
        0x03A9,
        0x039E,
        0x03A8,
        0x0396,
        0x005B,
        0x2234,
        0x005D,
        0x22A5,
        0x005F,

        0x00AF,
        0x03B1,
        0x03B2,
        0x03C7,
        0x03B4,
        0x03B5,
        0x03C6,
        0x03B3,
        0x03B7,
        0x03B9,
        0x03D5,
        0x03BA,
        0x03BB,
        0x03BC,
        0x03BD,
        0x03BF,

        0x03C0,
        0x03B8,
        0x03C1,
        0x03C3,
        0x03C4,
        0x03C5,
        0x03D6,
        0x03C9,
        0x03BE,
        0x03C8,
        0x03B6,
        0x007B,
        0x007C,
        0x007D,
        0x223C,
        0x003F,

        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,

        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,
        0x0000,

        0x00A0,
        0x03D2,
        0x2032,
        0x2264,
        0x2044,
        0x221E,
        0x0192,
        0x2663,
        0x2666,
        0x2665,
        0x2660,
        0x2194,
        0x2190,
        0x2191,
        0x2192,
        0x2193,

        0x00B0,
        0x00B1,
        0x2033,
        0x2265,
        0x00D7,
        0x221D,
        0x2202,
        0x00B7,
        0x00F7,
        0x2260,
        0x2261,
        0x2248,
        0x2026,
        0x003F,
        0x003F,
        0x21B5,

        0x2135,
        0x2111,
        0x211C,
        0x2118,
        0x2297,
        0x2295,
        0x2205,
        0x2229,
        0x222A,
        0x2283,
        0x2287,
        0x2284,
        0x2282,
        0x2286,
        0x2208,
        0x2209,

        0x2220,
        0x2207,
        0x00AE,
        0x00A9,
        0x2122,
        0x220F,
        0x221A,
        0x22C5,
        0x00AC,
        0x2227,
        0x2228,
        0x21D4,
        0x21D0,
        0x21D1,
        0x21D2,
        0x21D3,

        0x25CA,
        0x2329,
        0x00AE,
        0x00A9,
        0x2122,
        0x2211,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,

        0x20AC,
        0x232A,
        0x222B,
        0x2320,
        0x003F,
        0x2321,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F,
        0x003F};

    /**
     * Array of valid UTF8 sequences.
     */
    private static final ValidUTF8Sequence[] VALID_UTF8 = {
        new ValidUTF8Sequence(0x0000, 0x007F, 1, new char[]{0x00, 0x7F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
        new ValidUTF8Sequence(0x0080, 0x07FF, 2, new char[]{0xC2, 0xDF, 0x80, 0xBF, 0x00, 0x00, 0x00, 0x00}),
        new ValidUTF8Sequence(0x0800, 0x0FFF, 3, new char[]{0xE0, 0xE0, 0xA0, 0xBF, 0x80, 0xBF, 0x00, 0x00}),
        new ValidUTF8Sequence(0x1000, 0xFFFF, 3, new char[]{0xE1, 0xEF, 0x80, 0xBF, 0x80, 0xBF, 0x00, 0x00}),
        new ValidUTF8Sequence(0x10000, 0x3FFFF, 4, new char[]{0xF0, 0xF0, 0x90, 0xBF, 0x80, 0xBF, 0x80, 0xBF}),
        new ValidUTF8Sequence(0x40000, 0xFFFFF, 4, new char[]{0xF1, 0xF3, 0x80, 0xBF, 0x80, 0xBF, 0x80, 0xBF}),
        new ValidUTF8Sequence(0x100000, 0x10FFFF, 4, new char[]{0xF4, 0xF4, 0x80, 0x8F, 0x80, 0xBF, 0x80, 0xBF})};

    /**
     * number of valid utf8 sequances.
     */
    private static final int NUM_UTF8_SEQUENCES = VALID_UTF8.length;

    /**
     * utf16 low surrogate.
     */
    private static final int LOW_UTF16_SURROGATE = 0xD800;

    /**
     * utf16 high surrogate.
     */
    private static final int HIGH_UTF16_SURROGATE = 0xDFFF;

    /**
     * Utf 8 bye swap: invalid char.
     */
    private static final int UTF8_BYTE_SWAP_NOT_A_CHAR = 0xFFFE;

    /**
     * Utf 8 invalid char.
     */
    private static final int UTF8_NOT_A_CHAR = 0xFFFF;

    /**
     * max utf8 valid char value.
     */
    private static final int MAX_UTF8_FROM_UCS4 = 0x10FFFF;

    private static final int[] OFFSET_UTF8_SEQUENCES = {0, // 1 byte
        1, // 2 bytes
        2, // 3 bytes
        4, // 4 bytes
        NUM_UTF8_SEQUENCES}; // must be last

    /**
     * needed for error reporting.
     */
    private Lexer lexer;

    /**
     * character buffer.
     */
    private int[] charbuf = new int[CHARBUF_SIZE];

    /**
     * actual position in buffer.
     */
    private int bufpos;

    /**
     * Private unget buffer for the raw bytes read from the input stream. Normally this will only be used by the UTF-8
     * decoder to resynchronize the input stream after finding an illegal UTF-8 sequences. But it can be used for other
     * purposes when reading bytes in ReadCharFromStream.
     */
    private char[] rawBytebuf = new char[CHARBUF_SIZE];

    /**
     * actual position in rawBytebuf.
     */
    private int rawBufpos;

    /**
     * has a raw byte been pushed into stack?
     */
    private boolean rawPushed;

    /**
     * looking for an UTF BOM?
     */
    private boolean lookingForBOM = true;

    /**
     * has end of stream been reached?
     */
    private boolean endOfStream;

    private boolean pushed;

    private int tabs;

    /**
     * tab size in chars.
     */
    private int tabsize;

    /**
     * FSM for ISO2022.
     */
    private int state;

    private int c;

    /**
     * Encoding.
     */
    private int encoding;

    /**
     * current column number.
     */
    private int curcol;

    private int lastcol;

    /**
     * current line number.
     */
    private int curline;

    /**
     * input stream.
     */
    private InputStream stream;

    /**
     * Instatiates a new StreamInImpl.
     * @param stream input stream
     * @param encoding encoding constant
     * @param tabsize tab size
     */
    public StreamInImpl(InputStream stream, int encoding, int tabsize)
    {
        this.stream = stream;
        this.charbuf[0] = '\0';
        this.tabsize = tabsize;
        this.curline = 1;
        this.curcol = 1;
        this.encoding = encoding;
        this.state = FSM_ASCII;
    }

    /**
     * @see org.w3c.tidy.StreamIn#getCurcol()
     */
    public int getCurcol()
    {
        return this.curcol;
    }

    /**
     * @see org.w3c.tidy.StreamIn#getCurline()
     */
    public int getCurline()
    {
        return this.curline;
    }

    /**
     * Setter for <code>lexer</code>.
     * @param lexer The lexer to set.
     */
    public void setLexer(Lexer lexer)
    {
        this.lexer = lexer;
    }

    /**
     * @see org.w3c.tidy.StreamIn#getEncoding()
     */
    public int getEncoding()
    {
        return this.encoding;
    }

    /**
     * @see org.w3c.tidy.StreamIn#readChar()
     */
    public int readChar()
    {
        int c;

        if (this.pushed)
        {
            c = this.charbuf[--(this.bufpos)];
            if ((this.bufpos) == 0)
            {
                this.pushed = false;
            }

            if (c == '\n')
            {
                this.curcol = 1;
                this.curline++;
                return c;
            }

            this.curcol++;
            return c;
        }

        this.lastcol = this.curcol;

        if (this.tabs > 0)
        {
            this.curcol++;
            this.tabs--;
            return ' ';
        }

        for (;;)
        {
            c = readCharFromStream();

            if (c < 0)
            {
                return END_OF_STREAM;
            }

            if (c == '\n')
            {
                this.curcol = 1;
                this.curline++;
                break;
            }

            // #427663 - map '\r' to '\n' - Andy Quick 11 Aug 00
            if (c == '\r')
            {
                c = readCharFromStream();
                if (c != '\n')
                {
                    if (c != END_OF_STREAM) // EOF fix by Terry Teague 12 Aug 01
                    {
                        ungetChar(c);
                    }
                    c = '\n';
                }
                this.curcol = 1;
                this.curline++;
                break;
            }

            if (c == '\t')
            {
                this.tabs = this.tabsize - ((this.curcol - 1) % this.tabsize) - 1;
                this.curcol++;
                c = ' ';
                break;
            }

            // strip control characters, except for Esc
            if (c == '\033')
            {
                break;
            }

            if (0 < c && c < 32)
            {
                continue;
            }

            // watch out for chars that have already been decoded such as
            // IS02022, UTF-8 etc, that don't require further decoding
            if (this.encoding == Configuration.RAW
                || this.encoding == Configuration.ISO2022
                || this.encoding == Configuration.UTF8
                || this.encoding == Configuration.SHIFTJIS // #431953 - RJ
                || this.encoding == Configuration.BIG5) // #431953 - RJ
            {
                this.curcol++;
                break;
            }

            if (this.encoding == Configuration.MACROMAN)
            {
                c = decodeMacRoman(c);
            }

            // produced e.g. as a side-effect of smart quotes in Word
            if (127 < c && c < 160)
            {
                this.lexer.report.encodingError(this.lexer, Report.WINDOWS_CHARS, c);

                c = decodeWin1252(c);

                if (c == 0)
                {
                    continue;
                }
            }

            this.curcol++;
            break;
        }

        return c;
    }

    /**
     * @see org.w3c.tidy.StreamIn#ungetChar(int)
     */
    public void ungetChar(int c)
    {
        this.pushed = true;
        if (this.bufpos >= CHARBUF_SIZE)
        {
            // pop last element
            for (int j = 1; j < CHARBUF_SIZE; j++)
            {
                this.charbuf[j - 1] = this.charbuf[j];
            }
            (this.bufpos)--;
        }
        this.charbuf[(this.bufpos)++] = c;

        if (c == '\n')
        {
            --this.curline;
        }

        this.curcol = this.lastcol;
    }

    /**
     * @see org.w3c.tidy.StreamIn#isEndOfStream()
     */
    public boolean isEndOfStream()
    {
        return this.endOfStream;
    }

    /**
     * Function for conversion from Windows-1252 to Unicode.
     * @param c char to decode
     * @return decoded char
     */
    protected static int decodeWin1252(int c)
    {
        return WIN2UNICODE[c - 128];
    }

    /**
     * Function to convert from MacRoman to Unicode.
     * @param c char to decode
     * @return decoded char
     */
    protected static int decodeMacRoman(int c)
    {
        return MAC2UNICODE[c];
    }

    /**
     * Function to convert from Symbol Font chars to Unicode.
     * @param c char to decode
     * @return decoded char
     */
    int decodeSymbolFont(int c)
    {
        if (c > 255)
        {
            return c;
        }

        return SYMBOL2UNICODE[c];
    }

    /**
     * @see org.w3c.tidy.StreamIn#readCharFromStream()
     */
    public int readCharFromStream()
    {
        int c;
        int[] n = new int[]{0};
        char[] tempchar = new char[1];
        int[] count = new int[]{1};

        readRawBytesFromStream(tempchar, count, false);
        if (count[0] <= 0)
        {
            return END_OF_STREAM;
        }

        c = tempchar[0];

        if (lookingForBOM
            && (this.encoding == Configuration.UTF16
                || this.encoding == Configuration.UTF16LE
                || this.encoding == Configuration.UTF16BE || this.encoding == Configuration.UTF8))
        {
            // check for a Byte Order Mark
            int c1, bom;

            lookingForBOM = false;

            if (c == END_OF_STREAM)
            {
                lookingForBOM = false;
                return END_OF_STREAM;
            }

            count[0] = 1;
            readRawBytesFromStream(tempchar, count, false);
            c1 = tempchar[0];

            bom = (c << 8) + c1;

            if (bom == UNICODE_BOM_BE)
            {
                // big-endian UTF-16
                if (this.encoding != Configuration.UTF16 && this.encoding != Configuration.UTF16BE)
                {
                    this.lexer.report.encodingError(this.lexer, Report.ENCODING_MISMATCH, Configuration.UTF16BE);
                    // non-fatal error
                }
                this.encoding = Configuration.UTF16BE;
                this.lexer.configuration.inCharEncoding = Configuration.UTF16BE;

                return UNICODE_BOM; // return decoded BOM
            }
            else if (bom == UNICODE_BOM_LE)
            {
                // little-endian UTF-16
                if (this.encoding != Configuration.UTF16 && this.encoding != Configuration.UTF16LE)
                {
                    this.lexer.report.encodingError(this.lexer, Report.ENCODING_MISMATCH, Configuration.UTF16LE);
                    // non-fatal error
                }
                this.encoding = Configuration.UTF16LE;

                this.lexer.configuration.inCharEncoding = Configuration.UTF16LE;

                return UNICODE_BOM; // return decoded BOM
            }
            else
            {
                int c2;

                count[0] = 1;
                readRawBytesFromStream(tempchar, count, false);
                c2 = tempchar[0];

                if (((c << 16) + (c1 << 8) + c2) == UNICODE_BOM_UTF8)
                {
                    // UTF-8
                    this.encoding = Configuration.UTF8;
                    if (this.encoding != Configuration.UTF8)
                    {
                        this.lexer.report.encodingError(this.lexer, Report.ENCODING_MISMATCH, Configuration.UTF8);
                        // non-fatal error
                    }
                    this.lexer.configuration.inCharEncoding = Configuration.UTF8;

                    return UNICODE_BOM; // return decoded BOM
                }
                else
                {
                    // the 2nd and/or 3rd bytes weren't what we were expecting, so unget the extra 2 bytes
                    rawPushed = true;

                    if ((rawBufpos + 1) >= CHARBUF_SIZE)
                    {
                        System.arraycopy(rawBytebuf, 2, rawBytebuf, 0, CHARBUF_SIZE - 2);

                        rawBufpos -= 2;
                    }
                    // make sure the bytes are pushed in the right order
                    rawBytebuf[rawBufpos++] = (char) c2;
                    rawBytebuf[rawBufpos++] = (char) c1;

                    // drop through to code below, with the original char
                }
            }
        }

        lookingForBOM = false;

        /*
         * A document in ISO-2022 based encoding uses some ESC sequences called "designator" to switch character sets.
         * The designators defined and used in ISO-2022-JP are: "ESC" + "(" + ? for ISO646 variants "ESC" + "$" + ? and
         * "ESC" + "$" + "(" + ? for multibyte character sets Where ? stands for a single character used to indicate the
         * character set for multibyte characters. Tidy handles this by preserving the escape sequence and setting the
         * top bit of each byte for non-ascii chars. This bit is then cleared on output. The input stream keeps track of
         * the state to determine when to set/clear the bit.
         */

        if (this.encoding == Configuration.ISO2022)
        {
            if (c == 0x1b) // ESC
            {
                this.state = FSM_ESC;
                return c;
            }

            switch (this.state)
            {
                case FSM_ESC :
                    if (c == '$')
                    {
                        this.state = FSM_ESCD;
                    }
                    else if (c == '(')
                    {
                        this.state = FSM_ESCP;
                    }
                    else
                    {
                        this.state = FSM_ASCII;
                    }
                    break;

                case FSM_ESCD :
                    if (c == '(')
                    {
                        this.state = FSM_ESCDP;
                    }
                    else
                    {
                        this.state = FSM_NONASCII;
                    }
                    break;

                case FSM_ESCDP :
                    this.state = FSM_NONASCII;
                    break;

                case FSM_ESCP :
                    this.state = FSM_ASCII;
                    break;

                case FSM_NONASCII :
                    c |= 0x80;
                    break;

                default :
                    // 
                    break;
            }

            return c;
        }

        if (this.encoding == Configuration.UTF16LE)
        {
            int c1;

            count[0] = 1;
            readRawBytesFromStream(tempchar, count, false);
            if (count[0] <= 0)
            {
                return END_OF_STREAM;
            }
            c1 = tempchar[0];

            n[0] = (c1 << 8) + c;

            return n[0];
        }

        // UTF-16 is big-endian by default
        if ((this.encoding == Configuration.UTF16) || (this.encoding == Configuration.UTF16BE))
        {
            int c1;

            count[0] = 1;
            readRawBytesFromStream(tempchar, count, false);
            if (count[0] <= 0)
            {
                return END_OF_STREAM;
            }
            c1 = tempchar[0];

            n[0] = (c << 8) + c1;

            return n[0];
        }

        if (this.encoding == Configuration.UTF8)
        {
            // deal with UTF-8 encoded char

            int err = 0;
            int[] count2 = new int[]{0};

            // first byte "c" is passed in separately
            err = decodeUTF8BytesToChar(n, c, (char) 0, new boolean[]{true}, count2);
            if (!TidyUtils.toBoolean(err) && (n[0] == END_OF_STREAM) && (count2[0] == 1)) /* EOF */
            {
                return END_OF_STREAM;
            }
            else if (TidyUtils.toBoolean(err))
            {
                /* set error position just before offending character */
                this.lexer.lines = this.curline;
                this.lexer.columns = this.curcol;

                this.lexer.report.encodingError(this.lexer, (short) (Report.INVALID_UTF8 | Report.REPLACED_CHAR), n[0]);
                n[0] = 0xFFFD; /* replacement char */
            }

            return n[0];
        }

        // #431953 - start RJ
        /*
         * This section is suitable for any "multibyte" variable-width character encoding in which a one-byte code is
         * less than 128, and the first byte of a two-byte code is greater or equal to 128. Note that Big5 and ShiftJIS
         * fit into this kind, even though their second byte may be less than 128
         */
        if ((this.encoding == Configuration.BIG5) || (this.encoding == Configuration.SHIFTJIS))
        {
            if (c < 128)
            {
                return c;
            }
            else
            {
                int c1;
                count[0] = 1;
                readRawBytesFromStream(tempchar, count, false);

                if (count[0] <= 0)
                {
                    return END_OF_STREAM;
                }

                c1 = tempchar[0];
                n[0] = (c << 8) + c1;
                return n[0];
            }
        }
        // #431953 - end RJ
        else
        {
            n[0] = c;
        }

        return n[0];
    }

    int decodeUTF8BytesToChar(int[] c, int firstByte, char successorBytes, boolean[] usegetter, int[] count)
    {
        char[] buf = new char[10];

        int ch = 0; //uint
        int n = 0; //uint
        int i, bytes = 0;
        boolean hasError = false;

        if (successorBytes != 0)
        {
            buf[0] = successorBytes;
        }

        // special check if we have been passed an EOF char
        if (firstByte == END_OF_STREAM) //uint
        {
            // at present
            c[0] = firstByte;
            count[0] = 1;
            return 0;
        }

        ch = firstByte; // first byte is passed in separately

        if (ch <= 0x7F) // 0XXX XXXX one byte
        {
            n = ch;
            bytes = 1;
        }
        else if ((ch & 0xE0) == 0xC0) /* 110X XXXX two bytes */
        {
            n = ch & 31;
            bytes = 2;
        }
        else if ((ch & 0xF0) == 0xE0) /* 1110 XXXX three bytes */
        {
            n = ch & 15;
            bytes = 3;
        }
        else if ((ch & 0xF8) == 0xF0) /* 1111 0XXX four bytes */
        {
            n = ch & 7;
            bytes = 4;
        }
        else if ((ch & 0xFC) == 0xF8) /* 1111 10XX five bytes */
        {
            n = ch & 3;
            bytes = 5;
            hasError = true;
        }
        else if ((ch & 0xFE) == 0xFC) /* 1111 110X six bytes */
        {
            n = ch & 1;
            bytes = 6;
            hasError = true;
        }
        else
        {
            // not a valid first byte of a UTF-8 sequence
            n = ch;
            bytes = 1;
            hasError = true;
        }

        for (i = 1; i < bytes; ++i)
        {
            int[] tempCount = new int[1]; // no. of additional bytes to get

            // successor bytes should have the form 10XX XXXX
            if (usegetter != null && (bytes - i > 0))
            {
                tempCount[0] = 1; // to simplify things, get 1 byte at a time
                char[] buftocopy = new char[]{buf[i - 1]};

                readRawBytesFromStream(buftocopy, tempCount, false);
                if (tempCount[0] <= 0) // EOF
                {
                    hasError = true;
                    bytes = i;
                    break;
                }
            }

            if ((buf[i - 1] & 0xC0) != 0x80)
            {
                // illegal successor byte value
                hasError = true;
                bytes = i;
                if (usegetter[0])
                {
                    char[] buftocopy = new char[]{buf[i - 1]};
                    tempCount[0] = 1; // to simplify things, unget 1 byte at a time
                    readRawBytesFromStream(buftocopy, tempCount, true); // Unget the byte
                }
                break;
            }

            n = (n << 6) | (buf[i - 1] & 0x3F);
        }

        if (!hasError && ((n == UTF8_BYTE_SWAP_NOT_A_CHAR) || (n == UTF8_NOT_A_CHAR)))
        {
            hasError = true;
        }

        if (!hasError && (n > MAX_UTF8_FROM_UCS4))
        {
            hasError = true;
        }

        if (!hasError && (n >= LOW_UTF16_SURROGATE) && (n <= HIGH_UTF16_SURROGATE))
        {
            // unpaired surrogates not allowed
            hasError = true;
        }

        if (!hasError)
        {
            int lo = OFFSET_UTF8_SEQUENCES[bytes - 1];
            int hi = OFFSET_UTF8_SEQUENCES[bytes] - 1;

            // check for overlong sequences
            if ((n < VALID_UTF8[lo].lowChar) || (n > VALID_UTF8[hi].highChar))
            {
                hasError = true;
            }
            else
            {
                hasError = true; // assume error until proven otherwise

                for (i = lo; i <= hi; i++)
                {
                    int tempCount;
                    char theByte; //unsigned

                    for (tempCount = 0; tempCount < bytes; tempCount++)
                    {
                        if (!TidyUtils.toBoolean(tempCount))
                        {
                            theByte = (char) firstByte;
                        }
                        else
                        {
                            theByte = buf[tempCount - 1];
                        }
                        if ((theByte >= VALID_UTF8[i].validBytes[(tempCount * 2)])
                            && (theByte <= VALID_UTF8[i].validBytes[(tempCount * 2) + 1]))
                        {
                            hasError = false;
                        }
                        if (hasError)
                        {
                            break;
                        }
                    }
                }
            }
        }

        count[0] = bytes;

        c[0] = n;

        if (hasError)
        {
            // n = 0xFFFD;
            // replacement char - do this in the caller
            return -1;
        }

        return 0;
    }

    int encodeCharToUTF8Bytes(int c, int[] buf, Out out, boolean[] usePutter, int count)
    {
        int bytes = 0;
        boolean hasError = false;
        if (c <= 0x7F) // 0XXX XXXX one byte
        {
            buf[0] = c;
            bytes = 1;
        }
        else if (c <= 0x7FF) // 110X XXXX two bytes
        {
            buf[0] = (0xC0 | (c >> 6));
            buf[1] = (0x80 | (c & 0x3F));
            bytes = 2;
        }
        else if (c <= 0xFFFF) // 1110 XXXX three bytes
        {
            buf[0] = (0xE0 | (c >> 12));
            buf[1] = (0x80 | ((c >> 6) & 0x3F));
            buf[2] = (0x80 | (c & 0x3F));
            bytes = 3;
            if ((c == UTF8_BYTE_SWAP_NOT_A_CHAR) || (c == UTF8_NOT_A_CHAR))
            {
                hasError = true;
            }
            else if ((c >= LOW_UTF16_SURROGATE) && (c <= HIGH_UTF16_SURROGATE))
            {
                // unpaired surrogates not allowed
                hasError = true;
            }
        }
        else if (c <= 0x1FFFFF) // 1111 0XXX four bytes
        {
            buf[0] = (0xF0 | (c >> 18));
            buf[1] = (0x80 | ((c >> 12) & 0x3F));
            buf[2] = (0x80 | ((c >> 6) & 0x3F));
            buf[3] = (0x80 | (c & 0x3F));
            bytes = 4;
            if (c > MAX_UTF8_FROM_UCS4)
            {
                hasError = true;
            }
        }
        else if (c <= 0x3FFFFFF) // 1111 10XX five bytes
        {
            buf[0] = (0xF8 | (c >> 24));
            buf[1] = (0x80 | (c >> 18));
            buf[2] = (0x80 | ((c >> 12) & 0x3F));
            buf[3] = (0x80 | ((c >> 6) & 0x3F));
            buf[4] = (0x80 | (c & 0x3F));
            bytes = 5;
            hasError = true;
        }
        else if (c <= 0x7FFFFFFF) // 1111 110X six bytes
        {
            buf[0] = (0xFC | (c >> 30));
            buf[1] = (0x80 | ((c >> 24) & 0x3F));
            buf[2] = (0x80 | ((c >> 18) & 0x3F));
            buf[3] = (0x80 | ((c >> 12) & 0x3F));
            buf[4] = (0x80 | ((c >> 6) & 0x3F));
            buf[5] = (0x80 | (c & 0x3F));
            bytes = 6;
            hasError = true;
        }
        else
        {
            hasError = true;
        }

        if (hasError)
        {
            usePutter[0] = false; // don't output invalid UTF-8 byte sequence to a stream
        }

        if (usePutter[0])
        {
            int[] tempCount = new int[1];

            tempCount[0] = bytes;

            outcUTF8Bytes(out, buf, tempCount);
            if (tempCount[0] < bytes)
            {
                hasError = true;
            }
        }

        count = bytes;

        if (hasError)
        {
            return -1;
        }

        return 0;
    }

    /**
     * output UTF-8 bytes to output stream.
     */
    void outcUTF8Bytes(Out out, int[] buf, int[] count)
    {
        for (int i = 0; i < count[0]; i++)
        {
            out.outc(buf[i]);
        }
    }

    /**
     * Read raw bytes from stream, return <= 0 if EOF; or if "unget" is true, Unget the bytes to re-synchronize the
     * input stream Normally UTF-8 successor bytes are read using this routine.
     * @param buf character buffer
     * @param count number of bytes to read
     * @param unget
     */
    private void readRawBytesFromStream(char[] buf, int[] count, boolean unget)
    {
        int i;

        try
        {
            for (i = 0; i < count[0]; i++)
            {
                if (unget)
                {

                    c = this.stream.read();

                    // should never get here; testing for 0xFF, a valid char, is not a good idea
                    if (c == END_OF_STREAM) // || buf[i] == (unsigned char)EndOfStream
                    {
                        count[0] = -i;
                        return;
                    }

                    rawPushed = true;

                    if (rawBufpos >= CHARBUF_SIZE)
                    {
                        System.arraycopy(rawBytebuf, 1, rawBytebuf, 0, CHARBUF_SIZE - 1);
                        rawBufpos--;
                    }
                    rawBytebuf[rawBufpos++] = buf[i];

                    if (buf[i] == '\n')
                    {
                        --(this.curline);
                    }

                    this.curcol = this.lastcol;
                }
                else
                {
                    if (rawPushed)
                    {
                        buf[i] = rawBytebuf[--rawBufpos];
                        if (rawBufpos == 0)
                        {
                            rawPushed = false;
                        }

                        if (buf[i] == '\n')
                        {
                            this.curcol = 1;
                            this.curline++;
                        }
                        else
                        {
                            this.curcol++;
                        }
                    }
                    else
                    {
                        int c = this.stream.read();
                        if (c == END_OF_STREAM)
                        {
                            count[0] = -i;
                            break;
                        }
                        else
                        {
                            buf[i] = (char) c;
                            this.curcol++;
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("StreamInImpl.readRawBytesFromStream: " + e.toString());
        }
        return;
    }

}