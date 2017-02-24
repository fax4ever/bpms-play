package it.redhat.demo.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenGavInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 684583322034576559L;

	public final static String VERSION_REGEX = "(\\d+).(\\d+).(\\d+)(-SNAPSHOT)?";

	public enum Affinity {
		DIFFERENT_ARTIFACT(0), DIFFERENT_MAJOR(1), DIFFERENT_MINOR(2), DIFFERNT_MINI(3), DIFFERNT_SNAPSHOT(4), EQUALS(5);

		private final int value;

		Affinity(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		
		public boolean isGreater(Affinity other) {
			return value > other.getValue();
		}
		
	}

	private String groupId;
	private String artifactId;
	private String version;
	private int majorVersion;
	private int minorVersion;
	private int miniVersion;
	private boolean snapshot;

	public MavenGavInfo(String groupId, String artifactId, String version) {

		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;

		Pattern pattern = Pattern.compile(VERSION_REGEX);
		Matcher matcher = pattern.matcher(version);
		if (!matcher.matches()) {
			throw new RuntimeException("invalid maven version: " + version);
		}
		majorVersion = Integer.parseInt(matcher.group(1));
		minorVersion = Integer.parseInt(matcher.group(2));
		miniVersion = Integer.parseInt(matcher.group(3));
		snapshot = (matcher.group(4) != null);

	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getMiniVersion() {
		return miniVersion;
	}

	public boolean isSnapshot() {
		return snapshot;
	}
	
	public Affinity affinity(MavenGavInfo other) {
		
		if (!artifactId.equals(other.getArtifactId()) || !groupId.equals(other.getGroupId())) {
			return Affinity.DIFFERENT_ARTIFACT;
		}
		
		if (majorVersion != other.getMajorVersion()) {
			return Affinity.DIFFERENT_MAJOR;
		}
		
		if (minorVersion != other.getMinorVersion()) {
			return Affinity.DIFFERENT_MINOR;
		}
		
		if (miniVersion != other.getMiniVersion()) {
			return Affinity.DIFFERNT_MINI;
		}
		
		if (snapshot != other.isSnapshot()) {
			return Affinity.DIFFERNT_SNAPSHOT;
		}
		
		return Affinity.EQUALS;
		
	}
	
	public boolean greterMini(MavenGavInfo other) {
		
		return miniVersion > other.getMiniVersion();
		
	}

	@Override
	public String toString() {
		return "MavenGavInfo [groupId=" + groupId + ", artifactId=" + artifactId + ", majorVersion=" + majorVersion
				+ ", minorVersion=" + minorVersion + ", miniVersion=" + miniVersion + ", snapshot=" + snapshot + "]";
	}
	
	public String getGav() {
		return groupId + ":" + artifactId + ":" + version;
	}

}
