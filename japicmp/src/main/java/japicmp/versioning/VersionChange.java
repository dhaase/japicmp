package japicmp.versioning;

import japicmp.util.Optional;
import japicmp.exception.JApiCmpException;

import java.util.ArrayList;
import java.util.List;

public class VersionChange {
	private final List<SemanticVersion> oldVersions;
	private final List<SemanticVersion> newVersions;
	private final boolean ignoreMissingOldVersion;
	private final boolean ignoreMissingNewVersion;

	public VersionChange(List<SemanticVersion> oldVersions, List<SemanticVersion> newVersions, boolean ignoreMissingOldVersion,
						 	boolean ignoreMissingNewVersion) {
		this.oldVersions = oldVersions;
		this.newVersions = newVersions;
		this.ignoreMissingOldVersion = ignoreMissingOldVersion;
		this.ignoreMissingNewVersion = ignoreMissingNewVersion;
	}

	public Optional<ChangeTypeResult> computeChangeType() throws JApiCmpException {
		if (this.oldVersions.isEmpty()) {
			if (!ignoreMissingOldVersion) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, "Could not extract semantic version for at least one old version. Please " +
					"follow the rules for semantic versioning.");
			} else {
				return Optional.absent();
			}
		}
		if (this.newVersions.isEmpty()) {
			if (!ignoreMissingNewVersion) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, "Could not extract semantic version for at least one new version. Please " +
					"follow the rules for semantic versioning.");
			} else {
				return Optional.absent();
			}
		}
		if (allVersionsTheSame(oldVersions) && allVersionsTheSame(newVersions)) {
			SemanticVersion oldVersion = oldVersions.get(0);
			SemanticVersion newVersion = newVersions.get(0);
			Optional<SemanticVersion.ChangeType> result = oldVersion.computeChangeType(newVersion);
			return result.isPresent()
					? Optional.of(new ChangeTypeResult(result.get(), oldVersion.increment(result.get())))
					: Optional.absent();
		} else {
			if (oldVersions.size() != newVersions.size()) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, "Cannot compare versions because the number of old versions is different than the number of new versions.");
			} else {
				List<ChangeTypeResult> allChangeTypeResult = new ArrayList<>();
				for (int i=0; i<oldVersions.size(); i++) {
					SemanticVersion oldVersion = oldVersions.get(i);
					SemanticVersion newVersion = newVersions.get(i);
					Optional<SemanticVersion.ChangeType> changeTypeOptional = oldVersion.computeChangeType(newVersion);
					if (changeTypeOptional.isPresent()) {
						SemanticVersion.ChangeType changeType = changeTypeOptional.get();
						allChangeTypeResult.add(new ChangeTypeResult(changeType, oldVersion));
					}
				}
				ChangeTypeResult maxChangeTypeResult = null;
				for (ChangeTypeResult changeTypeResult : allChangeTypeResult) {
					if (maxChangeTypeResult == null) {
						maxChangeTypeResult = changeTypeResult;
					} else if (changeTypeResult.maxRank.getRank() > maxChangeTypeResult.maxRank.getRank()) {
						maxChangeTypeResult = changeTypeResult;
					}
				}
				return Optional.fromNullable(maxChangeTypeResult);
			}
		}
	}

	public static final class ChangeTypeResult {
		public final SemanticVersion.ChangeType maxRank;
		public final SemanticVersion oldVersion;

		public ChangeTypeResult(SemanticVersion.ChangeType maxRank,
								SemanticVersion oldVersion) {
			this.maxRank = maxRank;
			this.oldVersion = oldVersion;
		}
	}

	public boolean isAllMajorVersionsZero() {
		boolean allMajorVersionsZero = true;
		for (SemanticVersion semanticVersion : newVersions) {
			int major = semanticVersion.getMajor();
			if (major > 0) {
				allMajorVersionsZero = false;
				break;
			}
		}
		return allMajorVersionsZero;
	}

	private boolean allVersionsTheSame(List<SemanticVersion> versions) {
		boolean allVersionsTheSame = true;
		if (versions.size() > 1) {
			SemanticVersion firstVersion = versions.get(0);
			for (int i = 1; i < versions.size(); i++) {
				SemanticVersion version = versions.get(i);
				if (!firstVersion.equals(version)) {
					allVersionsTheSame = false;
					break;
				}
			}
		}
		return allVersionsTheSame;
	}
}
