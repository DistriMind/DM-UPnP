/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package fr.distrimind.oss.upnp.common.util.io;



import fr.distrimind.oss.upnp.common.model.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class IO {

	public static String makeRelativePath(String path, String base) {
		String p = "";
		if (path != null && !path.isEmpty()) {
			if (path.startsWith("/")) {
				if (path.startsWith(base)) {
					p = path.substring(base.length());
				} else {
					p = base + path;
				}
			} else {
				p = path.endsWith("/")
						? path.substring(0, path.length() - 1)
						: path;
			}
			if (p.startsWith("/")) p = p.substring(1);
		}
		return p;
	}

	public static void recursiveRename(File dir, String from, String to) {
		File[] subfiles = dir.listFiles();
		if (subfiles==null)
			return;
		for (File file : subfiles) {
			File f = new File(dir, file.getName().replace(from, to));
			if (file.isDirectory()) {
				recursiveRename(file, from, to);
				file.renameTo(f);
			} else {
				file.renameTo(f);
			}
		}
	}

	public static void findFiles(File file, FileFinder finder) {
		finder.found(file);
		File[] children = file.listFiles();
		if (children != null)
			for (File child : children) {
				findFiles(child, finder);
			}
	}

	@FunctionalInterface
	public interface FileFinder {
		void found(File file);
	}

	public static boolean deleteFile(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files != null)
				for (File file : files) {
					if (file.isDirectory()) {
						deleteFile(file);
					} else {
						file.delete();
					}
				}
		}
		return (path.delete());
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		try (FileInputStream fis1=new FileInputStream(sourceFile); FileChannel source = fis1.getChannel(); FileOutputStream fis2=new FileOutputStream(destFile); FileChannel destination = fis2.getChannel()) {
			destination.transferFrom(source, 0, source.size());
		}
	}

	/* ################################################################################################### */

	public static byte[] readBytes(InputStream is) throws IOException {
		return toByteArray(is);
	}

	public static byte[] readBytes(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return readBytes(is);
		}
	}

	public static void writeBytes(OutputStream outputStream, byte[] data) throws IOException {
		write(data, outputStream);
	}

	public static void writeBytes(File file, byte[] data) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("File does not exist: " + file);
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + file);
		}
		if (!file.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + file);
		}

		try (OutputStream os = new FileOutputStream(file)) {
			writeBytes(os, data);
			os.flush();
		}
	}

	public static void writeUTF8(OutputStream outputStream, String data) throws IOException {
		write(data, outputStream, StandardCharsets.UTF_8);
	}
	public static void writeUTF8(WritableByteChannel writableByteChannel, String data) throws IOException {
		write(data, writableByteChannel, StandardCharsets.UTF_8);
	}

	public static void writeUTF8(File file, String contents) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("File does not exist: " + file);
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + file);
		}
		if (!file.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + file);
		}

		try (OutputStream os = new FileOutputStream(file)) {
			writeUTF8(os, contents);
			os.flush();
		}
	}

	@SuppressWarnings("PMD.CloseResource")
	public static String readLines(InputStream is) throws IOException {
		if (is == null) throw new IllegalArgumentException("Inputstream was null");

		BufferedReader inputReader;
		inputReader = new BufferedReader(
				new InputStreamReader(is)
		);

		StringBuilder input = new StringBuilder();
		String inputLine;
		while ((inputLine = inputReader.readLine()) != null) {
			input.append(inputLine).append(System.lineSeparator());
		}

		return input.length() > 0 ? input.toString() : "";
	}

	public static String readLines(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return readLines(is);
		}
	}

	public static List<String> readLines(File file, boolean trimLines) throws IOException {
		return readLines(file, trimLines, null);
	}

	public static List<String> readLines(File file, boolean trimLines, Character commentChar) throws IOException {
		return readLines(file, trimLines, commentChar, false);
	}

	public static List<String> readLines(File file, boolean trimLines, Character commentChar, boolean skipEmptyLines) throws IOException {
		List<String> contents = new ArrayList<>();
		try (BufferedReader input = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = input.readLine()) != null) {
				if (commentChar != null && line.matches("^\\s*" + commentChar + ".*")) continue;
				String l = trimLines ? line.trim() : line;
				if (skipEmptyLines && l.isEmpty()) continue;
				contents.add(l);
			}
		}
		return contents;
	}


	/*
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements.  See the NOTICE file distributed with
	 * this work for additional information regarding copyright ownership.
	 * The ASF licenses this file to You under the Apache License, Version 2.0
	 * (the "License"); you may not use this file except in compliance with
	 * the License.  You may obtain a copy of the License at
	 *
	 *      https://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */

	/**
	 * General IO stream manipulation utilities.
	 * <p>
	 * This class provides static utility methods for input/output operations.
	 * <ul>
	 * <li>closeQuietly - these methods close a stream ignoring nulls and exceptions
	 * <li>toXxx/read - these methods read data from a stream
	 * <li>write - these methods write data to a stream
	 * <li>copy - these methods copy all the data from one stream to another
	 * <li>contentEquals - these methods compare the content of two streams
	 * </ul>
	 * <p>
	 * The byte-to-char methods and char-to-byte methods involve a conversion step.
	 * Two methods are provided in each case, one that uses the platform default
	 * encoding and the other which allows you to specify an encoding. You are
	 * encouraged to always specify an encoding because relying on the platform
	 * default can lead to unexpected results, for example when moving from
	 * development to production.
	 * <p>
	 * All the methods in this class that read a stream are buffered internally.
	 * This means that there is no cause to use a <code>BufferedInputStream</code>
	 * or <code>BufferedReader</code>. The default buffer size of 4K has been shown
	 * to be efficient in tests.
	 * <p>
	 * Wherever possible, the methods in this class do <em>not</em> flush or close
	 * the stream. This is to avoid making non-portable assumptions about the
	 * streams' origin and further use. Thus, the caller is still responsible for
	 * closing streams after use.
	 * <p>
	 * Origin of code: Excalibur.
	 *
	 */
	public static final String LINE_SEPARATOR;

	static {
		// avoid security issues
		StringWriter buf = new StringWriter(4);
		PrintWriter out = new PrintWriter(buf);
		out.println();
		LINE_SEPARATOR = buf.toString();
	}

	/**
	 * The default buffer size to use.
	 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	//-----------------------------------------------------------------------

	/**
	 * Unconditionally close an <code>Reader</code>.

	 * Equivalent to {@link java.io.Reader#close()}, except any exceptions will be ignored.
	 * This is typically used in finally blocks.
	 *
	 * @param input the Reader to close, may be null or already closed
	 */
	public static void closeQuietly(Reader input) {
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException ignored) {
			// ignore
		}
	}

	/**
	 * Unconditionally close a <code>Writer</code>.

	 * Equivalent to {@link java.io.Writer#close()}, except any exceptions will be ignored.
	 * This is typically used in finally blocks.
	 *
	 * @param output the Writer to close, may be null or already closed
	 */
	public static void closeQuietly(Writer output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException ignored) {
			// ignore
		}
	}

	/**
	 * Unconditionally close an <code>InputStream</code>.

	 * Equivalent to {@link java.io.InputStream#close()}, except any exceptions will be ignored.
	 * This is typically used in finally blocks.
	 *
	 * @param input the InputStream to close, may be null or already closed
	 */
	public static void closeQuietly(InputStream input) {
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException ignored) {
			// ignore
		}
	}

	/**
	 * Unconditionally close an <code>OutputStream</code>.

	 * Equivalent to {@link java.io.OutputStream#close()}, except any exceptions will be ignored.
	 * This is typically used in finally blocks.
	 *
	 * @param output the OutputStream to close, may be null or already closed
	 */
	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException ignored) {
			// ignore
		}
	}

	// read toByteArray
	//-----------------------------------------------------------------------

	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
	 * using the default character encoding of the platform.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input the <code>Reader</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	public static byte[] toByteArray(Reader input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="https://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input    the <code>Reader</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static byte[] toByteArray(Reader input, String encoding)
			throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output, encoding);
		return output.toByteArray();
	}

	/**
	 * Get the contents of a <code>String</code> as a <code>byte[]</code>
	 * using the default character encoding of the platform.

	 * This is the same as {@link String#getBytes()}.
	 *
	 * @param input the <code>String</code> to convert
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 *
	 * @deprecated Use {@link String#getBytes()}
	 */
	@Deprecated
	public static byte[] toByteArray(String input) {
		return input.getBytes();
	}

	// read char[]
	//-----------------------------------------------------------------------

	/**
	 * Get the contents of an <code>InputStream</code> as a character array
	 * using the default character encoding of the platform.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param is the <code>InputStream</code> to read from
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static char[] toCharArray(InputStream is) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output);
		return output.toCharArray();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a character array
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param is       the <code>InputStream</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static char[] toCharArray(InputStream is, String encoding)
			throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output, encoding);
		return output.toCharArray();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a character array.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input the <code>Reader</code> to read from
	 * @return the requested character array
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static char[] toCharArray(Reader input) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy(input, sw);
		return sw.toCharArray();
	}

	// read toString
	//-----------------------------------------------------------------------

	/**
	 * Get the contents of an <code>InputStream</code> as a String
	 * using the default character encoding of the platform.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input    the <code>InputStream</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	public static String toString(InputStream input, String encoding)
			throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding);
		return sw.toString();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a String.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input the <code>Reader</code> to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	public static String toString(Reader input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}

	/**
	 * Get the contents of a <code>byte[]</code> as a String
	 * using the default character encoding of the platform.
	 *
	 * @param input the byte array to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs (never occurs)
	 * @deprecated Use {@link String#String(byte[])}
	 */
	@Deprecated
	public static String toString(byte[] input) throws IOException {
		return new String(input);
	}

	/**
	 * Get the contents of a <code>byte[]</code> as a String
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 *
	 * @param input    the byte array to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws java.io.IOException          if an I/O error occurs (never occurs)
	 * @deprecated Use {@link String#String(byte[], String)}
	 */
	@Deprecated
	public static String toString(byte[] input, String encoding)
			throws IOException {
		if (encoding == null) {
			return new String(input);
		} else {
			return new String(input, encoding);
		}
	}

	// readLines
	//-----------------------------------------------------------------------

	//-----------------------------------------------------------------------

	/**
	 * Convert the specified string to an input stream, encoded as bytes
	 * using the default character encoding of the platform.
	 *
	 * @param input the string to convert
	 * @return an input stream
	 * @since Commons IO 1.1
	 */
	public static InputStream toInputStream(String input) {
		byte[] bytes = input.getBytes();
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * Convert the specified string to an input stream, encoded as bytes
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 *
	 * @param input    the string to convert
	 * @param encoding the encoding to use, null means platform default
	 * @return an input stream
	 * @throws java.io.IOException if the encoding is invalid
	 * @since Commons IO 1.1
	 */
	public static InputStream toInputStream(String input, String encoding) throws IOException {
		byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
		return new ByteArrayInputStream(bytes);
	}

	// write byte[]
	//-----------------------------------------------------------------------

	/**
	 * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code>.
	 *
	 * @param data   the byte array to write, do not modify during output,
	 *               null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(byte[] data, OutputStream output)
			throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
	 * using the default character encoding of the platform.

	 * This method uses {@link String#String(byte[])}.
	 *
	 * @param data   the byte array to write, do not modify during output,
	 *               null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(byte[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(new String(data));
		}
	}

	/**
	 * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
	 * using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link String#String(byte[], String)}.
	 *
	 * @param data     the byte array to write, do not modify during output,
	 *                 null ignored
	 * @param output   the <code>Writer</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(byte[] data, Writer output, String encoding)
			throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(new String(data, encoding));
			}
		}
	}

	// write char[]
	//-----------------------------------------------------------------------

	/**
	 * Writes chars from a <code>char[]</code> to a <code>Writer</code>
	 * using the default character encoding of the platform.
	 *
	 * @param data   the char array to write, do not modify during output,
	 *               null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(char[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes chars from a <code>char[]</code> to bytes on an
	 * <code>OutputStream</code>.

	 * This method uses {@link String#String(char[])} and
	 * {@link String#getBytes()}.
	 *
	 * @param data   the char array to write, do not modify during output,
	 *               null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(char[] data, OutputStream output)
			throws IOException {
		if (data != null) {
			output.write(new String(data).getBytes());
		}
	}

	/**
	 * Writes chars from a <code>char[]</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link String#String(char[])} and
	 * {@link String#getBytes(String)}.
	 *
	 * @param data     the char array to write, do not modify during output,
	 *                 null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(char[] data, OutputStream output, String encoding)
			throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(new String(data).getBytes(encoding));
			}
		}
	}

	// write String
	//-----------------------------------------------------------------------

	/**
	 * Writes chars from a <code>String</code> to a <code>Writer</code>.
	 *
	 * @param data   the <code>String</code> to write, null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(String data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform.

	 * This method uses {@link String#getBytes()}.
	 *
	 * @param data   the <code>String</code> to write, null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(String data, OutputStream output)
			throws IOException {
		if (data != null) {
			output.write(data.getBytes());
		}
	}
	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform.

	 * This method uses {@link String#getBytes()}.
	 *
	 * @param data   the <code>String</code> to write, null ignored
	 * @param output the <code>WritableByteChannel</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(String data, WritableByteChannel output)
			throws IOException {
		if (data != null) {
			output.write(ByteBuffer.wrap(data.getBytes()));
		}
	}

	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link String#getBytes(String)}.
	 *
	 * @param data     the <code>String</code> to write, null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(String data, OutputStream output, Charset encoding)
			throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(data.getBytes(encoding));
			}
		}
	}

	/**
	 * Writes chars from a <code>String</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link String#getBytes(String)}.
	 *
	 * @param data     the <code>String</code> to write, null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(String data, WritableByteChannel output, Charset encoding)
			throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(ByteBuffer.wrap(data.getBytes(encoding)));
			}
		}
	}

	// write StringBuffer
	//-----------------------------------------------------------------------

	/**
	 * Writes chars from a <code>StringBuffer</code> to a <code>Writer</code>.
	 *
	 * @param data   the <code>StringBuffer</code> to write, null ignored
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(StringBuffer data, Writer output)
			throws IOException {
		if (data != null) {
			output.write(data.toString());
		}
	}

	/**
	 * Writes chars from a <code>StringBuffer</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform.

	 * This method uses {@link String#getBytes()}.
	 *
	 * @param data   the <code>StringBuffer</code> to write, null ignored
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(StringBuffer data, OutputStream output)
			throws IOException {
		if (data != null) {
			output.write(data.toString().getBytes());
		}
	}

	/**
	 * Writes chars from a <code>StringBuffer</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link String#getBytes(String)}.
	 *
	 * @param data     the <code>StringBuffer</code> to write, null ignored
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void write(StringBuffer data, OutputStream output,
							 String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(data.toString().getBytes(encoding));
			}
		}
	}

	// copy from InputStream
	//-----------------------------------------------------------------------

	/**
	 * Copy bytes from an <code>InputStream</code> to an
	 * <code>OutputStream</code>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.

	 * Large streams (over 2GB) will return a bytes copied value of
	 * <code>-1</code> after the copy has completed since the correct
	 * number of bytes cannot be returned as an int. For large streams
	 * use the <code>copyLarge(InputStream, OutputStream)</code> method.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @throws ArithmeticException  if the byte count is too large
	 * @since Commons IO 1.1
	 */
	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
	 * <code>OutputStream</code>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.3
	 */
	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n;
		while (-1 != (n = input.read(buffer))) {

			output.write(buffer, 0, n);
			count += n;
			checkStreamLength(count);
		}
		return count;
	}
	public static void checkStreamLength(long length) throws IOException {
		if (length> Constants.MAX_INPUT_STREAM_SIZE_IN_BYTES)
			throw new IOException("Reach maximum input stream length : "+Constants.MAX_INPUT_STREAM_SIZE_IN_BYTES+" bytes");
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a
	 * <code>Writer</code> using the default character encoding of the platform.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.

	 * This method uses {@link java.io.InputStreamReader}.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(InputStream input, Writer output)
			throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a
	 * <code>Writer</code> using the specified character encoding.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * This method uses {@link java.io.InputStreamReader}.
	 *
	 * @param input    the <code>InputStream</code> to read from
	 * @param output   the <code>Writer</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(InputStream input, Writer output, String encoding)
			throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(in, output);
		}
	}

	// copy from Reader
	//-----------------------------------------------------------------------

	/**
	 * Copy chars from a <code>Reader</code> to a <code>Writer</code>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.

	 * Large streams (over 2GB) will return a chars copied value of
	 * <code>-1</code> after the copy has completed since the correct
	 * number of chars cannot be returned as an int. For large streams
	 * use the <code>copyLarge(Reader, Writer)</code> method.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @throws ArithmeticException  if the character count is too large
	 * @since Commons IO 1.1
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.3
	 */
	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
			checkReaderLength(count);
		}
		return count;
	}
	public static void checkReaderLength(long length) throws IOException {
		if (length> Constants.MAX_DESCRIPTOR_LENGTH)
			throw new IOException("Reach maximum reader length : "+Constants.MAX_DESCRIPTOR_LENGTH+" bytes");
	}

	/**
	 * Copy chars from a <code>Reader</code> to bytes on an
	 * <code>OutputStream</code> using the default character encoding of the
	 * platform, and calling flush.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.

	 * Due to the implementation of OutputStreamWriter, this method performs a
	 * flush.

	 * This method uses {@link java.io.OutputStreamWriter}.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(Reader input, OutputStream output)
			throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(output);
		copy(input, out);
		// XXX Unless anyone is planning on rewriting OutputStreamWriter, we
		// have to flush here.
		out.flush();
	}

	/**
	 * Copy chars from a <code>Reader</code> to bytes on an
	 * <code>OutputStream</code> using the specified character encoding, and
	 * calling flush.

	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.

	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.

	 * Due to the implementation of OutputStreamWriter, this method performs a
	 * flush.

	 * This method uses {@link java.io.OutputStreamWriter}.
	 *
	 * @param input    the <code>Reader</code> to read from
	 * @param output   the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if the input or output is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(Reader input, OutputStream output, String encoding)
			throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			OutputStreamWriter out = new OutputStreamWriter(output, encoding);
			copy(input, out);
			// XXX Unless anyone is planning on rewriting OutputStreamWriter,
			// we have to flush here.
			out.flush();
		}
	}

	// content equals
	//-----------------------------------------------------------------------

	/**
	 * Compare the contents of two Streams to determine if they are equal or
	 * not.

	 * This method buffers the input internally using
	 * <code>BufferedInputStream</code> if they are not already buffered.
	 *
	 * @param input1 the first stream
	 * @param input2 the second stream
	 * @return true if the content of the streams are equal or they both don't
	 *         exist, false otherwise
	 * @throws NullPointerException if either input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 */
	@SuppressWarnings("PMD.AvoidReassigningParameters")
	public static boolean contentEquals(InputStream input1, InputStream input2)
			throws IOException {
		if (!(input1 instanceof BufferedInputStream)) {
			input1 = new BufferedInputStream(input1);
		}
		if (!(input2 instanceof BufferedInputStream)) {
			input2 = new BufferedInputStream(input2);
		}
		long count=0;
		int ch = input1.read();
		while (-1 != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
			checkStreamLength(++count);
		}

		int ch2 = input2.read();
		return (ch2 == -1);
	}

	/**
	 * Compare the contents of two Readers to determine if they are equal or
	 * not.

	 * This method buffers the input internally using
	 * <code>BufferedReader</code> if they are not already buffered.
	 *
	 * @param input1 the first reader
	 * @param input2 the second reader
	 * @return true if the content of the readers are equal or they both don't
	 *         exist, false otherwise
	 * @throws NullPointerException if either input is null
	 * @throws java.io.IOException          if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	@SuppressWarnings("PMD.AvoidReassigningParameters")
	public static boolean contentEquals(Reader input1, Reader input2)
			throws IOException {
		if (!(input1 instanceof BufferedReader)) {
			input1 = new BufferedReader(input1);
		}
		if (!(input2 instanceof BufferedReader)) {
			input2 = new BufferedReader(input2);
		}

		long count=0;
		int ch = input1.read();
		while (-1 != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
			checkReaderLength(++count);
		}

		int ch2 = input2.read();
		return (ch2 == -1);
	}

}
