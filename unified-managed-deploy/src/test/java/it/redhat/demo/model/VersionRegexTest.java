package it.redhat.demo.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class VersionRegexTest {
	
	private Pattern pattern;
	
	@Before
	public void before() {
		pattern = Pattern.compile(MavenGavInfo.VERSION_REGEX);
	}
	
	@Test
	public void test_matcher_group_no_snapshot() {
		Matcher matcher = pattern.matcher("12.0.12");
		assertTrue(matcher.matches());
		assertEquals("12", matcher.group(1));
		assertEquals("0", matcher.group(2));
		assertEquals("12", matcher.group(3));
		assertNull(matcher.group(4));
	}
	
	@Test
	public void test_matcher_group_no_snapshot_2() {
		Matcher matcher = pattern.matcher("1.0.21");
		assertTrue(matcher.matches());
		assertEquals("1", matcher.group(1));
		assertEquals("0", matcher.group(2));
		assertEquals("21", matcher.group(3));
		assertNull(matcher.group(4));
	}
	
	@Test
	public void test_matcher_group_snapshot() {
		Matcher matcher = pattern.matcher("12.0.12-SNAPSHOT");
		assertTrue(matcher.matches());
		assertEquals("12", matcher.group(1));
		assertEquals("0", matcher.group(2));
		assertEquals("12", matcher.group(3));
		assertEquals("-SNAPSHOT", matcher.group(4));
	}
	
	@Test
	public void test_matcher_group_error() {
		Matcher matcher = pattern.matcher("12.0.12-SN");
		assertFalse(matcher.matches());
	}

}
