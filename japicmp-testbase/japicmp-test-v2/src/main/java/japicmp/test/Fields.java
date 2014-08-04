package japicmp.test;

public class Fields {
	public int privateToPublicField;
	protected int privateToProtectedField;
	int privateToPackageProctedField;
	private int privateRemainsPrivateField;
	private int publicToPrivateField;
	protected int publicToProtectedField;
	int publicToPackageProtectedField;
	public int publicRemainsPublicField;
	public int staticToNonStaticField;
	public static int nonStaticToStaticField;
	public int nonStaticStaysNonStaticField;
	public static int staticRemainsStaticField;
	public int finalToNonFinalField;
	public final int nonfinalToFinalField = 0;
	public int nonfinalStaysNonFinalField;
	public final int finalRemainsFinalField = 0;
}
