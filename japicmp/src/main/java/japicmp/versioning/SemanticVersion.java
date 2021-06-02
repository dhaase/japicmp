package japicmp.versioning;

import japicmp.util.Optional;

public class SemanticVersion {
	private final int major;
	private final int minor;
	private final int patch;

	public SemanticVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SemanticVersion that = (SemanticVersion) o;
		return major == that.major && minor == that.minor && patch == that.patch;
	}

	public Optional<ChangeType> computeChangeType(SemanticVersion version) {
		if (this.major != version.major) {
			return Optional.of(ChangeType.MAJOR);
		}
		if (this.minor != version.minor) {
			return Optional.of(ChangeType.MINOR);
		}
		if (this.patch != version.patch) {
			return Optional.of(ChangeType.PATCH);
		}
		return Optional.of(ChangeType.UNCHANGED);
	}

	@Override
	public int hashCode() {
		int result = major;
		result = 31 * result + minor;
		result = 31 * result + patch;
		return result;
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}

	public SemanticVersion increment(ChangeType type) {
		if (ChangeType.UNCHANGED == type) {
			return this;
		}
		int newMajor = this.major;
		int newMinor = this.minor;
		int newPatch = this.patch;
		if (ChangeType.MAJOR == type) {
			newMajor = this.major + 1;
			newMinor = 0;
			newPatch = 0;
		} else if (ChangeType.MINOR == type) {
			newMinor = this.minor + 1;
			newPatch = 0;
		} else if (ChangeType.PATCH == type) {
			newPatch = this.patch + 1;
		}
		return new SemanticVersion(newMajor, newMinor, newPatch);
	}

	public enum ChangeType {
		MAJOR(3),
		MINOR(2),
		PATCH(1),
		UNCHANGED(0);

		private final int rank;

		ChangeType(int rank) {
			this.rank = rank;
		}

		public int getRank() {
			return rank;
		}
	}
}
