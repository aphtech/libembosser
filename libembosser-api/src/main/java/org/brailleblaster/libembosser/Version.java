package org.brailleblaster.libembosser;

/**
 * A class to represent API versions.
 * 
 * This class is used to help determine the API version used within the SPI
 * interfaces but also for driver implementations to identify what version of
 * the API they implement. The API version will be of the form Major.Minor. It
 * is not guaranteed that the SPI interfaces from a different major version to a
 * driver implementation will work together as methods may have been added,
 * removed, renamed, etc. For an SPI and driver implementation of the same major
 * version but differing minor versions they may work together. In newer minor
 * versions there will only be additions to the API, which means that where the
 * minor version differs then the components may work together. Service
 * providers and driver implementations should use this version information to
 * ensure that they only call upon methods, constants, etc from objects from
 * other components.
 * <p>
 * It is worth noting that the SPI may be bundled in a JAR package with a
 * revision number. The different revision numbers have no impact upon API, the
 * changes should be restricted to bug fixes and other enhancements which do not
 * impact upon API. Therefore this class does not include the revision number as
 * this is irrelevant for ensuring components work together.
 * 
 * @version 1.0
 * 
 * @author Michael Whapples
 *
 */
public final class Version {
	private final static Version API_VERSION = new Version(1, 1);
	private int major;
	private int minor;
	/**
	 * Get the version of the SPI.
	 * 
	 * @return The version of the SPI.
	 */
	public static Version getApiVersion() {
		return API_VERSION;
	}

	/**
	 * Create a version object with the major and minor version numbers.
	 * 
	 * @param major
	 *            The major version number.
	 * @param minor
	 *            The minor version number.
	 */
	private Version(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	/**
	 * Get the major version number.
	 * 
	 * @return The major version number.
	 */
	public int getMajor() {
		return this.major;
	}

	/**
	 * Get the minor version number.
	 * 
	 * @return The minor version number.
	 */
	public int getMinor() {
		return this.minor;
	}

	@Override
	public String toString() {
		return String.format("V%d.%d", major, minor);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (major != other.major)
			return false;
		return minor == other.minor;
	}

}
